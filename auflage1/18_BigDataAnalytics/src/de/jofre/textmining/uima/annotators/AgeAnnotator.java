package de.jofre.textmining.uima.annotators;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;

import de.jofre.textmining.uima.types.Age;

public class AgeAnnotator extends JCasAnnotator_ImplBase {

	private Pattern agePattern = Pattern.compile("[1-6][0-9] Jahre");

	@Override
	public void process(JCas arg0) throws AnalysisEngineProcessException {

		// Text des Dokuments abrufen
		String docText = arg0.getDocumentText();

		// Auf Muster überprüfen
		Matcher matcher = agePattern.matcher(docText);
		int pos = 0;
		while (matcher.find(pos)) {

			// Muster gefunden
			Age age = new Age(arg0);

			// Alter-Span auslesen
			age.setBegin(matcher.start());
			age.setEnd(matcher.end());

			// Füge Alter zum Index hinzu
			age.addToIndexes();

			// Weitersuchen ab pos
			pos = matcher.end();
		}
	}
}
