package de.jofre.table;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import de.jofre.types.City;
import de.jofre.types.Company;
import de.jofre.types.Country;
import de.jofre.types.Person;
import de.jofre.validators.CompanyValidator;
import de.jofre.validators.DateValidator;
import de.jofre.validators.GeoValidator;
import de.jofre.validators.LanguageValidator;
import de.jofre.validators.NumberValidator;
import de.jofre.validators.PeopleValidator;

/**
 * Tabelle, die CSV, HTML und Diagramm-Input-Datensätze aus
 * den darin enthaltenen Daten erzeugen kann.
 * 
 * @author J. Freiknecht
 *
 */
public class BDTable {

	private List<List<String>> cells;
	private List<String> columnTitles;

	private final static Logger log = Logger.getLogger(BDTable.class.getName());

	public List<List<String>> getCells() {
		return cells;
	}

	public void setCells(List<List<String>> cells) {
		this.cells = cells;
	}

	public List<String> getColumnTitles() {
		return columnTitles;
	}

	public void setColumnTitles(List<String> columnTitles) {
		this.columnTitles = columnTitles;
	}

	public BDTable() {
		super();
		cells = new ArrayList<List<String>>();
		columnTitles = new ArrayList<String>();
	}

	public void addRow(List<String> row) {
		cells.add(row);
	}

	public void deleteRow(int row) {
		cells.remove(row);
	}

	public void addColumn(String title) {
		columnTitles.add(title);
		for (int i = 0; i < cells.size(); i++) {
			cells.get(i).add(null);
		}
	}

	public void readTableFromText(String text, String splitter, boolean headLine) {
		int startLine = 0;
		String[] lines = text.split(System.lineSeparator());

		if (headLine) {
			System.out.println("Lese Daten mit Kopfzeile");
			startLine = 1;
			columnTitles.clear();
			if (lines.length > 0) {
				String[] titles = lines[0].split(splitter);
				for (int i = 0; i < titles.length; i++) {
					columnTitles.add(titles[i]);
					System.out.println("Adde Kopfzeile: " + titles[i]);
				}
			}
		} else {
			System.out.println("Lese Daten ohne Kopfzeile.");
			columnTitles.clear();
			if (lines.length > 0) {
				int columns = lines[0].split(splitter).length;
				for (int i = 0; i < columns; i++) {
					columnTitles.add("Spalte " + i);
					System.out.println("Adde Kopfzeile: " + "Spalte " + i);
				}
			}
		}

		for (int i = startLine; i < lines.length; i++) {
			if (!lines[i].trim().equals("")) {
				String[] items = lines[i].split(splitter);
				List<String> newLine = new ArrayList<String>(
						Arrays.asList(items));
				this.addRow(newLine);
			}
		}
	}

	public String toCSV(boolean headersincluded) {
		StringBuilder sb = new StringBuilder();

		// Headerzeile enthalten?
		if ((this.columnTitles.size() > 0) && (headersincluded)) {
			for (int i = 0; i < columnTitles.size(); i++) {
				sb.append(columnTitles.get(i));
				if (i < columnTitles.size() - 1) {
					sb.append("\t");
				} else {
					sb.append(System.lineSeparator());
				}
			}
		}

		for (int i = 0; i < cells.size(); i++) {
			for (int j = 0; j < cells.get(i).size(); j++) {
				sb.append(cells.get(i).get(j));
				if (j < cells.get(i).size() - 1) {
					sb.append("\t");
				} else {
					sb.append(System.lineSeparator());
				}
			}
		}
		// Trim kümmert sich um leere Headerzeile und leere letzte Zeile
		return sb.toString().trim();
	}

	public String toHTMLTable() {

		StringBuilder sb = new StringBuilder(
				"<table style=\"width: 100%; border: 1px solid #4F4F4F; border-collapse: collapse;\" >\n");

		// Spaltennamen
		sb.append("\t<tr style=\"border: 1px solid silver;\">");
		for (int i = 0; i < columnTitles.size(); i++) {
			sb.append("<td style=\"border: 1px solid silver;\"><b>"
					+ columnTitles.get(i) + "</b></td>");
		}
		sb.append("</tr>\n");

		// Daten
		for (int i = 0; i < cells.size(); i++) {
			sb.append("\t<tr style=\"border: 1px solid silver;\">");
			for (int j = 0; j < cells.get(i).size(); j++) {
				sb.append("<td style=\"border: 1px solid silver;\">"
						+ cells.get(i).get(j).toString() + "</td>");
			}
			sb.append("</tr>\n");
		}

		sb.append("</table>");

		return sb.toString();
	}

