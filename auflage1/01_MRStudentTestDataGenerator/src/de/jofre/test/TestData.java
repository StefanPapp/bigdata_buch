package de.jofre.test;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TestData {

	private final static Logger log = Logger.getLogger(TestData.class.getName()); 
	
	/**
	 * Generiert eine Liste von Datensätzen aus Matrikelnummer, Datum und Note
	 * in das Verzeichnis der Anwendung. 
	 * 
	 * @param args - Wird nicht verwendet
	 */
	public static void main(String[] args) {
		
		int data_counter = 2000;
		
		log.log(Level.INFO, "Schreibe "+data_counter+" Einträge...");
		try {
			Random r = new Random();
			PrintWriter out = new PrintWriter("mr_student_data.txt");
			
			int student_id;
			Date test_date = new Date();
			Calendar c = Calendar.getInstance();
			DateFormat dfmt = new SimpleDateFormat("ddMMyyyy");
			int grade;
			
			for(int i=0; i<data_counter; i++) {
				
				// Matrikelnummer
				student_id = 200000 + r.nextInt(1000);
				
				// Datum der Klausur
				c.set(Calendar.YEAR,         2000 + r.nextInt(13));
				c.set(Calendar.MONTH,        1    + r.nextInt(11));
				c.set(Calendar.DAY_OF_MONTH, 1    + r.nextInt(30));
				test_date = c.getTime();
				
				// Note
				grade = 10 + r.nextInt(40);
				
				// Schreibe Datei
				out.print(student_id);
				out.print(dfmt.format(test_date));
				out.print(grade);
				out.print(System.getProperty("line.separator"));
			}
			
			out.close();
			log.log(Level.INFO, "Fertig!");
			
		} catch (FileNotFoundException e) {
			log.log(Level.SEVERE, "Fehler beim Schreiben!");
			e.printStackTrace();
		}
	}

}
