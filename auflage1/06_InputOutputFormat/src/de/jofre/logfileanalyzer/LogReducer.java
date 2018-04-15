package de.jofre.logfileanalyzer;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

public class LogReducer extends Reducer<Text, IntWritable, Text, IntWritable> {

	private final static Logger log = Logger.getLogger(LogReducer.class.getName());
	
	@Override
	protected void reduce(Text key, Iterable<IntWritable> values, Context context)
			throws IOException, InterruptedException {

		// Summiere alle Vorkommnisse der Log-Level-Bezeichner auf...
		int sum = 0;
		for (IntWritable val : values) {
			sum +=val.get();
		}
	
		log.log(Level.INFO, "Schreibe Log-Level-Bezeichner '"+key+"' mit Anzahl '"+sum+"'.");
		
		// Schreibe die absolute Anzahl der vorkommenden Log-Bezeichnung für den
		// einen Key auf.
		context.write(key, new IntWritable(sum));
		
	}
}
