package de.jofre.hive.udf;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.hadoop.hive.ql.exec.UDF;
import org.apache.hadoop.hive.ql.udf.UDFType;
import org.apache.hadoop.io.Text;

import com.cybozu.labs.langdetect.Detector;
import com.cybozu.labs.langdetect.DetectorFactory;
import com.cybozu.labs.langdetect.LangDetectException;

@UDFType(stateful = true)
public class LanguageDetect extends UDF {

	// Basiert auf https://code.google.com/p/language-detection/
	
	private final static Logger log = Logger.getLogger(LanguageDetect.class
			.getName());
	
	public LanguageDetect() {
		log.log(Level.INFO, "Initialisiere Sprachbefehle...");
		try {
			DetectorFactory.loadProfile("/usr/local/hive/lib/langprofiles");
			
		} catch (LangDetectException e) {
			log.log(Level.SEVERE, "Fehler beim Initialisieren der Sprachprofile.");
			e.printStackTrace();
		}
		log.log(Level.INFO, "Es können nun " + DetectorFactory.getLangList().size()+" Sprache erkannt werden.");
	}
	
	
	public Text evaluate(Text input) {
		
		log.log(Level.INFO, "Versuche '" + input.toString()+"' zu erkennen...");
		Detector detector;
		try {
			detector = DetectorFactory.create();
			if (detector != null) {
				detector.append(input.toString());
			}
			
			String lang = detector.detect();
			log.log(Level.INFO, "Sprache '" + lang + "' erkannt.");
			return new Text(lang);			
		} catch (LangDetectException e) {
			log.log(Level.SEVERE, "Fehler beim Erzeugen des Sprachdetektors.");
			e.printStackTrace();
		}
		
		return new Text("");

	}
}
