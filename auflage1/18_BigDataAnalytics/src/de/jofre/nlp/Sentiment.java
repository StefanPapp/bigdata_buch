package de.jofre.nlp;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import opennlp.tools.doccat.DoccatModel;
import opennlp.tools.doccat.DocumentCategorizerME;
import opennlp.tools.doccat.DocumentSample;
import opennlp.tools.doccat.DocumentSampleStream;
import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.PlainTextByLineStream;

public class Sentiment {
	private static final Logger log = Logger.getLogger(Sentiment.class.getName());

	/**
	 * Trainiere ein Model auf Basis der übergebenen Trainingsdaten.
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
	 * Führe eine Stimmungsanalyse auf Basis einer Classification durch.
	 * 
	 * @param _text
	 * @return
	 */
	public static String sentimentByClassification(String _text) {
		
		DoccatModel model = null;
				
		// Muss das Modell noch trainiert werden?
		if (model == null) {
			log.log(Level.INFO, "Trainiere Modell zum ersten Gebrauch.");
			model = training("/nlp/sentimenttraining.train");
		}
		
		// Wende das Modell auf die Realdaten an
		log.log(Level.INFO, "Klassifiziere Text...");
		DocumentCategorizerME categorizer = new DocumentCategorizerME(model);
		double[] outcomes = categorizer.categorize(_text);
		String category = categorizer.getBestCategory(outcomes);
		System.out.println(categorizer.getAllResults(outcomes));
		
		return category;
	}
	
	/**
	 * Stimmungsanalyse anhand einer simplen Wortliste.
	 * 
	 * @param _text
	 * @return
	 */
	public static String sentimentByWordList(String _text) {
		int good = 0;
		int bad = 0;
		
		for(int i=0; i<SentimentWordList.badWords.length; i++) {
			if (_text.toUpperCase().indexOf(SentimentWordList.badWords[i].toUpperCase()) > -1) {
				bad++;
			}
		}
		
		for(int i=0; i<SentimentWordList.goodWords.length; i++) {
			if (_text.toUpperCase().indexOf(SentimentWordList.goodWords[i].toUpperCase()) > -1) {
				good++;
			}
		}
		
		if (bad>good) {
			return "Schlecht";
		} else {
			return "Gut";
		}
	}
}
