package de.jofre.hbasemanager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Delete;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.filter.CompareFilter.CompareOp;
import org.apache.hadoop.hbase.filter.PageFilter;
import org.apache.hadoop.hbase.filter.SingleColumnValueFilter;
import org.apache.hadoop.hbase.util.Bytes;

public class HBaseManager {

	private Configuration conf;
	private HBaseAdmin admin;
	
	private static final int FETCH_SIZE = 100;
	
	private final static Logger log = Logger.getLogger(HBaseManager.class
			.getName());
	

	/**
	 * Constructor des HBaseManagers, erstellt die Configuration.
	 * 
	 * @param _zooKeeperQuorum
	 */
	public HBaseManager(String _zooKeeperQuorum) {
		conf = HBaseConfiguration.create();
		conf.set("hbase.zookeeper.quorum", _zooKeeperQuorum);
		try {
			admin = new HBaseAdmin(conf);
		} catch (Exception e) {
			log.log(Level.SEVERE, "Fehler beim Erstellen des HBase-Admins.");
			e.printStackTrace();
		}
	}

	/**
	 * List alle Tabellen aus HBase aus
	 * 
	 * @return Liste der Tabellen als String
	 */
	public List<String> getTables() {
		log.log(Level.INFO, "Lese Tabellen aus HBase aus.");
		TableName[] names = new TableName[0];
		try {
			names = admin.listTableNames();
		} catch (IOException e) {
			log.log(Level.SEVERE, "Fehler beim Abrufen aller Tabellennamen.");
			e.printStackTrace();
		}
		List<String> tables = new ArrayList<String>();
		for (int i = 0; i < names.length; i++) {
			tables.add(names[i].getNameAsString());
		}
		return tables;
	}
	
	
	/**
	 * Auslesen einer Zeile zu einem Schlüssel
	 * 
	 * @param _table Quelltabelle
	 * @param _rowKey Schlüssel
	 * @return Liste aller Zellen in der Zeile
	 */
	public List<Cell> getRow(String _table, String _rowKey) {
		log.log(Level.INFO, "Lese Zeile mit Schlüssel "+_rowKey+" aus Tabelle " + _table + ".");
		List<Cell> result = null;
		Result r = null;
		try {
			HTable table = new HTable(conf, _table);
			Get get = new Get(Bytes.toBytes(_rowKey));
			r = table.get(get);
			result = r.listCells();
			table.close();
		} catch (IOException e) {
			log.log(Level.SEVERE, "Fehler beim Auslesen einer Zeile.");
			e.printStackTrace();
		}
		return result;
	}
	
	/**
	 * Liest mehrere Zeilen ab einem bestimmten Zeilenschlüssel aus
	 * 
	 * @param _table Quelltabelle
	 * @param _startKey Ab welchem Schlüssel soll gelesen werden?
	 * @param _lines Wieviele Zeilen sollen maximal gelesen werden?
	 * @return Liste von Zeilen in Form einer Liste von Zellen
	 */
	public List<List<Cell>> getRows(String _table, String _startKey, int _lines) {
		log.log(Level.INFO, "Lese "+_lines+" Zeilen aus Tabelle " + _table + " ab Schlüssel "+_startKey+" aus.");
		List<List<Cell>> rows = new ArrayList<List<Cell>>();
		HTable table = null;

		// Zugriff auf Tabelle
		try {
			table = new HTable(conf, _table);
			Scan s = new Scan();
			s.setFilter(new PageFilter(_lines));
			if (_startKey != null) {
				s.setStartRow(Bytes.toBytes(_startKey));
			}
			ResultScanner rs = table.getScanner(s);
			for (Result r2 = rs.next(); r2 != null; r2 = rs.next()) {
				rows.add(r2.listCells());
			}
			rs.close();
			table.close();
		} catch (IOException e) {
			log.log(Level.SEVERE, "Fehler beim Auslesen mehrerer Zeilen.");
			e.printStackTrace();
		}

		return rows;
	}
	
