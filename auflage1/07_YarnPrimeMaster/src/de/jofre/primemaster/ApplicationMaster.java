package de.jofre.primemaster;

import java.io.IOException;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.yarn.api.protocolrecords.AllocateResponse;
import org.apache.hadoop.yarn.api.records.Container;
import org.apache.hadoop.yarn.api.records.ContainerLaunchContext;
import org.apache.hadoop.yarn.api.records.ContainerStatus;
import org.apache.hadoop.yarn.api.records.FinalApplicationStatus;
import org.apache.hadoop.yarn.api.records.LocalResource;
import org.apache.hadoop.yarn.api.records.LocalResourceType;
import org.apache.hadoop.yarn.api.records.LocalResourceVisibility;
import org.apache.hadoop.yarn.api.records.Priority;
import org.apache.hadoop.yarn.api.records.Resource;
import org.apache.hadoop.yarn.client.api.AMRMClient;
import org.apache.hadoop.yarn.client.api.AMRMClient.ContainerRequest;
import org.apache.hadoop.yarn.client.api.NMClient;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.apache.hadoop.yarn.util.ConverterUtils;
import org.apache.hadoop.yarn.util.Records;

public class ApplicationMaster {

	private final static Logger log = Logger.getLogger(ApplicationMaster.class
			.getName());

	public static void main(String[] args) {

		if (args.length < 2) {
			log.log(Level.SEVERE, "Es wurden zwei Parameter erwartet, aber nur " + args.length + " gefunden - Beende.");
			return;
		}
		
		// Im ersten Argument des ApplicationMasters haben wir die obere Grenze
		// unserer Primzahlenberechnung abgelegt.
		int maxPrime = Integer.parseInt(args[0]);
		
		// Der zweite Parameter beinhaltet die Anzahl an Containern, die wir zur Verarbeitung
		// nutzen möchten. -1, da Schleifen mit 0 beginnen, nicht mit 1.
		int containersRequested = Integer.parseInt(args[1]) - 1;

		log.log(Level.INFO, "Erzeuge ApplicationMaster...");

		// Erstellen der Konfiguration - yarn-site.xml und core-site.xml (und defaults) werden automatisch ausgelesen
		Configuration conf = new YarnConfiguration();

		log.log(Level.INFO, "Konfiguration erstellt.");

		// Zugriff auf das HDFS erfragen
		FileSystem fs = null;
		try {
			fs = FileSystem.get(conf);
		} catch (IOException e1) {
			log.log(Level.SEVERE, "Kein Zugriff auf das HDFS möglich.");
			e1.printStackTrace();
		}

		log.log(Level.INFO, "Link auf HDFS geholt.");

		// Angeben, wo die Anwendung liegt, die vom ApplicationMaster ausgeführt
		// werden soll (usere Primzahlenlogik)
		Path jarPath2 = new Path("/hdfs/yarn1/08_PrimeCalculator.jar");
		jarPath2 = fs.makeQualified(jarPath2); // Pfad muss voll qualifiziert sein (hdfs://...)

		// Dateiinformationen von 08_PrimeCalculator.jar im HDFS abfragen
		FileStatus jarStat2 = null;
		try {
			jarStat2 = fs.getFileStatus(jarPath2);
			log.log(Level.INFO, "JAR-Pfad im HDFS ist " + jarStat2.getPath());
		} catch (IOException e) {
			log.log(Level.SEVERE,
					"Datei des ApplicationMaster-JARs konnte nicht abgerufen werden!");
			e.printStackTrace();
		}

		// Liefere das JAR 08_PrimeCalculator.jar an die Container als LocalResource mit
		LocalResource packageResource = Records.newRecord(LocalResource.class);
		packageResource
				.setResource(ConverterUtils.getYarnUrlFromPath(jarPath2));
		packageResource.setSize(jarStat2.getLen());
		packageResource.setTimestamp(jarStat2.getModificationTime());
		
		// Da wir gleich die Anwendung als JAR starten, muss sie nicht als ARCHIVE mitgegeben
		// werden, damit Hadoop sie entpackt. Wir können sie so aufrufen.
		packageResource.setType(LocalResourceType.FILE);
		
		// Die Ressource soll nur unserem Container zur Verfügung stehen und gelöscht werden,
		// sobald der Job bearbeitet wurde.
		packageResource.setVisibility(LocalResourceVisibility.APPLICATION);

		log.log(Level.INFO, "Package für Anwendungslogik erstellt.");

		// Initialisieren des Clients für den ResourceManager
		AMRMClient<ContainerRequest> rmClient = AMRMClient.createAMRMClient();
		rmClient.init(conf);
		rmClient.start();

		log.log(Level.INFO, "Client für ResourceManager initialisiert.");

		// Initialisieren des Clients für den NodeManager
		NMClient nmClient = NMClient.createNMClient();
		nmClient.init(conf);
		nmClient.start();

		log.log(Level.INFO, "Client für NodeManager initialisiert.");

		// Registrieren des Clients am ResourceManager
		try {
			rmClient.registerApplicationMaster("", 0, "");
		} catch (Exception e) {
			log.log(Level.SEVERE,
					"Konnte Client nicht im ResourceManager registrieren!");
			e.printStackTrace();
			return;
		}

		log.log(Level.INFO,
				"Client erfolgreich im ResourceManager registriert.");

		// Setze Priorität für Container
		Priority priority = Records.newRecord(Priority.class);
		priority.setPriority(0);

		log.log(Level.INFO, "Priorität für Container gesetzt auf 0.");

		// Setze die nötigen Resourcen für die Container
		Resource capability = Records.newRecord(Resource.class);
		capability.setMemory(128);
		capability.setVirtualCores(1);

		log.log(Level.INFO,
				"Nötige Resourcen für Container angefordert (128MB RAM, 1 Kern).");

		// Anfordern der Container
		for (int i = 0; i < containersRequested; ++i) {
			ContainerRequest containerAsk = new ContainerRequest(capability,
					null, null, priority);
			rmClient.addContainerRequest(containerAsk);
		}

		log.log(Level.INFO, "Es wurden " + containersRequested
				+ " Container angefordert.");

		// Abholen und starten der requestierten Container
		int allocatedContainers = 0;
		int completedContainers = 0;

		// Solage wir noch nicht die gewünschte Anzahl an Containern vom
		// Hadoop-Cluster bekommen haben...
		while (allocatedContainers < containersRequested) {
			AllocateResponse response = null;

			// ... fragen wir, ob es Neuigkeiten vom RM gibt.
			try {
				response = rmClient.allocate(0);
			} catch (Exception e) {
				log.log(Level.WARNING, "Fehler beim Abfragen des Status.");
			}

			// Für alle Container, die wir nun zurückbekommen haben, zählen wir
			// den Zähler für die allokierten Container um 1 hoch.
			for (Container container : response.getAllocatedContainers()) {
				++allocatedContainers;
				
				// Container wird durchs Erstellen einer ContainerLaunchContexts
				// gestartet
				ContainerLaunchContext ctx = Records
						.newRecord(ContainerLaunchContext.class);

				// Hier wird der Command spezifiziert, der die eigentlich Anwendung darstellt
				// "1>" bedeutet, dass stdout hierhin ausgegeben wird, "2>" leitet stderr um.
				// Von uns festgelegte Parameter sind: 1. Container-Nummer, 2. Container-Anzahl, 3. Maximale Primzahl	
				String command = "java -jar primecalculator.jar " +
						allocatedContainers + " " + containersRequested + " " + maxPrime +
						" " +
						"1>/usr/local/hadoop/container_" + allocatedContainers + "_out "+ 
						"2>/usr/local/hadoop/container_" + allocatedContainers + "_err";

				// Weise dem ContainerLaunchContext den Befehl zu
				ctx.setCommands(Collections.singletonList(command));

				// Unser 08_PrimeCalculator.jar wird als lokale Ressource auf den Nodes bereitgestellt
				ctx.setLocalResources(Collections.singletonMap("primecalculator.jar",
						packageResource));

				log.log(Level.INFO, "Starte Container #" + allocatedContainers
						+ " mit Command '" + ctx.getCommands().toString()+"' ... ");
				
				try {

					// Wir starten den neu erhaltenen Container und sorgen
					// dafür, dass er unseren Befehl ausführt.
					nmClient.startContainer(container, ctx);
				} catch (Exception e) {
					log.log(Level.SEVERE, "Fehler beim Starten des Containers.");
					e.printStackTrace();
				}
			}

			// Wurde einer der Container abgearbeitet? Dann zähle den Zähler der
			// fertigen Container um 1 nach oben
			for (ContainerStatus status : response
					.getCompletedContainersStatuses()) {
				++completedContainers;
				log.log(Level.INFO, "Container " + status.getContainerId()
						+ " mit Status " + status.getState()
						+ " fertiggestellt.");

			}

			// Pausiere den Thread um 100 Millisekunden, bis erneut nach
			// Containern gebeten wird.
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				log.log(Level.SEVERE,
						"Fehler beim Warten auf die Bereitstellung der Container.");
				e.printStackTrace();
			}
		}

