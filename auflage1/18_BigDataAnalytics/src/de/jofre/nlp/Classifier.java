package de.jofre.nlp;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import opennlp.tools.doccat.DoccatModel;
import opennlp.tools.doccat.DocumentCategorizerME;
import opennlp.tools.doccat.DocumentSample;
import opennlp.tools.doccat.DocumentSampleStream;
import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.PlainTextByLineStream;

public class Classifier {

	private static final Logger log = Logger.getLogger(Classifier.class.getName());

	/**
	 * Speichere das Modell in einer Datei.
	 * 
	 * @param _model
	 * @param _file
	 */
	private static void saveModel(DoccatModel _model, String _file) {
		log.log(Level.INFO, "Speichere Klassifikatorenmodell nach " + _file);
		OutputStream os = null;
		try {
			os = new BufferedOutputStream(new FileOutputStream(_file));
			_model.serialize(os);
		} catch (IOException e) {
			log.log(Level.SEVERE, "Klassifikatorenmodell konnte nicht gespeichert werden.");
			e.printStackTrace();
		} finally {
			if (os != null) {
				try {
					os.close();
				} catch (IOException e) {
					log.log(Level.SEVERE, "Fehler beim Schreiben Klassifikatorenmodells.");
					e.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * Trainiere das Model mit den Trainingsdaten.
	 * 
	 * @param trainingData
	 * @return
	 */
	private static DoccatModel training(String trainingData) {
		
		DoccatModel model = null;
		
		// Lese Trainingsdaten
		log.log(Level.INFO, "Trainiere Modell...");
		InputStream is = null;
		try {
			is = Classifier.class
					.getResourceAsStream(trainingData);

			ObjectStream<String> os = new PlainTextByLineStream(is,
					"UTF-8");
			ObjectStream<DocumentSample> trainingStream = new DocumentSampleStream(
					os);

			// Trainieren ein deutschsprachiges Modell damit
			model = DocumentCategorizerME.train("de", trainingStream);
		} catch (IOException e) {
			log.log(Level.SEVERE, "Konnte Trainingsdaten nicht einlesen.");
			e.printStackTrace();
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					log.log(Level.SEVERE, "Fehler beim Lesen der Trainingsdaten.");
					e.printStackTrace();
				}
			}
		}
		
		// Optional: Speichere Modell
		// saveModel(model, "C:\\de-doccat.bin");
		
		return model;
	}
	
	/**
	 * Klassifiziere den übergebenen Text und liefere die Klasse zurück.
	 * 
	 * @param _text
	 * @return
	 */
	public static String classify(String _text) {
		
		DoccatModel model = null;
		
		// Optional: Laden eines Modells
		/*InputStream is = null;
		try {
			is = new FileInputStream("C:\\de-doccat.bin");
			model = new DoccatModel(is);
		} catch (Exception e) {
			log.log(Level.SEVERE, "Fehler beim Laden des Modells.");
			e.printStackTrace();
		}*/
		
		// Muss das Modell noch trainiert werden?
		if (model == null) {
			log.log(Level.INFO, "Trainiere Modell zum ersten Gebrauch.");
			model = training("/nlp/training.train");
		}
		
		// Wende das Modell auf die Realdaten an
		log.log(Level.INFO, "Klassifiziere Text '" + _text + "'...");
		DocumentCategorizerME categorizer = new DocumentCategorizerME(model);
		double[] outcomes = categorizer.categorize(_text);
		String category = categorizer.getBestCategory(outcomes);
		
		return category;
	}
}
