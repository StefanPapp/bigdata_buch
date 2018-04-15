package de.jofre.hadoopcontroller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.yarn.api.records.ApplicationReport;
import org.apache.hadoop.yarn.api.records.NodeReport;
import org.apache.hadoop.yarn.client.api.YarnClient;
import org.apache.hadoop.yarn.exceptions.YarnException;

public class YarnCluster {

	private final static Logger log = Logger.getLogger(YarnCluster.class
			.getName());
	private YarnClient yc = null;

	/**
	 * 
	 * Initialisiere die Verbindung zum Cluster. Die Eigenschaften können in der
	 * yarn-site.xml gefunden werden.
	 * 
	 * @param schedulerAddress
	 *            : URL des Schedulers
	 * @param resourceManagerAddress
	 *            : URL des ResourceManagers
	 * @param resourceTrackerAddress
	 *            : URL des Tasktrackers
	 */
	public void init(String schedulerAddress, String resourceManagerAddress,
			String resourceTrackerAddress, String username) {
		log.log(Level.INFO, "Konfiguriere Verbindung zum YARN-Cluster...");
		
		// Setze den Hadoop-User
		System.setProperty("HADOOP_USER_NAME", username);
		
		// Erstelle Verweis auf die Win32-Libs von Hadoop
		WinUtilsSolver.solveWinUtilError();
		
		// Erstelle eine Konfiguration mit den nötigen Adressen.
		Configuration conf = new Configuration();
		conf.set("yarn.resourcemanager.scheduler.address", schedulerAddress);
		conf.set("yarn.resourcemanager.address", resourceManagerAddress);
		conf.set("yarn.resourcemanager.resource-tracker.address",
				resourceTrackerAddress);

		// Erstelle den YarnClient aus der Konfiguration
		yc = YarnClient.createYarnClient();
		yc.init(conf);
		yc.start();
	}

	/**
	 * Beenden der Verbindung zu YARN
	 */
	public void uninit() {
		if (yc != null) {
			yc.stop();
		}
	}

	/**
	 * Abfragen aller Knoten im Cluster.
	 * 
	 * @return: Liste mit NodeReport-Objekten
	 */
	public List<NodeReport> getNodes() {
		if (yc == null) {
			log.log(Level.SEVERE, "YARN-client ist nicht verbunden!");
			return null;
		}
		List<NodeReport> nodes = null;
		try {

			// Abfragen aller Knoten
			nodes = yc.getNodeReports();
		} catch (Exception e) {
			log.log(Level.SEVERE, "Fehler beim Abfragen der Nodes.");
			e.printStackTrace();
		}

		return nodes;
	}

	/**
	 * Auflisten aller aktiven Anwendungen auf dem Cluster.
	 * 
	 * @return: Liste mit NodeReport-Objekten
	 */
	public List<ApplicationReport> getApplications() {
		if (yc == null) {
			log.log(Level.SEVERE, "YARN-client ist nicht verbunden!");
			return null;
		}
		List<ApplicationReport> results = new ArrayList<ApplicationReport>();
		try {

			// Abfragen aller Anwendungen
			results = yc.getApplications();
		} catch (YarnException e) {
			log.log(Level.SEVERE, "Fehler beim Abfragen der Anwendungen.");
			e.printStackTrace();
		} catch (IOException e) {
			log.log(Level.SEVERE, "Fehler beim Abfragen der Anwendungen.");
			e.printStackTrace();
		}

		return results;
	}
}
