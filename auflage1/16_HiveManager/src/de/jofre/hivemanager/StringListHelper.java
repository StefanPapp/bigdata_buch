package de.jofre.hivemanager;

import java.util.List;

public class StringListHelper {

	public static String joinStringList(List<String> list) {
		StringBuilder sb = new StringBuilder();
		for(int i=0; i<list.size(); i++) {
			sb.append(list.get(i));
			if (i<list.size()-1) {
				sb.append(", ");
			}
		}
		return sb.toString();
	}
}
