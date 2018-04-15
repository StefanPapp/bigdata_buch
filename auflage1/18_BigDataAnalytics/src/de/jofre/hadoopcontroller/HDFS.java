package de.jofre.hadoopcontroller;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

/**
 * Klasse für den Zugriff auf das HDFS.
 * 
 * @author J. Freiknecht
 * 
 */
public class HDFS {

	private final static Logger log = Logger.getLogger(HDFS.class.getName());

	private FileSystem fs = null;
	private File dlFolder = null; // Temporärer Ordner für Downloads
	private File ulFolder = null; // Temporärer Ordner für Uploads

	/**
	 * Initialisieren des Zugriffs auf's HDFS
	 * 
	 * @param url
	 *            : z.B. hdfs://master:9000
	 * @param username
	 *            : Benutzername der für die Eigenschaft HADOOP_USER_NAME
	 *            gesetzt wird
	 */
	public void init(String url, String username) {

		log.log(Level.INFO, "Verbinde zu HDFS...");

		// Setze den Hadoop-User
		System.setProperty("HADOOP_USER_NAME", username);

		// Erstelle Verweis auf die Win32-Libs von Hadoop
		WinUtilsSolver.solveWinUtilError();

		// Erstelle temporären Ordner zum Herunterladen
		// von Dateien aus dem HDFS.
		dlFolder = new File("E:\\dlTemp");
		if (!dlFolder.exists()) {
			log.log(Level.INFO,
					"Lege temporären Downloadordner '"
							+ dlFolder.getAbsolutePath() + "' an...");
			dlFolder.mkdirs();
		}

		// Erstelle temporären Ordner zum Hochladen
		// von Dateien in das HDFS.
		ulFolder = new File("E:\\ulTemp");
		if (!ulFolder.exists()) {
			log.log(Level.INFO,
					"Lege temporären Uploadordner '"
							+ ulFolder.getAbsolutePath() + "' an...");
			ulFolder.mkdirs();
		}

		// Erstelle Konfiguration
		Configuration c = new Configuration();
		c.set("fs.defaultFS", url);

		// Initialisiere FileSystem
		try {
			fs = FileSystem.get(c);
		} catch (IOException e) {
			log.log(Level.SEVERE,
					"Fehler beim Initialisieren des Zugriffs auf das FileSystem.");
			e.printStackTrace();
		}
	}

	/**
	 * Initialisieren des FileSystems ohne Benutzernamen
	 * 
	 * @param url
	 *            : z.B. hdfs://master:9000
	 */
	public void init(String url) {
		init(url, null);
	}

	/**
	 * Erstellen eines neuen Ordners im HDFS
	 * 
	 * @param fullQualifiedName
	 *            : Absoluter Pfad des neuen Ordners
	 * @return Konnte der Ordner erfolgreich erstellt werden?
	 */
	public boolean createNewFolder(String fullQualifiedName) {

		log.log(Level.INFO, "Lege neuen Ordner '" + fullQualifiedName
				+ "' an...");
		boolean result = false;

		if (fs != null) {
			try {
				Path newFolderPath = new Path(fullQualifiedName);
				// Wenn der Ordner noch nicht existiert...
				if (!fs.exists(newFolderPath)) {

					// Dann erstelle ihn
					result = fs.mkdirs(newFolderPath);

					// Und setzen den Besitzer der Datei auf den hduser und
					// dessen
					// Gruppe auf supergroup. Achtung, ist der Benutzername beim
					// Initialisieren
					// des HDFS-Objekts nicht richtig gesetzt, dann schlägt
					// diese Aktion fehl.
					fs.setOwner(newFolderPath, "hduser", "supergroup");
				}
			} catch (Exception e) {
				log.log(Level.SEVERE,
						"Fehler beim Anlegen des Verzeichnisses '"
								+ fullQualifiedName + "'.");
				e.printStackTrace();
				return result;
			}
		}

		return result;
	}

	/**
	 * Liste alle Dateien und Ordner aus dem angegebenen Verzeichnis auf
	 * 
	 * @param dir
	 *            : Auszulesendes Verzeichnis
	 * @return Liste von FileStatus-Objekten
	 */
	public List<FileStatus> getEntriesFromDir(String dir) {

		log.log(Level.INFO, "Liste Dateien und Ordner in '" + dir + "' auf...");

		// Liste zum Speichern der FileStatus-Objekte. Diese beinhalten neben
		// Dateinamen noch die Anzahl der Repliken, den Besitzer etc.
		List<FileStatus> results = new ArrayList<FileStatus>();
		FileStatus[] status = null;

		try {

			// Abrufen der Dateien
			status = fs.listStatus(new Path(dir));
		} catch (Exception e) {
			log.log(Level.SEVERE,
					"Fehler beim Auflisten der Dateien und Ordner.");
			e.printStackTrace();
			return null;
		}

		// Kopieren der Dateiinformationen in die Liste
		if (status != null) {
			for (int i = 0; i < status.length; i++) {
				results.add(status[i]);
			}
		}

		return results;
	}

