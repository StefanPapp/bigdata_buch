package de.jofre.xlsoutput;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.RecordWriter;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

public class XLSOutputFormat extends FileOutputFormat<Text, IntWritable> {

	private final static Logger log = Logger.getLogger(XLSOutputFormat.class.getName());
	
	@Override
	public RecordWriter<Text, IntWritable> getRecordWriter(TaskAttemptContext job)
			throws IOException, InterruptedException {
		
		// Erzeuge Zieldatei
		FileSystem fs = FileSystem.get(job.getConfiguration());
		Path path = FileOutputFormat.getOutputPath(job);
		Path file = new Path("output_"+job.getTaskAttemptID().getTaskID()+".xls");
		Path absolute = new Path(path, file);
		FSDataOutputStream out = fs.create(absolute);
		
		log.log(Level.INFO, "Ausgabedatei des RecordWriters ist '"+absolute.getName()+"'.");
		
		// Erstelle Excel-Datei
		HSSFWorkbook workbook = new HSSFWorkbook();
		
		// Erstelle Excel-Sheet
		workbook.createSheet("Log-Auswertung");
		
		return new XLSRecordWriter(workbook, out);
	}
}
