package de.jofre.pdfinput;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.JobContext;
import org.apache.hadoop.mapreduce.RecordReader;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;

public class PDFInputFormat extends FileInputFormat<Text, Text> {

	private final static Logger log = Logger.getLogger(PDFInputFormat.class
			.getName());
	
	// Eingabe Key und Value müssen den Datentypen des Mappers entsprechen (Text, Text).
	@Override
	public RecordReader<Text, Text> createRecordReader(InputSplit split,
			TaskAttemptContext context) throws IOException,
			InterruptedException {
		
		log.log(Level.INFO, "Creating new RecordReader...");
		
		// Der Record-Reader muss ebenfalls den Datentypen des Mappers entsprechen,
		// andernfalls wird direkt von Eclipse auf einen Typ-Missmatch hingewiesen.
		return new PDFLineRecordReader();
	}

	// Verbiete es, große Dateien zu splitten
	@Override
	protected boolean isSplitable(JobContext context, Path filename) {
		return false;
	}
		
}
