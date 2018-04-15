package de.jofre.logfileanalyzer;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

public class LogMapper extends Mapper<Text, Text, Text, IntWritable> {

	private final static Logger log = Logger.getLogger(LogMapper.class
			.getName());

	// Statische Zahl 1
	private final IntWritable one = new IntWritable(1);

	public void map(Text key, Text value, Context context) throws IOException,
			InterruptedException {

		// Trenne eine Zeile nach Leerzeichen, sodass alle Wörter darin einzeln
		// in dem String-Array words vorliegen.
		String[] words = key.toString().split(" ");

		// Überprüfe alle diese Wörter auf Gleichheit mit einem bekannten Log-Level
		for (int i = 0; i < words.length; i++) {
			
			log.log(Level.INFO, "Logmeldung gefunden mit Bezeichner '"+words[i]+"'.");
			if (words[i].equals("SEVERE")
					|| words[i].equals("WARNING")
					|| words[i].equals("INFO")
					|| words[i].equals("CONFIG")
					|| words[i].equals("FINE")
					|| words[i].equals("FINER")
					|| words[i].equals("FINEST")) {
				
				// Wurde ein Log-Level gefunden, sammle es auf.
				log.log(Level.INFO, words[i] + " ist ein Log-Level und wird indiziert.");
				context.write(new Text(words[i]), one);
			}
		}

	}
}
