package de.jofre.mysqlexport;

import java.util.Random;

public class NamesByGender {

	public static String getGender(String _name) {

		if (contains(_name, MALE_NAMES)) {
			return "male";
		}
		if (contains(_name, FEMALE_NAMES)) {
			return "female";
		}
		return "unknown";
	}

	public static String getRandomName() {
		Random r = new Random();
		int gender = r.nextInt(2);
		if (gender == 0) {
			// Männlich
			int index = r.nextInt(MALE_NAMES.length);
			return MALE_NAMES[index];
		} else {
			// Weiblich
			int index = r.nextInt(FEMALE_NAMES.length);
			return FEMALE_NAMES[index];
		}
	}

	public static String getRandomLastName() {
		Random r = new Random();
		int index = r.nextInt(LAST_NAMES.length);
		return LAST_NAMES[index];
	}

	private static boolean contains(String _str, String[] _list) {
		for (int i = 0; i < _list.length; i++) {
			if (_list[i].equalsIgnoreCase(_str)) {
				return true;
			}
		}
		return false;
	}

	public final static String[] LAST_NAMES = { "Meier", "Müller", "Schmidt",
			"Schneider", "Fischer", "Weber", "Wagner", "Becker", "Schulz",
			"Hoffmann", "Schäfer", "Koch", "Bauer", "Richter", "Klein", "Wolf",
			"Schröder", "Neumann", "Schwarz", "Zimmermann", "Braun", "Krüger",
			"Hartmann", "Lange", "Schmitt", "Krause", "Lehmann", "Schulze",
			"Köhler", "Herrmann", "König", "Huber", "Fuchs", "Peters",
			"Möller", "Weiß", "Jung", "Hahn", "Schubert", "Vogel", "Friedrich",
			"Keller", "Günther", "Frank", "Berger", "Winkler", "Roth", "Beck",
			"Lorenz", "Baumann", "Albrecht", "Schuster", "Simon", "Ludwig", "Böhm",
			"Winter", "Martin", "Schuhmacher", "Krämer"};

	public final static String[] MALE_NAMES = { "Alexander", "Andreas",
			"Benjamin", "Bernd", "Christian", "Daniel", "David", "Dennis",
			"Dieter", "Dirk", "Dominik", "Eric", "Erik", "Felix", "Florian",
			"Frank", "Jan", "Jens", "Jonas", "Jörg", "Jürgen", "Kevin",
			"Klaus", "Kristian", "Christian", "Leon", "Lukas", "Marcel",
			"Marco", "Marko", "Mario", "Markus", "Martin", "Mathias",
			"Matthias", "Max", "Maximilian", "Michael", "Mike", "Maik",
			"Niklas", "Patrick", "Paul", "Peter", "Philipp", "Phillipp",
			"Ralf", "Ralph", "René", "Robert", "Sebastian", "Stefan",
			"Stephan", "Steffen", "Sven", "Swen", "Thomas", "Thorsten",
			"Torsten", "Tim", "Tobias", "Tom", "Ulrich", "Uwe", "Wolfgang" };

	public final static String[] FEMALE_NAMES = { "Andrea", "Angelika", "Anja",
			"Anke", "Anna", "Anne", "Annett", "Antje", "Barbara", "Birgit",
			"Brigitte", "Christin", "Christina", "Christine", "Claudia",
			"Daniela", "Diana", "Doreen", "Franziska", "Gabriele", "Heike",
			"Ines", "Jana", "Janina", "Jennifer", "Jessica", "Jessika",
			"Julia", "Juliane", "Karin", "Karolin", "Katharina", "Kathrin",
			"Katrin", "Katja", "Kerstin", "Klaudia", "Claudia", "Kristin",
			"Christin", "Laura", "Lea", "Lena", "Lisa", "Mandy", "Manuela",
			"Maria", "Marie", "Marina", "Martina", "Melanie", "Monika",
			"Nadine", "Nicole", "Petra", "Sabine", "Sabrina", "Sandra", "Sara",
			"Sarah", "Silke", "Simone", "Sophia", "Sophie", "Stefanie",
			"Stephanie", "Susanne", "Tanja", "Ulrike", "Ursula", "Uta", "Ute",
			"Vanessa", "Yvonne" };
}
