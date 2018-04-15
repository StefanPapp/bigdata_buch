package de.jofre.textmining.uima;

import java.net.URL;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.uima.UIMAFramework;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.cas.text.AnnotationIndex;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.ResourceSpecifier;
import org.apache.uima.util.XMLInputSource;

import de.jofre.textmining.uima.types.Age;
import de.jofre.textmining.uima.types.Education;
import de.jofre.textmining.uima.types.Email;
import de.jofre.textmining.uima.types.FullName;

public class CVAnalyzer {

	private static final Logger log = Logger.getLogger(CVAnalyzer.class.getName());
	
	/**
	 * Aufruf der AE zur Analyse der Texte mittels UIMA.
	 * 
	 * @param _cvText
	 * @return
	 */
	public static Cv analyzeCV(String _cvText) {

		Cv cv = null;

		// Lade Engine-Definition
		ResourceSpecifier specifier = null;
		try {
			URL url = CVAnalyzer.class.getResource("/uima/descriptors/CVAEDescriptor.xml");
			XMLInputSource in = new XMLInputSource(url);
			specifier = UIMAFramework.getXMLParser().parseResourceSpecifier(in);
		} catch (Exception e) {
			log.log(Level.SEVERE, "Fehler beim Lesen des UIMA-AE-Descriptors.");
			e.printStackTrace();
			return null;
		}

		// Initialisieren Analysis Engine
		AnalysisEngine ae = null;
		try {
			ae = UIMAFramework.produceAnalysisEngine(specifier);
		} catch (ResourceInitializationException e) {
			log.log(Level.SEVERE, "Fehler beim Initialisieren der AE.");
			e.printStackTrace();
			return null;
		}
		
		// Führe Analyse aus
		JCas jcas;
		try {
			log.log(Level.INFO, "Beginne mit der Analyse des CVs.");
			jcas = ae.newJCas();
			
			// Analysiere den Text
			jcas.setDocumentText(_cvText);
			ae.process(jcas);
			
			// Frage alle Funde ab
			AnnotationIndex<Annotation> emails = jcas.getAnnotationIndex(Email.type);
			AnnotationIndex<Annotation> ages = jcas.getAnnotationIndex(Age.type);
			AnnotationIndex<Annotation> names = jcas.getAnnotationIndex(FullName.type);
			AnnotationIndex<Annotation> educations = jcas.getAnnotationIndex(Education.type);
			
			// Extrahiere jeweis ersten Fund
			Email email = null;
			FullName name = null;
			Education education = null;
			Age age = null;
			String emailString = "";
			String nameString = "";
			String genderString = "";
			String educationString = "";
			String ageString = "0";
			Iterator emailIterator = emails.iterator();
			if (emailIterator.hasNext()) {
				log.log(Level.INFO, "Es wurde eine Email im Dokument gefunden!");
				email = (Email)emailIterator.next();
				emailString = email.getCoveredText();
			}
			Iterator nameIterator = names.iterator();
			if (nameIterator.hasNext()) {
				log.log(Level.INFO, "Es wurde ein Name im Dokument gefunden!");
				name = (FullName)nameIterator.next();
				nameString = name.getCoveredText();
				genderString = name.getGender();
			}
			Iterator educationIterator = educations.iterator();
			if (educationIterator.hasNext()) {
				log.log(Level.INFO, "Es wurde eine Ausbildung im Dokument gefunden!");
				education = (Education)educationIterator.next();
				educationString = education.getCoveredText();
			}
			Iterator ageIterator = ages.iterator();
			if (ageIterator.hasNext()) {
				log.log(Level.INFO, "Es wurde ein Alter im Dokument gefunden!");
				age = (Age)ageIterator.next();
				// Entfernen der Zeichenkette " Jahre"
				ageString = age.getCoveredText().replace(" Jahre", "");
			}			
			
			// Konstruiere CV-Instanz
			cv = new Cv(nameString, emailString, educationString, Integer.parseInt(ageString), genderString);
			
			// Räume auf
			jcas.reset();
			ae.destroy();
		} catch (Exception e) {
			log.log(Level.SEVERE, "Fehler beim Analysieren des CVs.");
			e.printStackTrace();
		}
		  
		return cv;
	}
}
