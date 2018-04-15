package de.jofre.grades;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

// Eingabe-Key, Eingabe-Wert, Ausgabe-Key, Ausgabe-Wert
public class GradesMapper extends Mapper<Text,Text,IntWritable,IntWritable> {
	
	private final static Logger log = Logger.getLogger(GradesMapper.class.getName());
	
	private IntWritable year_int = null;
	private IntWritable grade_int = null;

	public void map(Text key, Text value, Context context) throws IOException,
			InterruptedException {
		
		// Auslesen des Jahres und der Note aus einem String wie "2853972308201319"
		if (key.toString().length() == 16) {
			String year_str = key.toString().substring(10,14);
			String grade_str = key.toString().substring(14,16);
			
			year_int = new IntWritable(Integer.parseInt(year_str));
			grade_int = new IntWritable(Integer.parseInt(grade_str));
			
			// Sammeln der Ergebnisse
			context.write(year_int, grade_int);
		} else {
			log.log(Level.INFO, "Ungültige Datensatzlänge entdeckt ("+key.toString().length()+").");
		}
	}
}
