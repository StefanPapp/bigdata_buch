

/* First created by JCasGen Wed Jul 09 17:44:08 CEST 2014 */
package de.jofre.textmining.uima.types;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import org.apache.uima.jcas.tcas.Annotation;


/** 
 * Updated by JCasGen Wed Jul 09 17:44:08 CEST 2014
 * XML source: C:/Users/jfreiknecht/workspaceBigData/18_BigDataAnalytics/uima/descriptors/typeSystemDescriptor.xml
 * @generated */
public class FullName extends Annotation {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(FullName.class);
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int type = typeIndexID;
  /** @generated
   * @return index of the type  
   */
  @Override
  public              int getTypeIndexID() {return typeIndexID;}
 
  /** Never called.  Disable default constructor
   * @generated */
  protected FullName() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated
   * @param addr low level Feature Structure reference
   * @param type the type of this Feature Structure 
   */
  public FullName(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated
   * @param jcas JCas to which this Feature Structure belongs 
   */
  public FullName(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated
   * @param jcas JCas to which this Feature Structure belongs
   * @param begin offset to the begin spot in the SofA
   * @param end offset to the end spot in the SofA 
  */  
  public FullName(JCas jcas, int begin, int end) {
    super(jcas);
    setBegin(begin);
    setEnd(end);
    readObject();
  }   

  /** 
   * <!-- begin-user-doc -->
   * Write your own initialization here
   * <!-- end-user-doc -->
   *
   * @generated modifiable 
   */
  private void readObject() {/*default - does nothing empty block */}
     
 
    
  //*--------------*
  //* Feature: Gender

  /** getter for Gender - gets 
   * @generated
   * @return value of the feature 
   */
  public String getGender() {
    if (FullName_Type.featOkTst && ((FullName_Type)jcasType).casFeat_Gender == null)
      jcasType.jcas.throwFeatMissing("Gender", "de.jofre.textmining.uima.types.FullName");
    return jcasType.ll_cas.ll_getStringValue(addr, ((FullName_Type)jcasType).casFeatCode_Gender);}
    
  /** setter for Gender - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setGender(String v) {
    if (FullName_Type.featOkTst && ((FullName_Type)jcasType).casFeat_Gender == null)
      jcasType.jcas.throwFeatMissing("Gender", "de.jofre.textmining.uima.types.FullName");
    jcasType.ll_cas.ll_setStringValue(addr, ((FullName_Type)jcasType).casFeatCode_Gender, v);}    
  }

    