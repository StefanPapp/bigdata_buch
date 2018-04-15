package de.jofre.grades;

import java.io.IOException;

import org.apache.hadoop.io.FloatWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

// Eingabe-Key, Eingabe-Wert, Ausgabe-Key, Ausgabe-Wert
public class GenderSpecificGradeMapper extends
		Mapper<Text, Text, Text, FloatWritable> {

	public void map(Text key, Text value, Context context)
			throws IOException, InterruptedException {

		final Text male = new Text("maennlich");
		final Text female = new Text("weiblich");

		// Extrahiere den Vornamen
		String names[] = key.toString().split(" ");

		// Ist der Name in zwei Teile zerlegbar?
		if (names.length > 0) {
			
			String pointFloat = value.toString(); //.replace(',','.');
			FloatWritable floatValue = new FloatWritable(Float.parseFloat(pointFloat));

			// Ist es ein männlicher Vorname?
			if (NamesByGender.getGender(names[0]).equals("male")) {
				context.write(male, floatValue);
			}

			// ... oder ein weiblicher?
			if (NamesByGender.getGender(names[0]).equals("female")) {
				context.write(female, floatValue);
			}

		}

	}
}