	/**
	 * Zählt die Einträge aus dataColumn und gibt diese als Key-Value-Pairs für
	 * die Visualisierung in einer BubbleChart (und allen Diagrammen, die ein
	 * ähnliches Format verwenden) zurück.
	 * 
	 * @param dataColumn
	 * @return
	 */
	public String toBubbleChartData(int dataColumn, int counterColumn,
			boolean useCounterColumn) {

		// Zählen Datensätz in der dataColumn
		Map<String, Integer> count = new HashMap<String, Integer>();
		int counterAdd = 1;
		for (int i = 0; i < cells.size(); i++) {
			String data = cells.get(i).get(dataColumn);
			if (useCounterColumn) {
				if (NumberValidator.isInteger(cells.get(i).get(counterColumn))) {
					counterAdd = Integer.parseInt(cells.get(i).get(
							counterColumn));
				} else if (NumberValidator.isDouble(cells.get(i).get(
						counterColumn))) {

					// Nicht genau
					counterAdd = (int) Double.parseDouble(cells.get(i).get(
							counterColumn));
				}
			}

			if (count.containsKey(data)) {
				count.put(data, count.get(data) + counterAdd);
			} else {
				count.put(data, counterAdd);
			}
		}

		// Konstruiere Eingabe
		StringBuilder sb = new StringBuilder();
		sb.append("{ \"name\":\"data\", \"children\":[");
		Iterator it = count.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<String, Integer> pairs = (Map.Entry<String, Integer>) it
					.next();
			sb.append("{\"name\": \"" + pairs.getKey() + "\", \"size\": "
					+ pairs.getValue() + "},");
		}
		if (sb.charAt(sb.length() - 1) == ',') {
			sb.deleteCharAt(sb.length() - 1);
		}
		sb.append("]}");

