package de.jofre.hbasedata.generator;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {

	private final static Logger log = Logger.getLogger(Main.class.getName());

	private static final int DATA_COUNTER = 20000;
	
	private static final String jobs[] = {"Lehrer", "Consultant", "Verkäufer", "Gärtner", "Sportler", "Finanzberater", "Musiker", "Bestatter",
		"Fahrzeughändler", "Bänker", "Informatiker", "Student", "Pastor", "Polizist", "Krankenpfleger", "Arzt", "Fahrlehrer", "Pirat", "Bürgermeister",
		"Politiker", "Geologe", "Professor", "Wissenschaftler", "Übersetzer", "Kellner", "Schneider", "Koch", "Security", "Schauspieler", "Regisseur",
		"Schuhmacher", "Müller", "Fleischer"};

	public static void main(String[] args) {

		log.log(Level.INFO, "Schreibe " + DATA_COUNTER + " Einträge...");
		try {
			Random r = new Random();

			Writer out = new BufferedWriter(new OutputStreamWriter(
				    new FileOutputStream("people.csv"), "UTF-8"));
			
			for (int i = 0; i < DATA_COUNTER; i++) {

				out.write("row" + i + ",");
				
				// Names
				String name = NamesByGender.getRandomName();
				String lastName = NamesByGender.getRandomLastName();
				out.write(name + " " + lastName + ",");

				// Property
				out.write(jobs[r.nextInt(jobs.length)]);
				
				// Umbruch
				if (i < DATA_COUNTER-1) {
					out.write(System.getProperty("line.separator"));
				}
			}

			out.close();
			log.log(Level.INFO, "Fertig!");

		} catch (Exception e) {
			log.log(Level.SEVERE, "Fehler beim Schreiben!");
			e.printStackTrace();
		}
	}

}
