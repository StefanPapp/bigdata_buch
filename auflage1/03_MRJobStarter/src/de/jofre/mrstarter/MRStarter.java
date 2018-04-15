package de.jofre.mrstarter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.jsp.JspWriter;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.FloatWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.KeyValueTextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;

import de.jofre.grades.GradesDriver;
import de.jofre.grades.GradesMapper;
import de.jofre.grades.GradesReducer;
import de.jofre.helper.HadoopProperties;
import de.jofre.helper.JSPHelper;
import de.jofre.helper.WinUtilsSolver;

public class MRStarter {

	private final static Logger log = Logger.getLogger(MRStarter.class
			.getName());

	// Das Verzeichnis, in der die Ausgabe des Jobs geschrieben wird
	private final static String MR_OUTPUT_DIR = "/hdfs/mr2/output";

	// Beinhaltet alle Eigenschaften der Hadoop-Konfiguration
	private Configuration conf = null;

	// Konstruktor wird bei jedem Erzeugen des Objekts aufgerufen
	public MRStarter() {

		// Setze den Hadoop-User
		System.setProperty("HADOOP_USER_NAME",
				HadoopProperties.get("hadoop_user"));

		// Gebe des Verzeichnis der Hadoop-Binaries bekannt
		WinUtilsSolver.solveWinUtilError();

		// Erstelle die Konfiguration
		conf = new Configuration();
		conf.set("yarn.resourcemanager.scheduler.address",
				HadoopProperties.get("scheduler_address"));
		conf.set("yarn.resourcemanager.address",
				HadoopProperties.get("resourcemgr_address"));
		conf.set("yarn.resourcemanager.resource-tracker.address",
				HadoopProperties.get("task_tracker_address"));
		conf.set("fs.defaultFS", HadoopProperties.get("hdfs_address"));
	}

	public boolean deleteOutput() {

		// Initialisieren des FileSystem-Zugriffs.
		FileSystem fs = null;
		try {
			fs = FileSystem.get(conf);
		} catch (IOException e) {
			log.log(Level.SEVERE,
					"Fehler beim Initialisieren des Zugriffs auf das FileSystem.");
			e.printStackTrace();
			return false;
		}

		// Löschen des Ausgabeverzeichnisses
		try {

			// Das true besagt, dass die Ordner unter unserem Pfad rekursiv
			// gelöscht werden sollen.
			fs.delete(new Path(HadoopProperties.get("hdfs_address")
					+ MR_OUTPUT_DIR), true);
		} catch (Exception e) {
			log.log(Level.SEVERE, "Fehler beim Löschen des Verzeichnisses '"
					+ MR_OUTPUT_DIR + "'.");
			e.printStackTrace();
			return false;
		}

		return true;
	}

	// Auslesen der Ergebnisdatei des Map-Reduce-Jobs
	public List<String> readResult() {
		
		// Hier wird Hadoop die Datei ablegen
		Path pt = new Path(HadoopProperties.get("hdfs_address") + MR_OUTPUT_DIR
				+ "/part-r-00000");
		List<String> result = new ArrayList<String>();
		FileSystem fs = null;
		BufferedReader br = null;
		try {
			
			// Zugriff auf das HDFS wird initialisiert
			fs = FileSystem.get(conf);
			br = new BufferedReader(new InputStreamReader(
					fs.open(pt)));
			String line;
			
			// Solange noch Zeilen in der ASCII-Datei zu finden sind,
			// lese diese aus und speicher sie in der Liste
			while ((line = br.readLine()) != null) {
				result.add(line);
			}
		} catch (IOException e) {
			log.log(Level.SEVERE, "Fehler beim Lesen der Ausgabedatei.");
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					log.log(Level.SEVERE, "Fehler beim Schließen des Readers.");
					e.printStackTrace();
				}
			}
		}

		return result;
	}

	public boolean startJob(JspWriter writer) {

		boolean result = false;

		Job job = null;
		try {
			job = Job.getInstance(conf);
		} catch (IOException e1) {
			log.log(Level.SEVERE, "Fehler beim Setzen der Job-Config.");
			e1.printStackTrace();
		}

		JSPHelper.writeToJsp(writer, "Job-Konfiguration erstellt!<br>");

		// Hadoop soll ein verfügbares JAR verwenden, das die Klasse
		// GradesDriver enthält.
		job.setJarByClass(GradesDriver.class);

		// Mapper- und Reducer-Klasse werden festgelegt
		job.setMapperClass(GradesMapper.class);
		job.setReducerClass(GradesReducer.class);

		// Ausgabetypen werden festgelegt
		job.setOutputKeyClass(IntWritable.class);
		job.setOutputValueClass(FloatWritable.class);
		job.setMapOutputKeyClass(IntWritable.class);
		job.setMapOutputValueClass(IntWritable.class);
		job.setInputFormatClass(KeyValueTextInputFormat.class);
		job.setOutputFormatClass(TextOutputFormat.class);

		JSPHelper.writeToJsp(writer, "Klassen für Job gesetzt.<br>");

		// Den Input-Pfad setzen wir diesmal im Code
		try {
			FileInputFormat.addInputPath(job,
					new Path(HadoopProperties.get("hdfs_address")
							+ "/hdfs/mr1/input"));
		} catch (IOException e) {
			log.log(Level.SEVERE, "Fehler beim Setzen des Eingabepfades!");
			JSPHelper.writeToJsp(writer,
					"<font color=\"#FF0000\">Fehler beim Ausführen des Jobs"
							+ e.getStackTrace() + "</font><br>");
			e.printStackTrace();
		}

		JSPHelper.writeToJsp(writer, "Eingabepfad gesetzt auf: "
				+ HadoopProperties.get("hdfs_address") + "/hdfs/mr1/input<br>");

		// Auch der Ausgabe-Pfad wird statisch gesetzt
		FileOutputFormat.setOutputPath(job,
				new Path(HadoopProperties.get("hdfs_address")
						+ "/hdfs/mr2/output"));
		JSPHelper
				.writeToJsp(writer, "Ausgabepfad gesetzt auf: "
						+ HadoopProperties.get("hdfs_address")
						+ "/hdfs/mr2/output<br>");

		try {
			// Führe den Job aus und warte, bis er beendet wurde
			JSPHelper.writeToJsp(writer, "Führe Job aus...<br>");
			result = job.waitForCompletion(true);
		} catch (Exception e) {
			log.log(Level.SEVERE, "Fehler beim Ausführen des Jobs!");
			JSPHelper.writeToJsp(writer,
					"<font color=\"#FF0000\">Fehler beim Ausführen des Jobs"
							+ e.getStackTrace() + "</font><br>");
			e.printStackTrace();
		}

		JSPHelper.writeToJsp(writer, "<b>Fertig!</b><br><br>");
		log.log(Level.INFO, "Fertig!");
		
		JSPHelper.writeToJsp(writer, "<b>Ergebnisse:</b><br>");
		List<String> results = readResult();
		
		for(int i=0; i<results.size(); i++) {
			JSPHelper.writeToJsp(writer, results.get(i)+"<br>");
		}
		return result;
	}
}