	/**
	 * Deaktivieren einer Tabelle
	 * 
	 * @param _table Tabelle, die deaktiviert werden soll.
	 */
	public void disableTable(String _table) {
		log.log(Level.INFO, "Deaktiviere " + _table + ".");
		try {
			admin.disableTable(_table);
		} catch (IOException e) {
			log.log(Level.SEVERE, "Fehler beim Deaktivieren der Tabelle.");
			e.printStackTrace();
		}
	}
	
	/**
	 * Aktivieren einer Tabelle
	 * 
	 * @param _table Tabelle, die aktiviert werden soll
	 */
	public void enableTable(String _table) {
		log.log(Level.INFO, "Aktiviere Tabelle " + _table + ".");
		try {
			admin.enableTable(_table);
		} catch (IOException e) {
			log.log(Level.SEVERE, "Fehler beim Aktivieren der Tabelle.");
			e.printStackTrace();
		}
	}	
	
	/**
	 * Löschen einer Tabelle (Muss zuvor deaktiviert worden sein)
	 * 
	 * @param _table Tabelle, die gelöscht werden soll
	 */
	public void deleteTable(String _table) {
		log.log(Level.INFO, "Lösche Tabelle " + _table + ".");
		try {
			admin.deleteTable(_table);
		} catch (IOException e) {
			log.log(Level.SEVERE, "Fehler beim Löschen der Tabelle.");
			e.printStackTrace();
		}
	}
	
	/**
	 * Ist eine Tabelle aktiviert?
	 * 
	 * @param _table Tabelle, die überprüft werden soll
	 * @return true falls aktiv, false falls inaktiv
	 */
	public boolean isTableEnabled(String _table) {
		log.log(Level.INFO, "Überprüfe, ob Tabelle " + _table + " aktiviert ist.");
		try {
			return !admin.isTableDisabled(_table);
		} catch (IOException e) {
			log.log(Level.SEVERE, "Fehler beim Abfragen des Tabellenstatus.");
			e.printStackTrace();
		}
		return false;
	}
	
	/**
	 * Löschen aller Zeilen in einer Tabelle. Tabelle wird dazu gelöscht und neuerstellt.
	 * 
	 * @param _table Tabelle, die geleert werden soll.
	 */
	public void emptyTable(String _table) {
		log.log(Level.INFO, "Leere Tabelle " + _table + ".");
		HTableDescriptor td;
		try {
			td = admin.getTableDescriptor(Bytes.toBytes(_table));
			admin.disableTable(_table);
			admin.deleteTable(_table);
			admin.createTable(td);
		} catch (Exception e) {
			log.log(Level.SEVERE, "Fehler beim Leeren der Tabelle.");
			e.printStackTrace();
		}
	}
	
	/**
	 * Auslesen aller Column-Families in einer Tabelle
	 * 
	 * @param _table Tabelle, die betrachtet werden soll
	 * @return Liste der Namen aller Column-Families als String
	 */
	public List<String> getColumnFamilies(String _table) {
		log.log(Level.INFO, "Lese ColumnFamilies für Tabelle " + _table + " aus.");
		HTableDescriptor td;
		List<String> cfs = new ArrayList<String>();
		try {
			td = admin.getTableDescriptor(Bytes.toBytes(_table));
			HColumnDescriptor[] descr = td.getColumnFamilies();
			for(int i=0; i<descr.length; i++) {
				cfs.add(descr[i].getNameAsString());
			}
		} catch (Exception e) {
			log.log(Level.SEVERE, "Fehler beim Leeren der Tabelle.");
			e.printStackTrace();
		}
		return cfs;
	}
	
