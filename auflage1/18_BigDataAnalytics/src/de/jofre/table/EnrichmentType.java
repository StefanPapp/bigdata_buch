package de.jofre.table;

public enum EnrichmentType {
	FAMOUSPERSON("Bekannte Persönlichkeit"),
	BIRTHDATE_OF_FAMOUS_PERSON ("Geburtstag einer bekannten Persönlichkeit"),
	CITY ("Stadt"),
	GEO_COORDINATES ("Geo-Koodrdinaten"),
	COMPANY("Firma"),
	COUNTRY("Land"),
	DOMAIN("Top-Level-Domain"),
	LANGUAGES_IN_COUNTRY("Länder mit dieser Sprache"),
	LANGUAGE("Sprache"),
	NONE("Keine");
	
	private String caption;
	
    private EnrichmentType(String caption) {
        this.caption = caption;
    }
    
    public String getCaption() {
		return caption;
	}
    
    public static EnrichmentType getTypeByCaption(String text) {
    	if (text.equals(FAMOUSPERSON.caption)) return FAMOUSPERSON;
    	if (text.equals(BIRTHDATE_OF_FAMOUS_PERSON.caption)) return BIRTHDATE_OF_FAMOUS_PERSON;
    	if (text.equals(CITY.caption)) return CITY;
    	if (text.equals(GEO_COORDINATES.caption)) return GEO_COORDINATES;
    	if (text.equals(COMPANY.caption)) return COMPANY;
    	if (text.equals(COUNTRY.caption)) return COUNTRY;
    	if (text.equals(DOMAIN.caption)) return DOMAIN;
    	if (text.equals(LANGUAGES_IN_COUNTRY.caption)) return LANGUAGES_IN_COUNTRY;
    	if (text.equals(LANGUAGE.caption)) return LANGUAGE;
    	if (text.equals(NONE.caption)) return NONE;
    	return null;
    }
}
