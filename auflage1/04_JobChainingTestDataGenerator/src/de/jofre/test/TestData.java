package de.jofre.test;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TestData {

private final static Logger log = Logger.getLogger(TestData.class.getName()); 
	
	/**
	 * Generiert Testdaten der Form "Vorname Nachname \t Note"
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		
		int data_counter = 2000;
		final float minX = 1.0f;
		final float maxX = 5.0f;
		
		log.log(Level.INFO, "Schreibe "+data_counter+" Studenten...");
		try {
			Random r = new Random();
			PrintWriter out = new PrintWriter("mr_job_chaining_data.txt");
			
			for(int i=0; i<data_counter; i++) {
				
				int randomGrades = r.nextInt(10);
				
				// Füge mehrere Einträge für einen Namen hinzu
				String name = NamesByGender.getRandomName();
				String lastName = NamesByGender.getRandomLastName();
				
				for (int j=0; j<randomGrades; j++) {
					
					// Schreibe Datei
					out.print(name+" ");
					out.print(lastName);
					out.print("\t");
					out.print(String.format("%.1f", r.nextFloat() * (maxX - minX) + minX));
					out.print(System.getProperty("line.separator"));
				}
			}
			
			out.close();
			log.log(Level.INFO, "Fertig!");
			
		} catch (FileNotFoundException e) {
			log.log(Level.SEVERE, "Fehler beim Schreiben!");
			e.printStackTrace();
		}
	}

}