	/**
	 * Überprüfen, ob eine Tabelle existiert
	 * 
	 * @param _table Tabelle, deren Existenz überprüft werden soll.
	 * @return true, falls existent, false falls nicht
	 */
	public boolean tableExists(String _table) {
		log.log(Level.INFO, "Überprüfe, ob Tabelle " + _table + " existiert.");
		try {
			return admin.tableExists(_table);
		} catch (IOException e) {
			log.log(Level.SEVERE, "Fehler beim Abfragen der Existenz einer Tabelle.");
			e.printStackTrace();
		}
		return false;
	}
	
	/**
	 * Löschen einer Zeile mit einem bestimmten Zeilenschlüssel
	 * 
	 * @param _table Tabelle, aus der gelöscht werden soll
	 * @param _rowKey Schlüssel der zu löschenden Zeile
	 */
	public void deleteRow(String _table, String _rowKey) {
		log.log(Level.INFO, "Lösche Zeile mit Schlüssel " + _rowKey + " aus Tabelle " + _table + " aus.");
		try {
			HTable table = new HTable(conf, _table);
			Delete d = new Delete(Bytes.toBytes(_rowKey));
			table.delete(d);
			table.close();
		} catch (IOException e) {
			log.log(Level.SEVERE, "Fehler beim Löschen des Datensatzes mit Schlüssel "+_rowKey+" aus Tabelle "+_table+".");
			e.printStackTrace();
		}
	}
		
	/**
	 * Suchen nach Zeilen mit einer bestimmten Kombination aus Column-Family, Spalte und Wert
	 * 
	 * @param _table Tabelle, in der gesucht werden soll
	 * @param _cf Column-Family, in der gesucht werden soll
	 * @param _column Spalte, in der gesucht werden soll
	 * @param _value Wert, der in der Spalte vorhanden sein muss
	 * @return Liste aller Zeilen, die auf die Suchparameter zutreffen
	 */
	public List<List<Cell>> search(String _table, String _cf, String _column, String _value) {
		log.log(Level.INFO, "Führe komplexe Suche in " + _table + " nach ColumnFamily " + _cf + ", Spalte " + _column + " und Wert " + _value + " aus.");
		List<List<Cell>> rows = new ArrayList<List<Cell>>();

		try {
			HTable table = new HTable(conf, _table);
			Scan s = null;
			
			if ((_cf != null) && (_column != null) && (_value != null)) {
				s = new Scan();
				SingleColumnValueFilter f = new SingleColumnValueFilter(Bytes.toBytes(_cf), Bytes.toBytes(_column), CompareOp.EQUAL, Bytes.toBytes(_value));
				f.setFilterIfMissing(true);
				s.setFilter(f);
				ResultScanner rs = table.getScanner(s);
				for (Result r2 = rs.next(); r2 != null; r2 = rs.next()) {
					rows.add(r2.listCells());
				}
				rs.close();
				table.close();
			}
			
		} catch (IOException e) {
			log.log(Level.SEVERE, "Fehler beim Zugriff auf die Tabelle.");
			e.printStackTrace();
		}

		return rows;
	}
	
	/**
	 * Hinzufügen einer neuen Zeile oder einer neuen Spalte zu einer Tabelle
	 * 
	 * @param _table Zieltabelle
	 * @param _rowKey Neuer oder existierender Schlüssel
	 * @param _cf Existierende Column-Family
	 * @param _column Neue oder existierende Spalte
	 * @param _value Neuer Wert
	 */
	public void add(String _table, String _rowKey, String _cf, String _column, String _value) {
		log.log(Level.INFO, "Füge Spalte zu Tabelle " + _table + " an Key " + _rowKey + " hinzu (" + _cf + ":"+_column+"="+_value+")");
		try {
			HTable table = new HTable(conf, _table);
	    	Put put = new Put(Bytes.toBytes(_rowKey));
	    	put.add(Bytes.toBytes(_cf), Bytes.toBytes(_column), Bytes.toBytes(_value));
	    	table.put(put);
	    	table.close();
		} catch (IOException e) {
			log.log(Level.SEVERE, "Fehler beim Zugriff auf die Tabelle.");
			e.printStackTrace();
		}
	}
	
