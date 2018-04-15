package de.jofre.validators;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.io.IOUtils;

import com.cybozu.labs.langdetect.Detector;
import com.cybozu.labs.langdetect.DetectorFactory;
import com.cybozu.labs.langdetect.LangDetectException;

public class LanguageValidator {

	private final static Logger log = Logger.getLogger(LanguageValidator.class
			.getName());
	
	private final static String LANGUAGES[] = {
		"af","ar","bg","bn","cs","da","de","el","en","es","et","fa","fi","fr","gu","he","hi","hr","hu",
		"id","it","ja","kn","ko","lt","lv","mk","ml","mr","ne","nl","no","pa","pl","pt","ro","ru","sk","sl",
		"so","sq","sv","sw","ta","te","th","tl","tr","uk","ur","vi","zh-cn","zh-tw"
	};

	private static final String PREFIX = "tempFileFromStream";
	private static final String SUFFIX = ".tmp";

	private static File streamToFile(InputStream in){
		File tempFile = null;
		try {
			tempFile = File.createTempFile(PREFIX, SUFFIX);
			tempFile.deleteOnExit();
			FileOutputStream out = new FileOutputStream(tempFile);
			IOUtils.copy(in, out);
		} catch (IOException e) {
			log.log(Level.SEVERE, "Fehler beim Erzeugen eines Language-Files.");
			e.printStackTrace();
		}
		return tempFile;
	}
	
	public LanguageValidator() {
		
		if (DetectorFactory.getLangList().size() > 0) return;
		
		// Initialisierung
		log.log(Level.INFO, "Initialisiere Sprachenerkennung...");
		try {
			List<String> jsonProfiles = new ArrayList<String>();
			for(int i=0; i<LANGUAGES.length; i++) {
				InputStream is = LanguageValidator.class.getResourceAsStream("/languages/"+LANGUAGES[i]);
				BufferedReader reader = new BufferedReader(new InputStreamReader(is));
				String line = reader.readLine();
				while(line != null) {
					jsonProfiles.add(line);
					line = reader.readLine();
				}
				reader.close();
				is.close();
			}
			DetectorFactory.loadProfile(jsonProfiles);
		} catch (Exception e) {
			log.log(Level.SEVERE,
					"Fehler beim Initialisieren der Sprachprofile.");
			e.printStackTrace();
		}
		
		log.info("Es wurden " + DetectorFactory.getLangList().size() + " Sprachen geladen.");
	}

	/**
	 * Ermittle die Sprache des Eingabestrings.
	 * 
	 * @param input
	 * @return
	 */
	public String getLanguage(String input) {

		// Erkennung
		log.log(Level.INFO, "Versuche '" + input.toString()
				+ "' zu erkennen...");
		Detector detector;
		try {
			detector = DetectorFactory.create();
			if (detector != null) {
				detector.append(input.toString());
			}

			String lang = detector.detect();
			log.log(Level.INFO, "Sprache '" + lang + "' erkannt.");
			return lang;
		} catch (LangDetectException e) {
			log.log(Level.SEVERE, "Fehler beim Erzeugen des Sprachdetektors.");
			e.printStackTrace();
		}

		return "";
	}
}
