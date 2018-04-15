package de.jofre.primemaster;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.yarn.api.ApplicationConstants;
import org.apache.hadoop.yarn.api.ApplicationConstants.Environment;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.apache.hadoop.yarn.api.records.ApplicationReport;
import org.apache.hadoop.yarn.api.records.ApplicationSubmissionContext;
import org.apache.hadoop.yarn.api.records.ContainerLaunchContext;
import org.apache.hadoop.yarn.api.records.LocalResource;
import org.apache.hadoop.yarn.api.records.LocalResourceType;
import org.apache.hadoop.yarn.api.records.LocalResourceVisibility;
import org.apache.hadoop.yarn.api.records.Resource;
import org.apache.hadoop.yarn.api.records.YarnApplicationState;
import org.apache.hadoop.yarn.client.api.YarnClient;
import org.apache.hadoop.yarn.client.api.YarnClientApplication;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.apache.hadoop.yarn.util.Apps;
import org.apache.hadoop.yarn.util.ConverterUtils;
import org.apache.hadoop.yarn.util.Records;

public class Client {

	private final static Logger log = Logger.getLogger(Client.class.getName());
	private final static SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss (dd.MM.yyyy)");
	
	// Der Constructor von Configuration liest automatisch die core-default.xml und die core-site.xml,
	// der Constructor von YarnConfiguration liest zusätzlich yarn-default.xml und yarn-site.xml
	private Configuration conf = new YarnConfiguration();

