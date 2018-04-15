package de.jofre.hive.udf;

import java.util.List;

import org.apache.hadoop.hive.ql.exec.UDFArgumentException;
import org.apache.hadoop.hive.ql.metadata.HiveException;
import org.apache.hadoop.hive.ql.udf.generic.GenericUDF;
import org.apache.hadoop.hive.serde2.objectinspector.ListObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.PrimitiveObjectInspectorFactory;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.StringObjectInspector;
import org.apache.hadoop.io.Text;

public class ListContainsString extends GenericUDF {


	private ListObjectInspector stringList;
	private StringObjectInspector stringItem;

	@Override
	public ObjectInspector initialize(ObjectInspector[] arg0)
			throws UDFArgumentException {

		// Wurden genau 2 Parameter übergeben?
		if (arg0.length != 2) {
			throw new UDFArgumentException(
					"listContainsString nimmt genau zwei Argumente entgegen (List und String).");
		}

		ObjectInspector listType = arg0[0];
		ObjectInspector stringType = arg0[1];

		// Sind die übergebenen Parameter vom richtigen Typ?
		if (!(listType instanceof ListObjectInspector)
				|| !(stringType instanceof StringObjectInspector)) {
			throw new UDFArgumentException("Übergebene Datentypen sind falsch!");
		}

		// Initialisiere Liste und String
		stringList = (ListObjectInspector) listType;
		stringItem = (StringObjectInspector) stringType;

		// Gehe sicher, dass die Liste auch Strings enthält
		if (!(stringList.getListElementObjectInspector() instanceof StringObjectInspector)) {
			throw new UDFArgumentException(
					"Liste muss Strings enthalten, tut sie aber nicht!");
		}

		// Da der Rückgabetyp der Funktion ein Boolean ist, geben wir hier den
		// BooleanObjectInspector zurück.
		return PrimitiveObjectInspectorFactory.javaBooleanObjectInspector;
	}

	@Override
	public Object evaluate(DeferredObject[] arg0) throws HiveException {

		// Hole die Liste von Strings und den String aus dem Aufruf
		List<Text> list = (List<Text>) stringList.getList(arg0[0].get());

		String str = stringItem.getPrimitiveJavaObject(arg0[1].get());

		// Dürfen beide nicht null sein
		if (list == null || str == null) {
			throw new UDFArgumentException(
					"Liste oder String konnte nicht gelesen werden.");
		}

		// Überprüfe, ob String in der Liste enthalten ist
		for (int i = 0; i < list.size(); i++) {
			
			// Cast Text-Objekt zu String
			String value = list.get(i).toString();

			if (value != null) {
				if (str.equals(value)) {
					return new Boolean(true);
				}
			}
		}

		// String wurde in allen Elementen nicht gefunden -> false
		return new Boolean(false);
	}

	@Override
	public String getDisplayString(String[] arg0) {
		return "listContainsString()";
	}

}
