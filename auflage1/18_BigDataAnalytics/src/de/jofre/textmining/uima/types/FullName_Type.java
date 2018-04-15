
/* First created by JCasGen Wed Jul 09 17:44:08 CEST 2014 */
package de.jofre.textmining.uima.types;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.cas.impl.CASImpl;
import org.apache.uima.cas.impl.FSGenerator;
import org.apache.uima.cas.FeatureStructure;
import org.apache.uima.cas.impl.TypeImpl;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.impl.FeatureImpl;
import org.apache.uima.cas.Feature;
import org.apache.uima.jcas.tcas.Annotation_Type;

/** 
 * Updated by JCasGen Wed Jul 09 17:44:08 CEST 2014
 * @generated */
public class FullName_Type extends Annotation_Type {
  /** @generated 
   * @return the generator for this type
   */
  @Override
  protected FSGenerator getFSGenerator() {return fsGenerator;}
  /** @generated */
  private final FSGenerator fsGenerator = 
    new FSGenerator() {
      public FeatureStructure createFS(int addr, CASImpl cas) {
  			 if (FullName_Type.this.useExistingInstance) {
  			   // Return eq fs instance if already created
  		     FeatureStructure fs = FullName_Type.this.jcas.getJfsFromCaddr(addr);
  		     if (null == fs) {
  		       fs = new FullName(addr, FullName_Type.this);
  			   FullName_Type.this.jcas.putJfsFromCaddr(addr, fs);
  			   return fs;
  		     }
  		     return fs;
        } else return new FullName(addr, FullName_Type.this);
  	  }
    };
  /** @generated */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = FullName.typeIndexID;
  /** @generated 
     @modifiable */
  @SuppressWarnings ("hiding")
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("de.jofre.textmining.uima.types.FullName");
 
  /** @generated */
  final Feature casFeat_Gender;
  /** @generated */
  final int     casFeatCode_Gender;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public String getGender(int addr) {
        if (featOkTst && casFeat_Gender == null)
      jcas.throwFeatMissing("Gender", "de.jofre.textmining.uima.types.FullName");
    return ll_cas.ll_getStringValue(addr, casFeatCode_Gender);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setGender(int addr, String v) {
        if (featOkTst && casFeat_Gender == null)
      jcas.throwFeatMissing("Gender", "de.jofre.textmining.uima.types.FullName");
    ll_cas.ll_setStringValue(addr, casFeatCode_Gender, v);}
    
  



  /** initialize variables to correspond with Cas Type and Features
	 * @generated
	 * @param jcas JCas
	 * @param casType Type 
	 */
  public FullName_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_Gender = jcas.getRequiredFeatureDE(casType, "Gender", "uima.cas.String", featOkTst);
    casFeatCode_Gender  = (null == casFeat_Gender) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_Gender).getCode();

  }
}



    