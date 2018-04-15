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
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.KeyValueTextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;

import de.jofre.grades.AverageGradeDriver;
import de.jofre.grades.AverageGradeMapper;
import de.jofre.grades.AverageGradeReducer;
import de.jofre.grades.GenderSpecificGradeDriver;
import de.jofre.grades.GenderSpecificGradeMapper;
import de.jofre.grades.GenderSpecificGradeReducer;
import de.jofre.helper.HadoopProperties;
import de.jofre.helper.JSPHelper;
import de.jofre.helper.WinUtilsSolver;

public class ChainedMRStarter {

	private final static Logger log = Logger.getLogger(ChainedMRStarter.class
			.getName());

	// Das Verzeichnis, in der die Ausgabe des Jobs geschrieben wird
	private final static String MR_OUTPUT_DIR_JOB1 = "/hdfs/mr3/output";
	private final static String MR_OUTPUT_DIR_JOB2 = "/hdfs/mr4/output";

	// Beinhaltet alle Eigenschaften der Hadoop-Konfiguration
	private Configuration conf = null;

	// Konstruktor wird bei jedem Erzeugen des Objekts aufgerufen
	public ChainedMRStarter() {

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
		
		// Wir benutzen für die Eingabe in den Mapper einen Tab als Trennzeichen
		// für Key und Value da unsere Struktur "Vorname[Leerzeichen]Nachname[Tab]Note" ist.
		conf.set("mapreduce.input.keyvaluelinerecordreader.key.value.separator", "\t");
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

		// Löschen der Ausgabeverzeichnisse
		try {

			// Das true besagt, dass die Ordner unter unserem Pfad rekursiv
			// gelöscht werden sollen.
			Path p1 = new Path(HadoopProperties.get("hdfs_address")
					+ MR_OUTPUT_DIR_JOB1);
			Path p2 = new Path(HadoopProperties.get("hdfs_address")
					+ MR_OUTPUT_DIR_JOB2);
			
			if (fs.exists(p1)) {
				fs.delete(p1, true);
			}
			
			if (fs.exists(p2)) {
				fs.delete(p2, true);
			}
		} catch (Exception e) {
			log.log(Level.SEVERE, "Fehler beim Löschen der Verzeichnisse '"
					+ MR_OUTPUT_DIR_JOB1 + "' und '" + MR_OUTPUT_DIR_JOB2
					+ "'.");
			e.printStackTrace();
			return false;
		}

		return true;
	}

