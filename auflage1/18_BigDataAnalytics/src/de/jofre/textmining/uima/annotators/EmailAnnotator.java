package de.jofre.textmining.uima.annotators;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;

import de.jofre.textmining.uima.types.Email;

public class EmailAnnotator extends JCasAnnotator_ImplBase {

	private Pattern emailPattern = Pattern
			.compile("[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})");

	@Override
	public void process(JCas arg0) throws AnalysisEngineProcessException {
		// Text des Dokuments abrufen
		String docText = arg0.getDocumentText();
		
		// Auf Muster überprüfen
		Matcher matcher = emailPattern.matcher(docText);
		int pos = 0;
		while (matcher.find(pos)) {
			
			// Muster gefunden
			Email mail = new Email(arg0);
			
			// Email-Span auslesen
			mail.setBegin(matcher.start());
			mail.setEnd(matcher.end());
			
			// Füge Mail zum Index hinzu
			mail.addToIndexes();
			pos = matcher.end();
		}
	}
}