		return sb.toString();
	}

	/**
	 * Erstellt Eingabedaten für das CalendarChart.
	 * 
	 * @param dateColumn
	 *            In welcher Spalte liegt das Alter?
	 * @param dataColumn
	 *            In welcher Spalte liegen die nummerischen Daten?
	 * @return
	 */
	public String toCalendarChartData(int dateColumn, int dataColumn) {

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

		// Suche den Maximalwert aus der dataColumn
		double max = 0;
		for (int i = 0; i < cells.size(); i++) {
			double d = Double.parseDouble(cells.get(i).get(dataColumn));
			if (d > max) {
				max = d;
			}
		}

		// Konstruiere Eingabe
		StringBuilder sb = new StringBuilder();
		sb.append("[");
		for (int i = 0; i < cells.size(); i++) {
			Date d = DateValidator.isDate(cells.get(i).get(dateColumn));
			if (d != null) {
				sb.append("{\"Date\":\"" + sdf.format(d) + "\",\"Min\":\""
						+ cells.get(i).get(dataColumn) + "\",\"Max\":\"" + max
						+ "\"}");
				if (i < cells.size() - 1) {
					sb.append(",");
				}
			}
		}
		sb.append("]");

		return sb.toString();
	}

	/**
	 * Generiert Eingabedaten für eine Koroplethenkarte.
	 * 
	 * @param regionColumn
	 *            : Spalte mit Bundesländern
	 * @param dataColumn
	 *            : Spalte mit nummerischen Daten
	 * @return
	 */
	public String toChoroplethData(int regionColumn, int dataColumn) {
		// Konstruiere Eingabe
		StringBuilder sb = new StringBuilder();
		sb.append("[");
		for (int i = 0; i < cells.size(); i++) {
			sb.append("{\"id\":\"" + cells.get(i).get(regionColumn)
					+ "\", \"rate\":" + cells.get(i).get(dataColumn) + "}");
			if (i < cells.size() - 1) {
				sb.append(",");
			}
		}
		sb.append("]");

		return sb.toString();
	}

	/**
	 * 
	 * Generiere Daten für ein Flare-Chart.
	 * 
	 * @param dataColumn
	 *            : Enthält das Schlüsselelement, das auf dem Kreis aufgetragen
	 *            wird.
	 * @param relColumn
	 *            : Enthält die Beziehungen vom Schlüsselelement. Alle Items in
	 *            relColumn müssen auch in dataColumn auftreten.
	 * @return
	 */
	public String toFlareChartData(int dataColumn, int relColumn) {

		Map<String, String> relations = new HashMap<String, String>();
		for (int i = 0; i < cells.size(); i++) {
			String item = cells.get(i).get(dataColumn);
			String relation = cells.get(i).get(relColumn);

			if (relations.containsKey(item)) {
				relations.put(item, relations.get(item) + ",\"" + relation
						+ "\"");
			} else {
				relations.put(item, "\"" + relation + "\"");
			}
		}

		// Konstruiere Eingabe
		StringBuilder sb = new StringBuilder();
		sb.append("[");
		Iterator it = relations.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<String, String> pairs = (Map.Entry<String, String>) it
					.next();
			sb.append("{\"name\": \"" + pairs.getKey() + "\", \"rellinks\": ["
					+ pairs.getValue() + "]},");
		}
		if (sb.charAt(sb.length() - 1) == ',') {
			sb.deleteCharAt(sb.length() - 1);
		}
		sb.append("]");

		return sb.toString();
	}

	/**
	 * Generiere Daten für ein 3D-GlobeChart
	 * 
	 * @param yearColumn
	 *            : Spalte, die das Jahr der Daten enthält.
	 * @param longColumn
	 *            : Spalte den LÄNGENGRADenthält
	 * @param latColumn
	 *            : Spalte den BREITENGRAD enthält
	 * @param dataColumn
	 *            : Spalte, die die nummerischen Daten enthält
	 * @return
	 */
	public String toGlobeChartData(int yearColumn, int longColumn,
			int latColumn, int dataColumn) {

		Map<String, String> data = new HashMap<String, String>();
		for (int i = 0; i < cells.size(); i++) {
			String year = cells.get(i).get(yearColumn);
			String longitude = cells.get(i).get(longColumn);
			String latitude = cells.get(i).get(latColumn);
			String counter = cells.get(i).get(dataColumn);

			if (data.containsKey(year)) {
				data.put(year, data.get(year) + "," + longitude + ","
						+ latitude + "," + counter);
			} else {
				data.put(year, longitude + "," + latitude + "," + counter);
			}
			
			/*if (i<cells.size()-1) {
				data.put(year, data.get(year) + ",");
			}*/
		}

		// Konstruiere Eingabe
		StringBuilder sb = new StringBuilder();
		sb.append("[");
		Iterator it = data.entrySet().iterator();
		while (it.hasNext()) {
			// ["1990",[6,159,0.001,30,99,0.002, ...]
			Map.Entry<String, String> pairs = (Map.Entry<String, String>) it
					.next();
			sb.append("[\"" + pairs.getKey() + "\", [" + pairs.getValue()
					+ "]],");
		}
		if (sb.charAt(sb.length() - 1) == ',') {
			sb.deleteCharAt(sb.length() - 1);
		}
		sb.append("]");

		return sb.toString();
	}

	/**
	 * Generiert Eingabedaten für ein LineChart. dataColumn wird in x-
	 * counterColumn in y-Richtung dargestellt.
	 * 
	 * @param dataColumn
	 * @param counterColumn
	 * @return
	 */
	public String toLineChartData(int dataColumn, int counterColumn) {

		SimpleDateFormat sdf = new SimpleDateFormat("d-MMM-yy", Locale.US);
		// Konstruiere Eingabe
		StringBuilder sb = new StringBuilder();
		sb.append("[");
		for (int i = 0; i < cells.size(); i++) {

			Date d = DateValidator.isDate(cells.get(i).get(dataColumn));

			if (d != null) {
				sb.append("{\"date\":\"" + sdf.format(d) + "\", \"value\":\""
						+ cells.get(i).get(counterColumn) + "\"}");
				if (i < cells.size() - 1) {
					sb.append(",");
				}
			}
		}
		sb.append("]");

		return sb.toString();
	}

	/**
	 * Generiert Eingabedaten für eine WordCloud
	 * 
	 * @param dataColumn
	 * @return
	 */
	public String toWordCloudData(int dataColumn) {

		// Konstruiere Eingabe
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < cells.size(); i++) {
			sb.append("\"" + cells.get(i).get(dataColumn) + "\"");
			if (i < cells.size() - 1) {
				sb.append(",");
			}
		}

		return sb.toString();
	}

	/**
	 * Durch welche Daten kann eine Spalte der Eingabetabelle angereichert
	 * werden?
	 * 
	 * @param column
	 * @return
	 */
	public List<EnrichmentType> getEnrichmentOptions(String sample) {

		log.log(Level.INFO, "Erfrage Anreicherungsoptionen für Datensatz '"
				+ sample + "'.");

		List<EnrichmentType> types = new ArrayList<EnrichmentType>();

		if (DateValidator.isDate(sample) != null) {
			types.add(EnrichmentType.BIRTHDATE_OF_FAMOUS_PERSON);
		}

		if (GeoValidator.isLongLatFormat(sample)) {
			types.add(EnrichmentType.GEO_COORDINATES);
		}

		if (!NumberValidator.isDouble(sample)
				&& !NumberValidator.isInteger(sample) && (DateValidator.isDate(sample) == null)) {
			types.add(EnrichmentType.CITY);
			types.add(EnrichmentType.COMPANY);
			types.add(EnrichmentType.COUNTRY);
			types.add(EnrichmentType.DOMAIN);
			types.add(EnrichmentType.FAMOUSPERSON);
			types.add(EnrichmentType.LANGUAGES_IN_COUNTRY);
			types.add(EnrichmentType.LANGUAGE);
		}

		if (types.size() == 0) {
			types.add(EnrichmentType.NONE);
		}

		return types;
	}

	/**
	 * Reichere Daten auf Basis der angegebenen Spalte an.
	 * 
	 * @param column
	 * @param type
	 *            : Durch welche Art von Daten soll die Spalte angereichert
	 *            werden?
	 * @param place
	 *            : Wo sollen die angereicherten Daten eingetragen werden?
	 */
	public void enrichColumn(int column, EnrichmentType type,
			EnrichmentPlace place) {

		if (type == EnrichmentType.NONE)
			return;

		log.log(Level.INFO,
				"Reichere Spalte " + column + " um den Typ "
						+ type.getCaption() + " an.");

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		LanguageValidator lv = new LanguageValidator(); // Sollte nur einmal
														// konstruiert werden

		if (place == EnrichmentPlace.NEW_COLUMN) {
			switch (type) {
			case BIRTHDATE_OF_FAMOUS_PERSON:
				columnTitles.add("Bekannte Person geboren");
				break;
			case CITY:
				columnTitles.add("Stadtdaten");
				break;
			case COMPANY:
				columnTitles.add("Firmendaten");
				break;
			case COUNTRY:
				columnTitles.add("Landdaten");
				break;
			case DOMAIN:
				columnTitles.add("Land der Domain");
				break;
			case FAMOUSPERSON:
				columnTitles.add("Personenbeschreibung");
				break;
			case GEO_COORDINATES:
				columnTitles.add("Stadt zu Koordinaten");
				break;
			case LANGUAGES_IN_COUNTRY:
				columnTitles.add("Sprachen des Landes");
				break;
			case LANGUAGE:
				columnTitles.add("Sprache der Daten");
				break;
			}
		}

		for (int i = 0; i < cells.size(); i++) {

			String newValue = "-";

			switch (type) {
			case BIRTHDATE_OF_FAMOUS_PERSON:
				Date date = DateValidator.isDate(cells.get(i).get(column));

				if (date != null) {
					Person person = PeopleValidator.isFamousBirthday(sdf
							.format(date));
					if (person != null) {
						newValue = person.getName();
					}
				}
				break;
			case CITY:
				City city = GeoValidator.isCity(cells.get(i).get(column));

				if (city != null) {
					newValue = "(Land: " + city.getCountry()
							+ ", Bevölkerung: " + city.getPopulation() + ")";
				}
				break;
			case COMPANY:
				Company company = CompanyValidator.isCompany(cells.get(i).get(
						column));

				if (company != null) {
					newValue = "(Abkürzung: " + company.getShortName()
							+ ", Sektor: " + company.getSector()
							+ ", Industrie: " + company.getIndustry() + ")";
				}
				break;
			case COUNTRY:
				System.out
						.println("Überprüfe Land " + cells.get(i).get(column));
				Country country = GeoValidator.isCountry(cells.get(i).get(
						column));

				if (country != null) {
					System.out.println("Land gefunden");
					newValue = "(Hauptstadt: " + country.getCapital()
							+ ", Sprachen: " + country.getLanguage() + ")";
				} else {
					System.out.println("Kein land gefunden");
				}
				break;
			case DOMAIN:
				String countryName = GeoValidator.isDomain(cells.get(i).get(
						column));

				if (countryName != null) {
					newValue = countryName;
				}
				break;
			case FAMOUSPERSON:
				Person famousPerson = PeopleValidator.isFamousPerson(cells.get(
						i).get(column));

				if (famousPerson != null) {
					newValue = "(" + famousPerson.getName() + ","
							+ famousPerson.getDescription() + ")";
				}
				break;
			case GEO_COORDINATES:
				// Erwartet Format LÄNGEGRAD,BREITENGRAD
				String[] longLat = cells.get(i).get(column).split(",");
				City cityFromCoordinates = GeoValidator.isLongLatLocation(
						longLat[0].trim(), longLat[1].trim());

				if (cityFromCoordinates != null) {
					newValue = cityFromCoordinates.getCity();
				}
				break;
			case LANGUAGES_IN_COUNTRY:
				List<Country> languageIn = GeoValidator
						.isLanguageSpokenIn(cells.get(i).get(column));

				for (int j = 0; j < languageIn.size(); j++) {
					if (newValue.equals("-"))
						newValue = "";
					newValue += languageIn.get(j).getName();
					if (j < languageIn.size() - 1) {
						newValue += ", ";
					}
				}
				break;
			case LANGUAGE:
				newValue = lv.getLanguage(cells.get(i).get(column));
				break;
			}

			// Füge Daten hinzu
			if (place == EnrichmentPlace.IN_BRACKETS) {
				cells.get(i).set(column,
						cells.get(i).get(column) + " (" + newValue + ")");
			} else {
				cells.get(i).add(newValue);
			}
		}
	}

	public String getDiagramChoiceForm() {
		StringBuilder sb = new StringBuilder();

		// Konstruiere JavaScript
		sb.append("<script type=\"text/javascript\">\n");
		sb.append("function changeChart(selection) {\n");
		sb.append("var selectionValue = selection.value;\n");
		sb.append("var select1 = document.getElementById(\"column1\");\n");
		sb.append("var select2 = document.getElementById(\"column2\");\n");
		sb.append("var select3 = document.getElementById(\"column3\");\n");
		sb.append("var select4 = document.getElementById(\"column4\");\n");
		sb.append("select1.options.length = 0;\n");
		sb.append("select1.style.display = 'inline';\n");
		sb.append("select2.options.length = 0;\n");
		sb.append("select2.style.display = 'inline';\n");
		sb.append("select3.options.length = 0;\n");
		sb.append("select3.style.display = 'inline';\n");
		sb.append("select4.options.length = 0;\n");
		sb.append("select4.style.display = 'inline';\n\n");

		List<DiagramType> diagrams = getPossibleDiagrams();
		for (int i = 0; i < diagrams.size(); i++) {
			switch (diagrams.get(i)) {
			case BUBBLE_CHART:
			case COLLAPSIBLE_INTENDED_TREEVIEW:
			case COLLAPSIBLE_TREEVIEW:
			case HIERARCHY_BAR:
			case ZOOMABLE_TREEMAP:
				sb.append("if (selectionValue == \""
						+ diagrams.get(i).getName() + "\") {\n");
				// Alle Optionen, die als ID verwendet werden können
				for (int j = 0; j < this.columnTitles.size(); j++) {
					sb.append("select1.options[select1.options.length] = new Option(\""
							+ columnTitles.get(j) + "\", \"" + j + "\");\n");
				}
				// Alle Optionen, die als Zähler verwendet werden können
				for (int j = 0; j < this.columnTitles.size(); j++) {
					if (NumberValidator.isDouble(cells.get(0).get(j))
							|| NumberValidator.isInteger(cells.get(0).get(j))) {
						sb.append("select2.options[select2.options.length] = new Option(\""
								+ columnTitles.get(j) + "\", \"" + j + "\");\n");
					}
				}
				sb.append("select2.options[select2.options.length] = new Option(\""
						+ "ANZAHL(Spalte1)" + "\", \"" + "99" + "\");\n");
				sb.append("select3.style.display = 'none';\n");
				sb.append("select4.style.display = 'none';\n");
				sb.append("}\n\n");
				break;
			case WORD_CLOUD:
				sb.append("if (selectionValue == \""
						+ diagrams.get(i).getName() + "\") {\n");
				for (int j = 0; j < this.columnTitles.size(); j++) {
					sb.append("select1.options[select1.options.length] = new Option(\""
							+ columnTitles.get(j) + "\", \"" + j + "\");\n");
				}
				sb.append("select2.style.display = 'none';\n");
				sb.append("select3.style.display = 'none';\n");
				sb.append("select4.style.display = 'none';\n");
				sb.append("}\n\n");
				break;
			case CALENDAR_CHART:
				sb.append("if (selectionValue == \""
						+ diagrams.get(i).getName() + "\") {\n");
				// Eine Spalte mit Datum
				for (int j = 0; j < this.columnTitles.size(); j++) {
					if (DateValidator.isDate(cells.get(0).get(j)) != null) {
						sb.append("select1.options[select1.options.length] = new Option(\""
								+ columnTitles.get(j) + "\", \"" + j + "\");\n");
					}
				}
				// Alle Optionen, die als Zähler verwendet werden können
				for (int j = 0; j < this.columnTitles.size(); j++) {
					if (NumberValidator.isDouble(cells.get(0).get(j))
							|| NumberValidator.isInteger(cells.get(0).get(j))) {
						sb.append("select2.options[select2.options.length] = new Option(\""
								+ columnTitles.get(j) + "\", \"" + j + "\");\n");
					}
				}
				sb.append("select3.style.display = 'none';\n");
				sb.append("select4.style.display = 'none';\n");
				sb.append("}\n\n");
				break;
			case CHOROPLETH:
				sb.append("if (selectionValue == \""
						+ diagrams.get(i).getName() + "\") {\n");
				// Eine Spalte mit einer Region
				for (int j = 0; j < this.columnTitles.size(); j++) {
					if (GeoValidator.isRegion(cells.get(0).get(j)) != null) {
						sb.append("select1.options[select1.options.length] = new Option(\""
								+ columnTitles.get(j) + "\", \"" + j + "\");\n");
					}
				}
				// Alle Optionen, die als Zähler verwendet werden können
				for (int j = 0; j < this.columnTitles.size(); j++) {
					if (NumberValidator.isDouble(cells.get(0).get(j))
							|| NumberValidator.isInteger(cells.get(0).get(j))) {
						sb.append("select2.options[select2.options.length] = new Option(\""
								+ columnTitles.get(j) + "\", \"" + j + "\");\n");
					}
				}
				sb.append("select3.style.display = 'none';\n");
				sb.append("select4.style.display = 'none';\n");
				sb.append("}\n\n");
				break;
			case GLOBE_CHART:
				sb.append("if (selectionValue == \""
						+ diagrams.get(i).getName() + "\") {\n");
				// Eine Spalte mit einer JahresZahl
				for (int j = 0; j < this.columnTitles.size(); j++) {
					if (DateValidator.isYear(cells.get(0).get(j))) {
						sb.append("select1.options[select1.options.length] = new Option(\""
								+ columnTitles.get(j) + "\", \"" + j + "\");\n");
					}
				}
				// Längen- / Breitengrad
				for (int j = 0; j < this.columnTitles.size(); j++) {
					if (GeoValidator.isLongLatFormat(cells.get(0).get(j))
							|| NumberValidator.isInteger(cells.get(0).get(j))) {
						sb.append("select2.options[select2.options.length] = new Option(\""
								+ columnTitles.get(j) + "\", \"" + j + "\");\n");
					}
				}
				// Längen- / Breitengrad
				for (int j = 0; j < this.columnTitles.size(); j++) {
					if (GeoValidator.isLongLatFormat(cells.get(0).get(j))
							|| NumberValidator.isInteger(cells.get(0).get(j))) {
						sb.append("select3.options[select3.options.length] = new Option(\""
								+ columnTitles.get(j) + "\", \"" + j + "\");\n");
					}
				}
				for (int j = 0; j < this.columnTitles.size(); j++) {
					if (NumberValidator.isDouble(cells.get(0).get(j))
							|| NumberValidator.isInteger(cells.get(0).get(j))) {
						sb.append("select4.options[select4.options.length] = new Option(\""
								+ columnTitles.get(j) + "\", \"" + j + "\");\n");
					}
				}
				sb.append("}\n\n");
				break;
			case LINE_CHART:
				sb.append("if (selectionValue == \""
						+ diagrams.get(i).getName() + "\") {\n");
				// Alle Optionen, die als ID verwendet werden können
				for (int j = 0; j < this.columnTitles.size(); j++) {
					sb.append("select1.options[select1.options.length] = new Option(\""
							+ columnTitles.get(j) + "\", \"" + j + "\");\n");
				}
				for (int j = 0; j < this.columnTitles.size(); j++) {
					if (NumberValidator.isDouble(cells.get(0).get(j))
							|| NumberValidator.isInteger(cells.get(0).get(j))) {
						sb.append("select2.options[select2.options.length] = new Option(\""
								+ columnTitles.get(j) + "\", \"" + j + "\");\n");
					}
				}
				sb.append("select3.style.display = 'none';\n");
				sb.append("select4.style.display = 'none';\n");
				sb.append("}\n\n");
				break;
			case FLARE_CHART:
				sb.append("if (selectionValue == \""
						+ diagrams.get(i).getName() + "\") {\n");
				// Alle Optionen, die als ID verwendet werden können
				for (int j = 0; j < this.columnTitles.size(); j++) {
					sb.append("select1.options[select1.options.length] = new Option(\""
							+ columnTitles.get(j) + "\", \"" + j + "\");\n");
				}
				for (int j = 0; j < this.columnTitles.size(); j++) {
					sb.append("select2.options[select2.options.length] = new Option(\""
							+ columnTitles.get(j) + "\", \"" + j + "\");\n");
				}
				sb.append("select3.style.display = 'none';\n");
				sb.append("select4.style.display = 'none';\n");
				sb.append("}\n\n");
				break;
			}
		}
		sb.append("}\n");
		sb.append("</script>\n");

		// Generiere HTML-Code
		sb.append("<form action=\"visualization.jsp\" method=\"get\">\n");
		sb.append("<select id=\"chartname\" name=\"chartname\" onChange=\"changeChart(this)\">\n");
		for (int i = 0; i < diagrams.size(); i++) {
			sb.append("<option>" + diagrams.get(i).getName() + "</option>\n");
		}
		sb.append("</select>\n");
		sb.append("<select id=\"column1\" name=\"column1\"></select>\n");
		sb.append("<select id=\"column2\" name=\"column2\"></select>\n");
		sb.append("<select id=\"column3\" name=\"column3\"></select>\n");
		sb.append("<select id=\"column4\" name=\"column4\"></select>\n");
		sb.append("<input type=\"submit\" value=\"Auswählen\">");
		sb.append("</form>\n");

		// Rufe changeChart auf
		sb.append("<script type=\"text/javascript\">changeChart(document.getElementById(\"chartname\"));</script>");
		return sb.toString();
	}

	public List<DiagramType> getPossibleDiagrams() {
		List<DiagramType> diagrams = new ArrayList<DiagramType>();

		// Die Tabelle muss mindestens über eine Zeile und eine Spalte verfügen
		if ((cells.size() > 0) && (cells.get(0).size() > 0)) {
			diagrams.add(DiagramType.BUBBLE_CHART);
			diagrams.add(DiagramType.COLLAPSIBLE_INTENDED_TREEVIEW);
			diagrams.add(DiagramType.COLLAPSIBLE_TREEVIEW);
			diagrams.add(DiagramType.HIERARCHY_BAR);
			diagrams.add(DiagramType.WORD_CLOUD);
			diagrams.add(DiagramType.ZOOMABLE_TREEMAP);

			// Für alle weiteren Diagramme müssen mindestens zwei Spalten vorhanden sein
			if (cells.get(0).size() > 1) {
				for (int i = 0; i < cells.size(); i++) {
					for (int j = 0; j < cells.get(i).size(); j++) {
						
						// Beinhaltet eine Spalte ein Datumswert?
						if (DateValidator.isDate(cells.get(i).get(j)) != null) {

							// Existiert desweiteren ein nummerischer Wert?
							for (int k = 0; k < cells.get(i).size(); k++) {
								if (NumberValidator.isDouble(cells.get(i)
										.get(k))
										|| NumberValidator.isInteger(cells.get(
												i).get(k))) {
									
									// ... dann ließe sich aus den Daten ein CALENDAR_CHART und
									// ein LINE_CHART entwerfen
									if (!diagrams
											.contains(DiagramType.CALENDAR_CHART)) {
										diagrams.add(DiagramType.CALENDAR_CHART);
									}
									if (!diagrams
											.contains(DiagramType.LINE_CHART)) {
										diagrams.add(DiagramType.LINE_CHART);
									}
								}
							}
						}
						if (GeoValidator.isRegion(cells.get(i).get(j)) != null) {
							// Es muss noch einen numerischen Wert geben
							for (int k = 0; k < cells.get(i).size(); k++) {
								if (NumberValidator.isDouble(cells.get(i)
										.get(k))
										|| NumberValidator.isInteger(cells.get(
												i).get(k))) {
									if (!diagrams
											.contains(DiagramType.CHOROPLETH)) {
										diagrams.add(DiagramType.CHOROPLETH);
									}
								}
							}

						}

						// Für Längen- und Breitengrad müssen zwei Spalten mit
						// validem Datenformat gefunden werden
						if (GeoValidator.isLongLatFormat(cells.get(i).get(j))) {
							for (int k = j + 1; k < cells.get(i).size(); k++) {
								if (GeoValidator.isLongLatFormat(cells.get(i)
										.get(k))) {
									// ... plus ein numerischer Wert
									for (int l = 0; l < cells.get(i).size(); l++) {
										if (NumberValidator.isDouble(cells.get(
												i).get(l))
												|| NumberValidator
														.isInteger(cells.get(i)
																.get(l))) {

											for (int m = 0; m < cells.get(i)
													.size(); m++) {

												if (DateValidator.isYear(cells
														.get(i).get(m))) {

													if (!diagrams
															.contains(DiagramType.GLOBE_CHART)) {
														diagrams.add(DiagramType.GLOBE_CHART);
													}
												}
											}
										}
									}
								}
							}
						}
					}
				}

				// Flare-Chart benötigt lediglich 2 Datenreihen
				if (!diagrams.contains(DiagramType.FLARE_CHART)) {
					diagrams.add(DiagramType.FLARE_CHART);
				}
			}
		}

		return diagrams;
	}

	public boolean filterByColumns(int startX, int startY, int endX, int endY) {

		// Daten vorhanden?
		if (cells.size() > 0) {
			if (cells.get(0).size() == 0) {
				log.log(Level.WARNING,
						"Keine Daten in Tabelle zum Filtern vorhanden.");
				return false;
			}
		} else {
			log.log(Level.WARNING,
					"Keine Daten in Tabelle zum Filtern vorhanden.");
			return false;
		}

		// Ungültiger Index?
		if ((endX < startX) || (endY < startY) || (startX < 0) || (startY < 0)
				|| (endY > cells.size()) || (endX > cells.get(0).size())) {
			log.log(Level.WARNING, "Ungültige Spalten-/Zeilenfilter.");
			return false;
		}

		log.log(Level.INFO, "Beginne mit Filterung der Daten.");

		// Lösche Zeilen nach endY
		for (int i = cells.size() - 1; i > endY; i--) {
			cells.remove(i);
		}

		// Lösche Zeilen vor startY
		for (int i = startY - 1; i >= 0; i--) {
			cells.remove(i);
		}

		// Lösche Spalten nach endX
		for (int i = 0; i < cells.size(); i++) {
			for (int j = cells.get(i).size() - 1; j > endX; j--) {
				cells.get(i).remove(j);
			}
		}

		// Lösche Spalten vor startX
		for (int i = 0; i < cells.size(); i++) {
			for (int j = startX; j > 0; j--) {
				cells.get(i).remove(j);
			}
		}

		// Lösche Spaltennamen
		for (int i = columnTitles.size() - 1; i > endX; i--) {
			columnTitles.remove(i);
		}

		for (int i = startX; i > 0; i--) {
			columnTitles.remove(i);
		}

		return true;
	}
}
