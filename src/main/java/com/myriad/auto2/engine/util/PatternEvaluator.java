package com.myriad.auto2.engine.util;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.myriad.auto2.edith.util.Language;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


/**
 * Created by nileshkumar_shegokar on 6/23/2017.
 */
public class PatternEvaluator {
	static Logger log = LogManager.getLogger(PatternEvaluator.class.getName());

	public static String solvePattern(String pattern, Language language) {
		String value= resolvePattern(pattern,language);
		return value;
	}
	
	
	public static String resolvePattern(String pattern, Language language) {
		String element = null;

		do {
			log.info("Pattern to resolve :" + pattern);
			int start = pattern.indexOf("{{");
			int end = pattern.indexOf("}}")+2;
			if (start != -1) {
				element = pattern.substring(start, end);
				pattern = resolve(pattern, element, language);
			} else {
				element = null;
			}
		} while (element != null);
		return pattern;
	}

	public static String resolve(String patternData, String element, Language language) {
		DataGenerator generator = DataGenerator.initialiseDataGenerator(language);
		String regex = element;
		while (patternData.contains(element)) {
			element = element.replace("{{", "");
			element = element.replace("}}", "");
			Object object = generator.getValue(element);
			if (object instanceof Integer) {
				patternData = patternData.replace(regex, Integer.toString((Integer) object));
			} else if (object instanceof String) {
				patternData = patternData.replace(regex, (String) object);
			} else if (object instanceof Date) {
				patternData = patternData.replace(regex, dateToString((Date) object));
			}
		}
		return patternData;
	}

	private static CharSequence dateToString(Date date) {
		return new SimpleDateFormat("MM/dd/yyyy").format(date);
	}
}