	/**
	 * Importieren mehrerer Werte in eine Tabelle
	 * 
	 * @param _table Zieltabelle
	 * @param _cf Existierende Column-Family
	 * @param _data Daten, die importiert werden sollen. Datensätze werden Zeilenweise gelesen. Zeile muss CSV sein. Erste Zeile wird für Spaltennamen verwendet.
	 */
	public void importData(String _table, String _cf, String _data) {
		log.log(Level.INFO, "Importiere Daten in Tabelle " + _table + " in Column-Family " + _cf + ".");
		String[] lines = _data.split(System.getProperty("line.separator"));
		
		if (lines.length == 0) return;
		
		try {
			HTable table = new HTable(conf, _table);
			
			String[] columnNames = lines[0].split(",");
			
			for(int i=1; i<lines.length; i++) {
				String[] values = lines[i].split(",");
				
				if (values.length > 0) {
					Put p = new Put(Bytes.toBytes(values[0]));
					
					for(int j=1; j<values.length; j++) {
						p.add(Bytes.toBytes(_cf), Bytes.toBytes(columnNames[j]), Bytes.toBytes(values[j]));
					}
					table.put(p);
				}
			}
			table.close();
		} catch (IOException e) {
			log.log(Level.SEVERE, "Fehler beim Importieren der Daten.");
			e.printStackTrace();
		}
	}
	
	/**
	 * Füge einer Tabelle eine Column-Family hinzu.
	 * 
	 * @param _table Zieltabelle
	 * @param _newCf Name, der neuen Column-Family
	 * @return true bei Erfolg, false bei Fehlschlagen
	 */
	public boolean addColumnFamily(String _table, String _newCf) {
		log.log(Level.INFO, "Füge Tabelle " + _table + " die Column-Family " + _newCf + " hinzu.");
		
		HTableDescriptor td = null;
		try {
			td = admin.getTableDescriptor(Bytes.toBytes(_table));
			HColumnDescriptor[] cfs = td.getColumnFamilies();
			for(int i=0; i<cfs.length; i++) {
				if (cfs[i].getNameAsString().equalsIgnoreCase(_newCf)) {
					log.log(Level.INFO, "Column-Family " + _newCf + " existiert bereits!");
					return false;
				}
			}
			
			admin.disableTable(_table);
			HColumnDescriptor hcd = new HColumnDescriptor(Bytes.toBytes(_newCf));
			admin.addColumn(Bytes.toBytes(_table), hcd);
			admin.enableTable(_table);
			return true;
		} catch (Exception e) {
			log.log(Level.SEVERE, "Fehler beim Hinzufügen der Column-Family " + _newCf + ".");
			e.printStackTrace();
		}
		return false;
	}
	
	/**
	 * Erzeuge eine neue Tabelle
	 * 
	 * @param _newTable Tabellenname
	 */
	public void addTable(String _newTable) {
		log.log(Level.INFO, "Erzeuge neue Tabelle " + _newTable + ".");
		
		if (tableExists(_newTable)) {
			log.log(Level.WARNING, "Tabelle " + _newTable + " existiert bereits - beende.");
			return;
		}
		
		TableName tn = TableName.valueOf(Bytes.toBytes(_newTable));
		HTableDescriptor td = new HTableDescriptor(tn);
		try {
			admin.createTable(td);
		} catch (IOException e) {
			log.log(Level.SEVERE, "Fehler beim Hinzufügen der Tabelle " + _newTable + ".");
			e.printStackTrace();
		}
	}
	
	/**
	 * Hole die Konstante, die besagt, wieviele Elemente beim Anzeigen der Tabelle geladen werden sollen
	 * 
	 * @return Anzahl der zu ladenden Elemente
	 */
	public int getFetchSize() {
		return FETCH_SIZE;
	}
}
