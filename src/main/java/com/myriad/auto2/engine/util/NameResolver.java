package com.myriad.auto2.engine.util;

public class NameResolver {

	public static final String VARIABLE = "variable";
	public static final String CLASS = "class";
	
	public static String toDisplayCase(String data, String type, Boolean resolveNumbers) {
		final String ACTIONABLE_DELIMITERS = " '-_/";
		StringBuilder sb = new StringBuilder();
		boolean capNext = true;
		//replace all special symbol and spaces through regex
		data = data.replaceAll("[^a-zA-Z0-9]", "");
		if(resolveNumbers) {
		data=data.replaceAll("[0-9]","");
		}
		
		for (char var : data.toCharArray()) {
			var = (capNext) ? Character.toUpperCase(var) : var;
			sb.append(var);
			capNext = (ACTIONABLE_DELIMITERS.indexOf((int) var) >= 0);
		}
		

		if (type.equalsIgnoreCase(VARIABLE)) {
			sb.replace(0, 1, "" + Character.toLowerCase(sb.charAt(0)));
		}

		if (type.equalsIgnoreCase(CLASS)) {
			sb.replace(0, 1, "" + Character.toUpperCase(sb.charAt(0)));
		}
		// limit variable/class name length to 20
		if (sb.length() > 20) {
			return sb.substring(0, 20);
		} else {
			return sb.toString().trim();
		}
	}
	
	public static String convertStringArrayToString(String[] strArr, String delimiter) {
		StringBuilder sb = new StringBuilder();
                if(strArr.length!=0){
		for (String str : strArr)
			sb.append(str).append(delimiter);
                }
                if(sb.length()!=0)
		return sb.substring(0, sb.length() - 1);
                return null;
	}
}
