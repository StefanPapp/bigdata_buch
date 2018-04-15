package de.jofre.grades;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.FloatWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.KeyValueTextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

public class GradesDriver extends Configured implements Tool {
	
	private final static Logger log = Logger.getLogger(GradesDriver.class.getName());

	public static void main(String[] args) {
        int res = 1; // Wenn 1 nicht verändert wird, endet der Job nicht korrekt
		try {
			res = ToolRunner.run(new Configuration(), new GradesDriver(), args);
		} catch (Exception e) {
			log.log(Level.SEVERE, "Fehler beim Ausführen des Jobs!");
			e.printStackTrace();
		}
        System.exit(res);
	}
	
	

    /* 
     * Konstruiert und startet den Job.
     * 
     * @see org.apache.hadoop.util.Tool#run(java.lang.String[])
     */
    @Override
    public int run(String[] args) {
 
		log.log(Level.INFO, "Starte Map-Reduce-Job 'GradesDriver'... ");
        
		// Wenn Configured erweitert wird, kann die bestehende Konfiguration
		// per getConf abgerufen werden.
        Configuration conf = this.getConf();
        Job job = null;
        
		try {
			job = Job.getInstance(conf);
		} catch (IOException e1) {
			log.log(Level.SEVERE, "Fehler bei Instanziierung des Jobs!");
			e1.printStackTrace();
		}
		
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
		
		// Der Pfad, aus dem Hadoop die Eingabedateien list, wird als erstes Argument
		// beim Starten des JARs übergeben.
		try {
			FileInputFormat.addInputPath(job, new Path(args[0]));
		} catch (IllegalArgumentException e) {
			log.log(Level.SEVERE, "Fehler (Argument) beim Setzen des Eingabepfades!");
			e.printStackTrace();
		} catch (IOException e) {
			log.log(Level.SEVERE, "Fehler (IO) beim Setzen des Eingabepfades!");
			e.printStackTrace();
		}
		
		// Der Ausgabeordner wird als zweites Argument übergeben
		FileOutputFormat.setOutputPath(job, new Path(args[1]));
		boolean result = false;

		try {
			// Führe den Job aus und warte, bis er beendet wurde
			result = job.waitForCompletion(true);
		} catch (ClassNotFoundException e) {
			log.log(Level.SEVERE, "Fehler (ClassNotFound) beim Ausführen des Jobs!");
			e.printStackTrace();
		} catch (IOException e) {
			log.log(Level.SEVERE, "Fehler (IOException) beim Ausführen des Jobs!");
			e.printStackTrace();
		} catch (InterruptedException e) {
			log.log(Level.SEVERE, "Fehler (Interrupted) beim Ausführen des Jobs!");
			e.printStackTrace();
		}

		log.log(Level.INFO, "Fertig!");
		return result ? 0 : 1;
    }
}