	// Auslesen der Ergebnisdatei des Map-Reduce-Jobs
	public List<String> readResult() {

		// Hier wird Hadoop die Datei ablegen
		Path pt = new Path(HadoopProperties.get("hdfs_address")
				+ MR_OUTPUT_DIR_JOB2 + "/part-r-00000");
		List<String> result = new ArrayList<String>();
		FileSystem fs = null;
		BufferedReader br = null;
		try {
			// Zugriff auf das HDFS wird initialisiert
			fs = FileSystem.get(conf);

			// Wenn die Datei nicht existiert, beende.
			if (!fs.exists(pt)) {
				log.log(Level.SEVERE, "Die Ausgabedatei existiert nicht!");
				return null;
			}

			br = new BufferedReader(new InputStreamReader(fs.open(pt)));
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

	public boolean startJobs(JspWriter writer) {
		
		boolean result = false;

		//
		// 1. Job
		//
		JSPHelper.writeToJsp(writer, "<b>Erstelle Job1...</b><br>");

		Job job1 = null;
		try {
			job1 = Job.getInstance(conf);
		} catch (IOException e1) {
			log.log(Level.SEVERE, "Fehler beim Setzen der Job1-Config.");
			e1.printStackTrace();
			return false;
		}
		
		JSPHelper.writeToJsp(writer, "Job1-Konfiguration erstellt!<br>");

		// Hadoop soll ein verfügbares JAR verwenden, das die Klasse
		// GradesDriver enthält.
		job1.setJarByClass(AverageGradeDriver.class);

		// Mapper- und Reducer-Klasse werden festgelegt
		job1.setMapperClass(AverageGradeMapper.class);
		job1.setReducerClass(AverageGradeReducer.class);

		// Ausgabetypen werden festgelegt
		job1.setOutputKeyClass(Text.class);
		job1.setOutputValueClass(FloatWritable.class);
		job1.setMapOutputKeyClass(Text.class);
		job1.setMapOutputValueClass(FloatWritable.class);
		job1.setInputFormatClass(KeyValueTextInputFormat.class);
		job1.setOutputFormatClass(TextOutputFormat.class);

		JSPHelper.writeToJsp(writer, "Klassen für Job1 gesetzt.<br>");

		// Den Input-Pfad setzen wir diesmal im Code
		try {
			FileInputFormat.addInputPath(job1,
					new Path(HadoopProperties.get("hdfs_address")
							+ "/hdfs/mr3/input"));
		} catch (IOException e) {
			log.log(Level.SEVERE, "Fehler beim Setzen des Eingabepfades!");
			JSPHelper.writeToJsp(writer,
					"<font color=\"#FF0000\">Fehler beim Ausführen von Job1"
							+ e.getStackTrace() + "</font><br>");
			e.printStackTrace();
		}

		JSPHelper.writeToJsp(writer, "Eingabepfad gesetzt auf: "
				+ HadoopProperties.get("hdfs_address") + "/hdfs/mr3/input<br>");

		// Auch der Ausgabe-Pfad wird statisch gesetzt
		FileOutputFormat.setOutputPath(job1,
				new Path(HadoopProperties.get("hdfs_address")
						+ MR_OUTPUT_DIR_JOB1));
		JSPHelper.writeToJsp(writer, "Ausgabepfad gesetzt auf: "
				+ HadoopProperties.get("hdfs_address") + MR_OUTPUT_DIR_JOB1
				+ "<br>");

		try {
			// Führe den Job aus und warte, bis er beendet wurde
			JSPHelper.writeToJsp(writer, "Führe Job1 aus...<br>");
			result = job1.waitForCompletion(true);
		} catch (Exception e) {
			log.log(Level.SEVERE, "Fehler beim Ausführen von Job1!");
			JSPHelper.writeToJsp(writer,
					"<font color=\"#FF0000\">Fehler beim Ausführen von Job1"
							+ e.getStackTrace() + "</font><br>");
			e.printStackTrace();
		}

		JSPHelper.writeToJsp(writer, "<b>Fertig mit Job1!</b><br><br>");
		log.log(Level.INFO, "Fertig mit Job1!");
			
		//
		// 2. Job
		///
		JSPHelper.writeToJsp(writer, "<b>Erstelle Job2...</b><br>");

		//ControlledJob job2 = null;
		Job job2 = null;
		try {
			job2 = Job.getInstance(conf);
			//job2 = new ControlledJob(conf);
		} catch (IOException e1) {
			log.log(Level.SEVERE, "Fehler beim Setzen der Job2-Config.");
			e1.printStackTrace();
			return false;
		}

		JSPHelper.writeToJsp(writer, "Job2-Konfiguration erstellt!<br>");

		// Hadoop soll ein verfügbares JAR verwenden, das die Klasse
		// GradesDriver enthält.
		job2.setJarByClass(GenderSpecificGradeDriver.class);

		// Mapper- und Reducer-Klasse werden festgelegt
		job2.setMapperClass(GenderSpecificGradeMapper.class);
		job2.setReducerClass(GenderSpecificGradeReducer.class);

		// Ausgabetypen werden festgelegt
		job2.setOutputKeyClass(Text.class);
		job2.setOutputValueClass(FloatWritable.class);
		job2.setMapOutputKeyClass(Text.class);
		job2.setMapOutputValueClass(FloatWritable.class);
		job2.setInputFormatClass(KeyValueTextInputFormat.class);
		job2.setOutputFormatClass(TextOutputFormat.class);

		JSPHelper.writeToJsp(writer, "Klassen für Job2 gesetzt.<br>");

		// Der Eingabeordner von Job2 ist der Ausgabeordner von Job1
		try {
			FileInputFormat.addInputPath(job2,
					new Path(HadoopProperties.get("hdfs_address")
							+ MR_OUTPUT_DIR_JOB1));
		} catch (IOException e) {
			log.log(Level.SEVERE, "Fehler beim Setzen des Eingabepfades!");
			JSPHelper.writeToJsp(writer,
					"<font color=\"#FF0000\">Fehler beim Ausführen von Job2"
							+ e.getStackTrace() + "</font><br>");
			e.printStackTrace();
		}

		JSPHelper.writeToJsp(writer, "Eingabepfad gesetzt auf: "
				+ HadoopProperties.get("hdfs_address") + MR_OUTPUT_DIR_JOB1 + "<br>");

		// Auch der Ausgabe-Pfad wird statisch gesetzt
		FileOutputFormat.setOutputPath(job2,
				new Path(HadoopProperties.get("hdfs_address")
						+ MR_OUTPUT_DIR_JOB2));
		JSPHelper.writeToJsp(writer, "Ausgabepfad gesetzt auf: "
				+ HadoopProperties.get("hdfs_address") + MR_OUTPUT_DIR_JOB2
				+ "<br>");

		try {
			// Führe den Job aus und warte, bis er beendet wurde
			JSPHelper.writeToJsp(writer, "Führe Job2 aus...<br>");
			result = job2.waitForCompletion(true);
		} catch (Exception e) {
			log.log(Level.SEVERE, "Fehler beim Ausführen von Job2!");
			JSPHelper.writeToJsp(writer,
					"<font color=\"#FF0000\">Fehler beim Ausführen von Job2"
							+ e.getStackTrace() + "</font><br>");
			e.printStackTrace();
		}

		JSPHelper.writeToJsp(writer, "<b>Fertig mit Job2!</b><br><br>");
		log.log(Level.INFO, "Fertig mit Job2!");
		
		// Optional kann ein JobControl-Objekt erstellt werden. Hier kann jedoch nicht
		// auf die Fertigstellung des Jobs gewartet werden.
		/*JobControl jc = new JobControl("grades");
		jc.addJob(job1);
		jc.addJob(job2);
		job2.addDependingJob(job1);
		jc.run();*/
		
		// Ergebnisse
		JSPHelper.writeToJsp(writer, "<b>Ergebnisse:</b><br>");
		List<String> results = readResult();

		if (results != null) {
			for (int i = 0; i < results.size(); i++) {
				JSPHelper.writeToJsp(writer, results.get(i) + "<br>");
			}
		} else {
			JSPHelper.writeToJsp(writer, "Es wurde keine Ergebnisdatei gefunden!<br>");
		}
		
		return result;
	}
}
