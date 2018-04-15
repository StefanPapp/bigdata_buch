package de.jofre.pdfinput;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.RecordReader;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.util.PDFTextStripper;

public class PDFLineRecordReader extends RecordReader<Text, Text> {

	private final static Logger log = Logger
			.getLogger(PDFLineRecordReader.class.getName());

	// Map-Reduce-Prozess
	private Text key = new Text();
	private Text value = new Text();
	private int currentLine = 0; // Zeiger auf aktuelle Zeile
	private List<String> lines = null; // Inhalt der PDFs

	// PDF-Verarbeitung
	private PDDocument doc = null;
	private PDFTextStripper textStripper = null;

	@Override
	public void initialize(InputSplit split, TaskAttemptContext context)
			throws IOException, InterruptedException {

		log.log(Level.INFO, "Initialisiere PDFLineRecordReader...");
		
		// Lese Split
		FileSplit fileSplit = (FileSplit) split;
		final Path file = fileSplit.getPath();
		log.log(Level.INFO, "Pfad des Splits ist '" + file.toString() + "'.");

		Configuration conf = context.getConfiguration();
		FileSystem fs = file.getFileSystem(conf);
		FSDataInputStream filein = fs.open(fileSplit.getPath());

		// Ist ein InputStream auf das PDF vorhanden?
		if (filein != null) {

			// Dann lese das PDF ein
			doc = PDDocument.load(filein);

			// Konnte das PDF gelesen werden?
			if (doc != null) {
				textStripper = new PDFTextStripper();
				String text = textStripper.getText(doc);
				
				// Lese jede Zeile des PDFs in eine Liste
				lines = Arrays.asList(text.split(System.lineSeparator()));
				currentLine = 0;
				
				log.log(Level.INFO, "PDF wurde gelesen, "+lines.size()+" Zeilen gefunden.");
			} else {
				log.log(Level.SEVERE, "PDF konnte nicht gelesen werden!");
			}

		} else {
			log.log(Level.SEVERE, "Split konnte nicht gelesen werden!");
		}
	}


	/* 
	 * 	Geben wir hier false zurück, teilen wir dem Prozess mit, dass
	 *  der Split fertig gelesen wurde.
	 * 
	 * @see org.apache.hadoop.mapreduce.RecordReader#nextKeyValue()
	 */
	@Override
	public boolean nextKeyValue() throws IOException, InterruptedException {

		// Initialisiere Variablen
		if (key == null) {
			key = new Text();
		}

		if (value == null) {
			value = new Text();
		}

		// Setze key und value
		if (currentLine < lines.size()) {
			String line = lines.get(currentLine);
			
			// Der Schlüssel beinhaltet die aktuelle Zeile des PDFs
			key.set(line);
			
			// Der Wert bleibt leer
			value.set("");
			currentLine++;
			
			// Gebe true zurück, falls noch mehr Zeilen im PDF sind
			return true;
		} else {
			
			// Sind alle Zeilen bearbeitet, beende den Lesevorgang
			key = null;
			value = null;
			return false;
		}
	}

	@Override
	public Text getCurrentKey() throws IOException, InterruptedException {
		return key;
	}

	@Override
	public Text getCurrentValue() throws IOException, InterruptedException {
		return value;
	}

	@Override
	public float getProgress() throws IOException, InterruptedException {
		
		// Angabe in 0.0f - 1.0f
		return (100.0f / lines.size() * currentLine) / 100.0f;
	}

	@Override
	public void close() throws IOException {
		
		// Wenn wir fertig sind, schließe das PDF-Dokument
		if (doc != null) {
			doc.close();
		}

	}

}
