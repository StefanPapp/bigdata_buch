package de.jofre.textmining.uima.annotators;

import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;

import de.jofre.textmining.uima.types.FullName;

public class NameAnnotator extends JCasAnnotator_ImplBase {

	@Override
	public void process(JCas arg0) throws AnalysisEngineProcessException {
		// Text des Dokuments abrufen
		String docText = arg0.getDocumentText();
		
		int firstNameStart = 0;
		int lastNameStart = 0;
		
		// Mädchennamen
		for(int i=0; i<NamesByGender.FEMALE_NAMES.length; i++) {
			firstNameStart = docText.indexOf(NamesByGender.FEMALE_NAMES[i]);
			if (firstNameStart > -1) {
				
				for(int j=0; j<NamesByGender.LAST_NAMES.length; j++) {
				
					lastNameStart = docText.indexOf(NamesByGender.LAST_NAMES[j]);
					if (lastNameStart == firstNameStart + NamesByGender.FEMALE_NAMES[j].length() + 1) {
						FullName name = new FullName(arg0);
						name.setBegin(firstNameStart);
						name.setEnd(firstNameStart + NamesByGender.FEMALE_NAMES[i].length() + NamesByGender.LAST_NAMES[j].length() + 1);
						name.setGender("FEMALE");
						name.addToIndexes();
					}
				}
			}
		}
		
		// Jungennamen
		for(int i=0; i<NamesByGender.MALE_NAMES.length; i++) {
			firstNameStart = docText.indexOf(NamesByGender.MALE_NAMES[i]);
			if (firstNameStart > -1) {
				
				for(int j=0; j<NamesByGender.LAST_NAMES.length; j++) {
				
					lastNameStart = docText.indexOf(NamesByGender.LAST_NAMES[j]);
					if (lastNameStart == firstNameStart + NamesByGender.MALE_NAMES[j].length() + 1) {
						FullName name = new FullName(arg0);
						name.setBegin(firstNameStart);
						name.setEnd(firstNameStart + NamesByGender.MALE_NAMES[i].length() + NamesByGender.LAST_NAMES[j].length() + 1);
						name.setGender("MALE");
						name.addToIndexes();
					}
				}
			}
		}		
	}
}
