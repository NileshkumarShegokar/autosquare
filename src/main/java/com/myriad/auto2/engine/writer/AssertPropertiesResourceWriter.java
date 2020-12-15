/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.myriad.auto2.engine.writer;

import com.myriad.auto2.controllers.SidebarController;
import com.myriad.auto2.edith.util.Language;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Properties;


/**
 *
 * @author nileshkumar_shegokar
 */
public class AssertPropertiesResourceWriter {

	static Logger log = LogManager.getLogger(AssertPropertiesResourceWriter.class.getName());

	public static void writeResourcePropertiesFile(String location, String artifactId, Properties properties,
			List<Language> languages) throws IOException {
		OutputStream output = null;

		String newPath = location + File.separator + artifactId + File.separator + "src" + File.separator + "main"
				+ File.separator + "resources";
		Files.createDirectories(Paths.get(newPath));
		for (Language language : languages) {
			output = new FileOutputStream(newPath + File.separator + "assert_" + language + ".properties");
			properties.store(output, null);
			output.flush();

			if (output != null) {
				output.close();
			}
		}
	}

}
