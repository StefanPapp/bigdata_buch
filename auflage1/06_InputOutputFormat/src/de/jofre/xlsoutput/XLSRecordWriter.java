package de.jofre.xlsoutput;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.RecordWriter;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

public class XLSRecordWriter extends RecordWriter<Text, IntWritable> {

	private final static Logger log = Logger.getLogger(XLSRecordWriter.class.getName());
	
	FSDataOutputStream out = null;
	HSSFWorkbook workbook = null;
	int rowCount = 0;
	
	// Constructor für den Writer, der ein Excel-Workbook und einen OutputStream
	// entgegen nimmt.
	public XLSRecordWriter(HSSFWorkbook workbook, FSDataOutputStream out) {
		this.workbook = workbook;
		this.out = out;
		rowCount = 0;
	}
	
	// Schreiben der Schlüsselwertpaare
	@Override
	public void write(Text key, IntWritable value) throws IOException,
			InterruptedException {
		
		log.log(Level.INFO, "Schreibe ["+key.toString()+", "+value.toString()+"].");
		
		// Wenn das workbook erstellt werden konnte...
		if (workbook != null) {
			
			// Dann lege im ersten Sheet für jedes Schlüsselwertpaar eine
			// neue Zeile an.
			Row row = workbook.getSheetAt(0).createRow(rowCount);
			Cell loglevel = row.createCell(0);
			Cell counter = row.createCell(1);
			loglevel.setCellValue(key.toString());
			counter.setCellValue(value.toString());
			rowCount++;
		}
		
	}

	// Sind alle Werte geschrieben, muss die Excel-Datei geschrieben werden.
	@Override
	public void close(TaskAttemptContext context) throws IOException,
			InterruptedException {
		
		log.log(Level.INFO, "Schreibevorgang beendet, speichere Excel-Datei...");
		
		if ((out != null) && (workbook != null)) {
			workbook.write(this.out);
			out.close();
		}
	}

}