	public void run(String[] args) {
		
		if (args.length < 3) {
			log.log(Level.SEVERE, "Es wurden drei Parameter erwartet, aber nur " + args.length + " gefunden - Beende.");
			return;
		}
		
		log.log(Level.INFO, "Job wird erzeugt...");
		
		// Werte die Argumente des Jobs aus
		Path jarPath = new Path(args[0]); // Erstes Argument nach der Klassenangabe enthält den Pfad des JARs, das später über den Cluster verteilt werden soll
		
		int maxPrime = Integer.parseInt(args[1]); // Zweites Argument, die obere Primzahlengrenze
		
		int containers = Integer.parseInt(args[2]); // Gesamtanzahl der Container
		
		log.log(Level.INFO, "Argumente des Jobs wurden ausgelesen - Pfad des JARs auf dem HDFS ist " + jarPath.toString()+", Primzahlen werden bis " + maxPrime + " auf " + containers + " Containern berechnet.");
		
		// Kopiere das JAR, das verteilt werden soll, in das HDFS
		FileSystem fs = null;
		try {
			fs = FileSystem.get(conf);
			
			// Das Dateisystem muss hier zwingend spezifiziert werden (hdfs://...)
			jarPath = fs.makeQualified(jarPath);
			
			// Löschen falls vorhanden
			if (fs.exists(jarPath)) {
				log.log(Level.INFO, "Datei '"+jarPath.toString()+"' existierte  bereits im HDFS, wird gelöscht.");
				fs.delete(jarPath, true);
			}
			
			
			// Kopieren des neuen JARs an den Zielordner
			String yarnAppName = jarPath.getName();
			fs.copyFromLocalFile(new Path(yarnAppName), jarPath);
			log.log(Level.INFO, "JAR kopiert von '"+yarnAppName+"' nach '"+jarPath.toString()+"'.");
		} catch (IOException e1) {
			log.log(Level.SEVERE, "JAR konnte nicht ins HDFS kopiert werden!");
			e1.printStackTrace();
		}
		
	    // Erstelle den YarnClient um Zugriff auf den Cluster zu erhalten
	    YarnClient yc = YarnClient.createYarnClient();
	    yc.init(conf);
	    yc.start();
	    
	    log.log(Level.INFO, "YarnClient wurde erstellt.");
	    
	    // Erstelle eine Anwendungsinstanz über den Client
	    YarnClientApplication app = null;
	    try {
			app = yc.createApplication();
		} catch (Exception e) {
			log.log(Level.SEVERE, "YarnClientApplication konnte nicht erzeugt werden!");
			e.printStackTrace();
		}
	    
	    // Erzeuge einen ContainerLaunchContext um den ApplicationMaster auszuführen
	    ContainerLaunchContext amContainer =
	        Records.newRecord(ContainerLaunchContext.class);
	    // Setzen des Befehls zum Ausführen des ApplicationMasters
	    amContainer.setCommands(
	        Collections.singletonList(
	            "$JAVA_HOME/bin/java" +
	            " -Xmx256M" +
	            " de.jofre.primemaster.ApplicationMaster" +
	            " " + maxPrime + " " + containers + 
	            " 1>" + ApplicationConstants.LOG_DIR_EXPANSION_VAR + "/stdout" +
	            " 2>" + ApplicationConstants.LOG_DIR_EXPANSION_VAR + "/stderr"
	            )
	        );
	    
	    log.log(Level.INFO, "Command für ApplicationMaster wurde gesetzt: "+amContainer.getCommands().toString());
	    
	    // Setzen des JARs des ApplicationMasters als lokale Ressource, die mit dem Request mitgeschickt wird.
	    LocalResource appMasterJar = Records.newRecord(LocalResource.class);
	    FileStatus jarStat = null;
		try {
			jarStat = fs.getFileStatus(jarPath);
			log.log(Level.INFO, "JAR-Pfad im HDFS ist "+jarStat.getPath());
		} catch (IOException e) {
			log.log(Level.SEVERE, "Datei des ApplicationMaster-JARs konnte nicht abgerufen werden!");
			e.printStackTrace();
		}
	    appMasterJar.setResource(ConverterUtils.getYarnUrlFromPath(jarPath));
	    appMasterJar.setSize(jarStat.getLen());
	    appMasterJar.setTimestamp(jarStat.getModificationTime());
	    appMasterJar.setType(LocalResourceType.FILE);
	    appMasterJar.setVisibility(LocalResourceVisibility.APPLICATION);
	       
	    // Hier wird auf Ubuntu ein Syslink mit Namen primegen.jar angelegt (Name beliebig).
		Map<String, LocalResource> res = new HashMap<String, LocalResource>();
		res.put("primegen.jar", appMasterJar);
		amContainer.setLocalResources(res);
	    
	    // Setup des CLASSPATH für den ApplicationMaster
	    Map<String, String> appMasterEnv = new HashMap<String, String>();
	    
	    // Erfragen aller Eigenschaften entsprechend der YARN-Konstanten
	    for (String c : conf.getStrings(YarnConfiguration.YARN_APPLICATION_CLASSPATH, YarnConfiguration.DEFAULT_YARN_APPLICATION_CLASSPATH)) {
	          Apps.addToEnvironment(appMasterEnv, Environment.CLASSPATH.name(), c.trim());
	          log.log(Level.INFO, "'" + c.trim() + "' wurde der Umgebungsvariablen hinzugefügt.");
	    }
	    Apps.addToEnvironment(appMasterEnv, Environment.CLASSPATH.name(), Environment.PWD.$() + File.separator + "*");
	    log.log(Level.INFO, "'" + Environment.PWD.$() + File.separator + "*" + "' wurde der Umgebungsvariablen hinzugefügt.");
	    amContainer.setEnvironment(appMasterEnv);
	    
	    // Setzen der benötigten Resourcen für den ApplicationMaster
	    Resource capability = Records.newRecord(Resource.class);
	    capability.setMemory(256); // 256MB RAM
	    capability.setVirtualCores(1); // 1 virtueller Kern
	    
	    // Anwendung zum Abschicken bereitmachen
	    ApplicationSubmissionContext appContext = app.getApplicationSubmissionContext();
	    appContext.setApplicationName("PrimeGenerator"); // Name der Anwendung
	    appContext.setAMContainerSpec(amContainer);
	    appContext.setResource(capability);
	    appContext.setQueue("default");
	    
	    // Anwendung starten
	    ApplicationId appId = appContext.getApplicationId();
	    log.log(Level.INFO, "Submitte Anwendung "+appId);
	    
	    try {
			ApplicationId id = yc.submitApplication(appContext);
			log.log(Level.INFO, "Application mit ID '"+id+"' erstellt.");
		} catch (Exception e) {
			log.log(Level.SEVERE, "Fehler beim Absenden des Jobs!");
			e.printStackTrace();
		}
	    
	    // Warten, bis die Anwendung beendet wurde
	    String status = "unbekannt";
	    String finishTime = "unbekannt";
		try {
			ApplicationReport appReport = yc.getApplicationReport(appId);
		    YarnApplicationState appState = appReport.getYarnApplicationState();
		    while (appState != YarnApplicationState.FINISHED &&
		           appState != YarnApplicationState.KILLED &&
		           appState != YarnApplicationState.FAILED) {
		      Thread.sleep(500);
		      appReport = yc.getApplicationReport(appId);
		      appState = appReport.getYarnApplicationState();
		      status = appState.toString();
		      finishTime = sdf.format(new Date(appReport.getFinishTime()));
		    }				
		} catch (Exception e) {
			log.log(Level.SEVERE, "Fehler beim Abholen des Job-Status.");
			e.printStackTrace();
		}
    
		// Fertig
		log.log(Level.INFO, "Anwendung " + appId + " mit Status '" + status + "' fertig ausgeführt um " + finishTime);
	    
	}

	// Die Methode main dient als Einstieg in die YARN-Anwendung
	public static void main(String[] args) throws Exception {
		Client c = new Client();
		c.run(args);
	}
}
