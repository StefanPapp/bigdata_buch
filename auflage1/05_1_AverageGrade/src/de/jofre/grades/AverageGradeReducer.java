package de.jofre.grades;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.hadoop.io.FloatWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

// Eingabe-Key, Eingabe-Wert, Ausgabe-Key, Ausgabe-Wert
public class AverageGradeReducer extends Reducer<Text, FloatWritable, Text, FloatWritable> {

	private final static Logger log = Logger.getLogger(AverageGradeReducer.class.getName());
	
	@Override
	protected void reduce(Text key, Iterable<FloatWritable> values, Context context)
			throws IOException, InterruptedException {

		// Summiere alle Noten eines Studierenden auf...
		float sum = 0;
		float count = 0;
		for (FloatWritable val : values) {
			sum +=val.get();
			count +=1;
		}
	
		// Und bilde den Durchschnitt
		float result = sum / count;
		
		log.log(Level.INFO, "Name: "+key+" Note: "+result);
		
		// Schreibe den Durschnitt für den Studierenden in key
		context.write(key, new FloatWritable(result));
		
	}
}
