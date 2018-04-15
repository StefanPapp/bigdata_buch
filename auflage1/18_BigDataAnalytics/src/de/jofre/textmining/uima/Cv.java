package de.jofre.textmining.uima;

/**
 * Einfaches POJO zum Halten der Lebenslaufdaten.
 * 
 * @author J. Freiknecht
 *
 */
public class Cv {

	private String name;
	private String email;
	private String education;
	private int age;
	private String gender;
	
	public Cv(String name, String email, String education, int age,
			String gender) {
		super();
		this.name = name;
		this.email = email;
		this.education = education;
		this.age = age;
		this.gender = gender;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getEducation() {
		return education;
	}
	public void setEducation(String education) {
		this.education = education;
	}
	public int getAge() {
		return age;
	}
	public void setAge(int age) {
		this.age = age;
	}
	public String getGender() {
		return gender;
	}
	public void setGender(String gender) {
		this.gender = gender;
	}
}
