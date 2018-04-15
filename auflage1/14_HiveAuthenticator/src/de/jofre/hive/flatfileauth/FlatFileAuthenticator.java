package de.jofre.hive.flatfileauth;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.security.sasl.AuthenticationException;

import org.apache.hive.service.auth.PasswdAuthenticationProvider;

public class FlatFileAuthenticator implements PasswdAuthenticationProvider {

	// Store beinhaltet Kombination aus Benutzername und Passwort
	private Hashtable<String, String> store = null;
	
	// Pfad zur Datei, die die Benutzerdaten beinhaltet
	private static final String AUTH_FILE = "/usr/local/hive/users.auth";

	private final static Logger log = Logger
			.getLogger(FlatFileAuthenticator.class.getName());

	// Lesen der Benutzerdaten aus der angegebenen Datei
	private Hashtable<String, String> readAuthFile() {
		Hashtable<String, String> ht = new Hashtable<String, String>();
		BufferedReader br = null;
		String line = "";
		
		// Benutzername und Passwort müssen durch Tab getrennt werden
		String cvsSplitBy = "\t"; 

		try {
			
			// Lese jede Zeile der Benutzerdatendatei
			br = new BufferedReader(new FileReader(AUTH_FILE));
			while ((line = br.readLine()) != null) {
				String[] entry = line.split(cvsSplitBy);
				
				// ... und füge diese der Hash-Table hinzu (0=Benutzername, 1=Passwort)
				ht.put(entry[0], entry[1]);
			}

		} catch (FileNotFoundException e) {
			log.log(Level.WARNING, "Datei mit Benutzerdaten nicht gefunden in "
					+ AUTH_FILE);
			e.printStackTrace();
		} catch (IOException e) {
			log.log(Level.WARNING, "Benutzerdaten konnte nicht gelesen werden aus "
					+ AUTH_FILE);
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					log.log(Level.WARNING, "Fehler beim Schließen der Datei.");
					e.printStackTrace();
				}
			}
		}
		return ht;
	}

	public FlatFileAuthenticator() {
		File file = new File(AUTH_FILE);
		if (file.exists()) {
			log.log(Level.INFO, "Benutzerdaten aus " + AUTH_FILE
					+ "gelesen und verwendet.");
			store = readAuthFile();
		}
	}
	
	// Konvertieren des Hex-Strings aus den Benutzerdaten
	// in einen Byte-Array
	private static byte[] hexStringToByteArray(String s) {
	    int len = s.length();
	    byte[] data = new byte[len / 2];
	    for (int i = 0; i < len; i += 2) {
	        data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
	                             + Character.digit(s.charAt(i+1), 16));
	    }
	    return data;
	}

	@Override
	public void Authenticate(String user, String password)
			throws AuthenticationException {

		byte[] pwToCheck;
		try {
			
			// Konvertiere das zu überprüfende Passwort in einen
			// Byte-Array.
			pwToCheck = password.getBytes("UTF-8");
			MessageDigest md = MessageDigest.getInstance("MD5");
			byte[] pwAsMD5 = md.digest(pwToCheck);

			// Konnten die Benutzerdaten gelesen werden?
			if (store != null) {
				String pw = store.get(user);

				// Existiert ein Passwort zu dem übergebenen Benutzer?
				if (pw != null) {
					
					// Konvertiere das gelesene Passwort (als String)
					// in einen Byte-Array
					byte[] pwAsByte = hexStringToByteArray(pw);
					
					// Vergleiche beide MD5-Summen
					if (Arrays.equals(pwAsByte, pwAsMD5)) {
						
						// Summen sind gleich!
						log.log(Level.INFO, "Benutzer " + user + " erfolgreich authentifiziert.");
						return;
					}
				}
			}
		} catch (Exception e) {
			log.log(Level.SEVERE, "MD5-Encodierung konnte nicht durchgeführt werden.");
			e.printStackTrace();
		}

		// Bisher nicht per Return aus der Methode gesprungen? Dann werfe Exception, die
		// die Authentifizierung fehlschlagen lässt.
		throw new AuthenticationException("Benutzer " + user + " konnte nicht authentifiziert werden.");
	}

}
