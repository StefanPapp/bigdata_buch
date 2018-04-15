package de.jofre.prime;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import de.jofre.helper.HadoopProperties;

public class PrimeCalculator {
	
	private final static Logger log = Logger.getLogger(PrimeCalculator.class
			.getName());

	// Methode zum Auffinden von Primzahlen
	private static List<Integer> getPrimeNumbers(int min, int max) {
		List<Integer> primeNumbers = new ArrayList<Integer>();
		boolean primeCheck = false;
		if ((min >= 2) && (max > min)) {
			int k;
			for (k = min; k < max; k++) {
				primeCheck = true;
				for (int j = 2; j < k / 2; j++) {
					if (k % j == 0) {
						primeCheck = false;
						break;
					}
				}
				if (primeCheck) {
					primeNumbers.add(k);
				}
			}
		} else {
			// Ungültige Grenzen - Gebe leere Liste zurück
			return primeNumbers;
		}
		return primeNumbers;
	}

	public static void main(String[] args) {
		
		if (args.length < 3) {
			log.log(Level.SEVERE, "Anwendung erwartet 3 Argumente - es wurden lediglich " + args.length + " übergeben - Beende.");
			return;
		}

		// Hole die Nummer des allokierten Containers
		int currentContainerNumber = Integer.parseInt(args[0]);

		// Hole die Gesamtanzahl der Container
		int containerCount = Integer.parseInt(args[1]);

		// Hole max. Zahl für Primtest
		int maxInt = Integer.parseInt(args[2]);
		
		log.log(Level.INFO, "Starte einen neuen Container - Container-Nr: " + currentContainerNumber + ", Container-Anzahl: " + containerCount + ", Max-Int: " + maxInt);

		int minBorder = maxInt / containerCount * (currentContainerNumber - 1);
		if (minBorder < 2) minBorder = 2; // Algorthmus akzeptiert nur n>1
		int maxBorder = maxInt / containerCount * currentContainerNumber;
		if (containerCount > currentContainerNumber) maxBorder -=1; // Überschneidungen vermeiden
		
		log.log(Level.INFO, "Berechne Primzahlen von " + minBorder + " bis " + maxBorder + ".");

		// Berechne die Primzahlen für den gegebenen Intervall
		List<Integer> primeNumbers = getPrimeNumbers(minBorder, maxBorder);
		
		// Die Anwendung hat nicht überall Zugriff auf die Konfiguration des Hadoop-Clusters.
		// Also übergeben wir die nötigen Parameter manuell
		Configuration conf = new Configuration();
		conf.set("fs.defaultFS", HadoopProperties.get("hdfs_address"));	
		
		// Das Maven-assembly-plugin setzt die verwendeten Klassen für HDFS und File falsch.
		// Die folgenden zwei Zeilen korrigieren das.
		// Siehe: http://stackoverflow.com/questions/17265002/hadoop-no-filesystem-for-scheme-file
		conf.set("fs.hdfs.impl", org.apache.hadoop.hdfs.DistributedFileSystem.class.getName());
		conf.set("fs.file.impl", org.apache.hadoop.fs.LocalFileSystem.class.getName());

		// Schreibe das Ergebnis ins HDFS
		FileSystem fs = null;
		Path outputPath = null;
		try {
			fs = FileSystem.get(conf);

			if (fs != null) {
				
				// Jeder Container bekommt eine eigene Datei
				outputPath = new Path("/hdfs/yarn1/output/container" + currentContainerNumber + ".txt");
				outputPath = fs.makeQualified(outputPath);
				log.log(Level.INFO, "Ausgabepfad ist: " + outputPath);
				
				// Wir schreiben direkt ins HDFS
				BufferedWriter br=new BufferedWriter(new OutputStreamWriter(fs.create(outputPath,true)));
				
				log.log(Level.INFO, "Schreibe Ausgabe nach " + outputPath);

				if (br != null) {
					for (int i = 0; i < primeNumbers.size(); i++) {
						
						// Schreibe die Primzahlen als String, nicht als Integer
						br.write(primeNumbers.get(i).toString());
						br.write(","); // ... getrennt durch Kommata
					}
					br.close();
					log.log(Level.INFO, "Schreibevorgang abgeschlossen!");
				}
			}
		} catch (IOException e) {
			log.log(Level.SEVERE, "Ausgabe für Container "+currentContainerNumber+" konnte nicht nach "+outputPath+" geschrieben werden.");
			e.printStackTrace();
		}
		
		log.log(Level.INFO, "Container " + currentContainerNumber + " fertig - Beende.");

	}
}