	/**
	 * Herunterladen einer Datei aus dem HDFS
	 * 
	 * @param file
	 *            : Absoluter Pfad der herunterzuladenden Datei
	 * @return Lokaler Pfad der heruntergeladenen Datei auf dem Server
	 */
	public String downloadFile(String file) {
		log.log(Level.INFO, "Lade Datei '" + file + "' herunter...");

		if (fs != null) {

			// Dekodieren des Dateipfades
			String filePath = file;
			try {
				filePath = URLDecoder.decode(file, "UTF-8");
			} catch (UnsupportedEncodingException e1) {
				log.log(Level.WARNING,
						"Fehler beim dekodieren des Dateipfades.");
				e1.printStackTrace();
			}

			try {

				// Erstelle Pfad-Objekte für Ziel- und Quellverzeichnis
				File fileToBeDownloaded = new File(filePath);
				Path source = new Path(filePath);
				Path target = new Path(dlFolder.getAbsolutePath()
						+ System.getProperty("file.separator")
						+ fileToBeDownloaded.getName());
				log.log(Level.INFO, "Quelle: '" + source.toString()
						+ "' Target: '" + target.toString() + "'.");

				// Lade die Datei herunter
				fs.copyToLocalFile(source, target);
				return target.toString();
			} catch (Exception e) {
				log.log(Level.SEVERE, "Fehler beim Herunderladen der Datei '"
						+ filePath + "'.");
				e.printStackTrace();
			}
		}
		return null;
	}

	/**
	 * Löschen einer Datei oder eines Ordners.
	 * 
	 * @param entity
	 *            : Absoluter Pfad zum zu löschenden Objekt.
	 * @return War der Löschvorgang erfolgreich?
	 */
	public boolean deleteFileOrFolder(String entity) {
		log.log(Level.INFO, "Lösche Datei/Ordner '" + entity + "'...");
		boolean result = false;

		// Dekodieren des Pfades
		String filePath = entity;
		try {
			filePath = URLDecoder.decode(entity, "UTF-8");
		} catch (UnsupportedEncodingException e1) {
			log.log(Level.WARNING, "Fehler beim dekodieren des Dateipfades.");
			e1.printStackTrace();
		}

		try {

			// Lösche den Ordner/ die Datei. Ordner werden dank des "true"
			// rekursiv gelöscht.
			result = fs.delete(new Path(filePath), true);
		} catch (Exception e) {
			log.log(Level.SEVERE, "Fehler beim Löschen von '" + filePath + "'.");
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * Hochladen einer Datei vom Server zum HDFS
	 * 
	 * @param source
	 *            : Hochzuladene Datei
	 * @param target
	 *            : Zielpfad auf dem Server
	 * @return War das Hochladen erfolgreich?
	 */
	public boolean uploadFile(String source, String target) {

		log.log(Level.INFO, "Lade Datei hoch von '" + source + "' nach '"
				+ target + "'...");

		boolean result = false;
		try {
			Path targetPath = new Path(target);

			// Lade die Datei hoch...
			fs.copyFromLocalFile(new Path(source), targetPath);

			// ... und ändere den Besitzer
			fs.setOwner(targetPath, "hduser", "supergroup");
			result = true;
		} catch (Exception e) {
			log.log(Level.SEVERE, "Fehler beim Hochladen von '" + source + "'.");
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * Abrufen der ersten n Zeilen einer Datei im HDFS
	 * 
	 * @param file: Datei, die zu lesen ist
	 * @param lineCount: Maximale Anzahl der zu lesenden Zeilen
	 * @return Inhalt der ersten n Zeilen
	 */
	public String getFileContet(String file, int lineCount) {
		StringBuilder sb = new StringBuilder();
		int count = 0;
		try {
			Path pt = new Path(file);
			BufferedReader br = new BufferedReader(new InputStreamReader(
					fs.open(pt)));
			String line;
			line = br.readLine();
			while ((line != null) && (lineCount > count)) {
				sb.append(line + System.lineSeparator());
				line = br.readLine();
				count++;
			}
			br.close();
		} catch (Exception e) {
			log.log(Level.SEVERE, "Fehler beim Laden des Inhalts der Datei '" + file+"'.");
			e.printStackTrace();
		}
		return sb.toString();
	}
}
