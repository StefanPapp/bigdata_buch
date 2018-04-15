package de.jofre.validators;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DateValidator {
	// Gängige Datumsformate
		private static List<SimpleDateFormat> dateFormats = new ArrayList<SimpleDateFormat>() {{
	        add(new SimpleDateFormat("M/dd/yyyy"));
	        add(new SimpleDateFormat("dd.M.yyyy"));
	        add(new SimpleDateFormat("dd.mm.yyyy"));
	        add(new SimpleDateFormat("dd.mm.yy"));
	        add(new SimpleDateFormat("mm/dd/yyyy"));
	        add(new SimpleDateFormat("M/dd/yyyy hh:mm:ss"));
	        add(new SimpleDateFormat("dd.M.yyyy hh:mm:ss"));
	        add(new SimpleDateFormat("dd.MMM.yyyy"));
	        add(new SimpleDateFormat("dd-MMM-yyyy"));
			add(new SimpleDateFormat("dd. MM yyyy"));
			add(new SimpleDateFormat("yyyy-MM-dd"));
		}};
		
		/**
		 * Passt eines der Datumsformate? Dann liefere ein Datum zurück,
		 * andernfalls null.
		 * 
		 * @param date
		 * @return
		 */
		public static Date isDate(String date) {
			Date testDate = null;
	        for (SimpleDateFormat format : dateFormats) {
	            try {
	                format.setLenient(false);
	                testDate = format.parse(date);
	            } catch (ParseException e) {
	            }
	            if (testDate != null) {
	            	return testDate;
	            }
	        }
	        return null;
		}
		
		public static boolean isYear(String input) {
			if (NumberValidator.isInteger(input)) {
				Integer year = Integer.parseInt(input);
				if ((year >= 0) && (year < 9999)) {
					return true;
				}
			}
			return false;
		}
}
