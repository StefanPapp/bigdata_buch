package de.jofre.grades;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.hadoop.io.FloatWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.mapreduce.Reducer;

// Eingabe-Key, Eingabe-Wert, Ausgabe-Key, Ausgabe-Wert
public class GradesReducer extends Reducer<IntWritable, IntWritable, IntWritable, FloatWritable> {

	private final static Logger log = Logger.getLogger(GradesMapper.class.getName());
	
	@Override
	protected void reduce(IntWritable key, Iterable<IntWritable> values, Context context)
			throws IOException, InterruptedException {

		// Summiere alle Noten eines Jahres auf...
		float sum = 0;
		float count = 0;
		for (IntWritable val : values) {
			sum +=val.get();
			count +=1;
		}
	
		// Und bilde den Durchschnitt
		float result = sum / count;
		
		// Am Ende soll eine Note nach dem Schema x,x herauskommen
		result /=10;
		
		log.log(Level.INFO, "Schreibe Jahr: "+key+" und Ergebnis: "+result);
		
		// Schreibe den Durschnitt für das Jahr in key
		context.write(key, new FloatWritable(result));
		
	}
}
