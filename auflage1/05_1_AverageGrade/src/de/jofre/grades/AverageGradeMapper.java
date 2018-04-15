package de.jofre.grades;

import java.io.IOException;

import org.apache.hadoop.io.FloatWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

// Eingabe-Key, Eingabe-Wert, Ausgabe-Key, Ausgabe-Wert
public class AverageGradeMapper extends Mapper<Text,Text,Text,FloatWritable> {
	
	public void map(Text key, Text value, Context context) throws IOException,
			InterruptedException {
	
			// Formatieren der Gleitkommazahlen (Ersetzen der Kommata durch Punkte)
			String pointFloat = value.toString().replace(',','.');
			FloatWritable floatValue = new FloatWritable(Float.parseFloat(pointFloat));
			
			// Hier müssen wir einfach nur die vorhandenen Daten in den Mapper einlesen
			context.write(key, floatValue);
	}
}
