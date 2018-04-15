package de.jofre.hivemanager;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.vfs2.FileContent;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemManager;
import org.apache.commons.vfs2.FileSystemOptions;
import org.apache.commons.vfs2.VFS;
import org.apache.commons.vfs2.provider.sftp.SftpFileSystemConfigBuilder;

public class HiveUserManager {

	// Pfad zur Datei, die die Benutzerdaten beinhaltet
	private static final String AUTH_FILE = "/usr/local/hive/users.auth";

	private final static Logger log = Logger.getLogger(HiveUserManager.class
			.getName());

	private HashMap<String, String> users = null;

	public HiveUserManager() {
		users = readUsers();
	}
	
	private HashMap<String, String> readUsers() {
		HashMap<String, String> users = new HashMap<String, String>();
		BufferedReader br = null;

		try {
			// SFTP-Verbindung herstellen
			FileSystemOptions fsOptions = new FileSystemOptions();
			SftpFileSystemConfigBuilder.getInstance().setStrictHostKeyChecking(
					fsOptions, "no");
			SftpFileSystemConfigBuilder.getInstance().setUserDirIsRoot(
					fsOptions, false);

			FileSystemManager fsManager = VFS.getManager();

			String uri = "sftp://hduser:hduser@single" + AUTH_FILE;
			FileObject fo = fsManager.resolveFile(uri, fsOptions);
			String line = "";
			// Benutzername und Passwort müssen durch Tab getrennt werden
			String cvsSplitBy = "\t";

			if (fo.exists()) {
				FileContent fc = fo.getContent();
				InputStream is = fc.getInputStream();

				if (is != null) {
					// Lese jede Zeile der Benutzerdatendatei
					br = new BufferedReader(new InputStreamReader(is));
					while ((line = br.readLine()) != null) {
						String[] entry = line.split(cvsSplitBy);

						// ... und füge diese der Hash-Table hinzu
						// (0=Benutzername,
						// 1=Passwort)
						users.put(entry[0], entry[1]);
					}
				} else {
					return null;
				}
			} else {
				log.log(Level.WARNING, "Benutzerdatei existiert nicht.");
				return null;
			}

		} catch (Exception e) {
			log.log(Level.SEVERE, "Fehler beim Auslesen der Benutzerdatei.");
			e.printStackTrace();
			return null;
		}

		return users;
	}

	public HashMap<String, String> getUsers() {
		return users;
	}

	public boolean writeUsers() {

		FileObject fo = null;
		BufferedWriter bw = null;

		if (users == null) {
			log.log(Level.WARNING, "Keine Benutzer zum Schreiben vorhanden.");
			return false;
		}

		try {
			FileSystemOptions fsOptions = new FileSystemOptions();
			SftpFileSystemConfigBuilder.getInstance().setStrictHostKeyChecking(
					fsOptions, "no");
			SftpFileSystemConfigBuilder.getInstance().setUserDirIsRoot(
					fsOptions, false);
			FileSystemManager fsManager = VFS.getManager();
			String uri = "sftp://hduser:hduser@single" + AUTH_FILE;
			fo = fsManager.resolveFile(uri, fsOptions);
			if (fo != null) {
				OutputStream os = fo.getContent().getOutputStream();

				log.log(Level.INFO, "Persistiere Benutzer...");

				bw = new BufferedWriter(new OutputStreamWriter(os));

				Iterator<Entry<String,String>> it = users.entrySet().iterator();
				while (it.hasNext()) {
					Entry<String,String> entry = (Entry<String,String>) it.next();
					bw.write(entry.getKey() + "\t" + entry.getValue());
					bw.newLine();
					it.remove();
				}

				bw.flush();

			}
		} catch (Exception e1) {
			log.log(Level.SEVERE,
					"Fehler beim Schreiben der Benutzerdatei.");
			e1.printStackTrace();
		} finally {
			if (bw != null) {
				try {
					bw.close();
				} catch (IOException e) {
					log.log(Level.SEVERE,
							"Fehler beim Schließen der Benutzerdatei.");
					e.printStackTrace();
					return false;
				}
			}
		}

		return true;
	}
	
	public boolean removeUser(String user) {
		if (users != null) {
			if (users.get(user) != null) {
				log.log(Level.INFO, "Lösche Benutzer " + user+ "...");
				users.remove(user);
				return writeUsers();
			}
		}
		return false;
	}

	public boolean addUser(String user, String password) {

		// Überprüfe, ob der Benutzer bereits existiert
		HashMap<String, String> users = getUsers();
		String pwAsHex = "";

		if (users.get(user) != null) {
			log.log(Level.WARNING, "Benutzer " + user + " existiert bereits.");
			return false;
		}

		try {
			pwAsHex = convertToMd5(password);
		} catch (UnsupportedEncodingException e) {
			log.log(Level.WARNING, "Konnte keine MD5-Summe berechnen.");
			e.printStackTrace();
			return false;
		}

		users.put(user, pwAsHex);

		return writeUsers();
	}

	private static String convertToMd5(final String md5)
			throws UnsupportedEncodingException {
		StringBuffer sb = null;
		try {
			final MessageDigest md = MessageDigest.getInstance("MD5");
			final byte[] array = md.digest(md5.getBytes("UTF-8"));
			sb = new StringBuffer();
			for (int i = 0; i < array.length; ++i) {
				sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100)
						.substring(1, 3));
			}
		} catch (NoSuchAlgorithmException e) {
			log.log(Level.SEVERE, "Fehler beim Generieren des MD5-Strings.");
			e.printStackTrace();
		}
		return sb.toString();
	}
}
