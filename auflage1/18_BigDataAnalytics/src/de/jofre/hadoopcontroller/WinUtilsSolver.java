package de.jofre.hadoopcontroller;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class WinUtilsSolver {
	private final static Logger log = Logger.getLogger(WinUtilsSolver.class.getName());


	/**
	 * Erstellen einer Phantomdatei im Root-Verzeichnis von [Eclipse]/bin.
	 */
	public static void solveWinUtilError() {
		
		// Erfrage, ob die Systemeigenschaft hadoop.home.dir gesetzt ist
		if (System.getProperty("hadoop.home.dir") != null) {

			String hDir = System.getProperty("hadoop.home.dir");
			if (!hDir.endsWith("\\")) {
				hDir = hDir += "\\";
			}

			// Wenn ja, überprüfe ob darin ein Ordner "bin" existiert und
			// darin eine Datei "winutils.exe" liegt.
			File winUtilsPath = new File(hDir + "bin\\winutils.exe");
			if (winUtilsPath.exists()) {

				log.log(Level.INFO,
						"winutils.exe in "+winUtilsPath.getAbsolutePath()+" gefunden, Workaround nicht nötig.");
				return;
			} else {
				log.log(Level.WARNING, "hadoop.home.dir ist zwar gesetzt, jedoch wurden keine Binaries gefunden.");
			}
		}

		// Existieren die Binaries denn?
		File binaries = new File("E:\\hadoop-2.2.0\\bin\\winutils.exe");
		if (binaries.exists()) {
			
			// ... dann verlinke sie
			System.getProperties().put("hadoop.home.dir", "E:\\hadoop-2.2.0\\");
		} else {
			
			// Existieren sie nicht, simuliere sie
			log.log(Level.INFO, "Wende WinUtils-Workaround an...");

			// Erstelle eine Datei im aktuellen Ordner (in unserem Fall dem
			// Root-Ordner
			// von Eclipse)
			File workaround = new File("E:\\hadoop-2.2.0\\");

			// Erstelle die Systemeigenschaft hadoop.home.dir und setze deren
			// Wert
			// auf den eben erstellten Ordner.
			System.getProperties().put("hadoop.home.dir",
					workaround.getAbsolutePath());

			// Erstelle in diesem Ordner den Ordner "bin" ...
			new File("./bin").mkdirs();
			try {

				// ... und darin eine leere Datei "winutils.exe"
				new File("E:\\hadoop-2.2.0\\bin\\winutils.exe").createNewFile();
			} catch (IOException e) {
				log.log(Level.SEVERE,
						"Fehler beim Erstellen der Datei './bin/winutils.exe'.");
				e.printStackTrace();
			}
		}
	}
}
