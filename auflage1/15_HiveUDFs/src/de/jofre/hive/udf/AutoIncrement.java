package de.jofre.hive.udf;

import org.apache.hadoop.hive.ql.exec.UDF;
import org.apache.hadoop.hive.ql.udf.UDFType;
import org.apache.hadoop.io.LongWritable;

@UDFType(stateful = true)
public class AutoIncrement extends UDF {

	private LongWritable id = new LongWritable();
	
	public AutoIncrement() {
		// UDF wird bei jedem neuen Statement zurückgesetzt.
		id.set(0);
	}
	
	public LongWritable evaluate() {
		
		// Bei jedem Aufruf wird der Counter erhöht...
		id.set(id.get() + 1);
		
		// und zurückgegeben.
		return id;
	}
}
