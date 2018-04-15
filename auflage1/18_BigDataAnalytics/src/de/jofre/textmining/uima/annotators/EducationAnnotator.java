package de.jofre.textmining.uima.annotators;

import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;

import de.jofre.textmining.uima.types.Education;

public class EducationAnnotator extends JCasAnnotator_ImplBase {

	@Override
	public void process(JCas arg0) throws AnalysisEngineProcessException {
		// Text des Dokuments abrufen
		String docText = arg0.getDocumentText();

		int educationPos = 0;
		for (int i = 0; i < EducationByName.EDUCATION.length; i++) {

			educationPos = docText.toUpperCase().indexOf(
					EducationByName.EDUCATION[i].toUpperCase());
			if (educationPos > -1) {

				Education edu = new Education(arg0);
				edu.setBegin(educationPos);
				edu.setEnd(educationPos + EducationByName.EDUCATION[i].length());
				edu.addToIndexes();
			}
		}
	}
}
