package com.myriad.auto2.engine.util;

import com.myriad.auto2.engine.exceptions.TestNameValidationException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.Properties;


public class Validator {
	static Properties prop = new Properties();

	private static void loadValidationResources() throws IOException, URISyntaxException {
		InputStream input = null;
		input = ResourceLoader.getRestrictions();
		// load a properties file from class path, inside static method
		prop.load(input);
	}

	public static boolean validateTestCaseName(String testCaseName) throws TestNameValidationException {

		try {
			loadValidationResources();
			String testName = prop.getProperty("test_name", null);
			String[] restrcitedNames = testName.split(",");
			for (String name : restrcitedNames) {
				if (name.equalsIgnoreCase(testCaseName)) {
					return false;
				}
			}
		} catch (Exception ex) {
			throw new TestNameValidationException("Test Name validation failed", ex);
		}
		return true;

	}

}