		log.log(Level.INFO, "Startbefehl für alle Container wurde gegeben.");

		// Warte bis die reservierten Container mit der Arbeit fertig sind.
		while (completedContainers < containersRequested) {

			AllocateResponse response = null;
			try {
				response = rmClient.allocate(completedContainers
						/ containersRequested);
			} catch (Exception e) {
				log.log(Level.SEVERE,
						"Fehler beim Abholen der AllocateResponse.");
				e.printStackTrace();
			}
			for (ContainerStatus status : response
					.getCompletedContainersStatuses()) {
				++completedContainers;

				log.log(Level.INFO, "Container " + completedContainers
						+ " hat seine Arbeit mit Status '" + status
						+ "' beendet.");
			}

			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				log.log(Level.SEVERE,
						"Fehler beim Warten auf Fertigstellung der Anwendung.");
				e.printStackTrace();
			}
		}

		log.log(Level.INFO, "Container haben ihre Arbeit verrichtet.");

		// Deregistrierung des ApplicationMasters vom ResourceManager
		try {
			rmClient.unregisterApplicationMaster(
					FinalApplicationStatus.SUCCEEDED, "", "");
		} catch (Exception e) {
			log.log(Level.SEVERE,
					"Fehler beim Deregistrieren des ApplicationMasters.");
			e.printStackTrace();
		}

		log.log(Level.INFO, "ApplicationMaster ist fertig - Beende.");
	}
}
