package de.jofre.validators;

public class NumberValidator {
	
	/**
	 * Ist es eine Ganzzahl?
	 * 
	 * @param s
	 * @return
	 */
	public static boolean isInteger(String s) {
	    try { 
	        Integer.parseInt(s); 
	    } catch(NumberFormatException e) { 
	        return false; 
	    }
	    return true;
	}
	
	/**
	 * Ist es eine Gleitkommazahl?
	 * 
	 * @param s
	 * @return
	 */
	public static boolean isDouble(String s) {
	    try { 
	        Double.parseDouble(s); 
	    } catch(NumberFormatException e) { 
	        return false; 
	    }
	    return true;
	}
}
