/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.myriad.auto2.engine.writer;

import com.myriad.auto2.controllers.SidebarController;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Properties;
import java.util.TreeSet;


/**
 *
 * @author nileshkumar_shegokar
 */
public class PathsWriter {

	static Logger log = LogManager.getLogger(PathsWriter.class.getName());

	public static void writePathsPropertiesFile(String location, String artifactId, Properties properties) throws IOException {
		OutputStream output = null;
		
		System.out.println("Paths:"+properties);
		String newPath = location + File.separator + artifactId + File.separator + "src" + File.separator + "main"
				+ File.separator + "resources";
		Files.createDirectories(Paths.get(newPath));
		//
		Properties tmp = new Properties() {
		    @Override
		    public synchronized Enumeration<Object> keys() {
		        return Collections.enumeration(new TreeSet<Object>(super.keySet()));
		    }
		};
		tmp.putAll(properties);
		//
			output = new FileOutputStream(newPath + File.separator + "paths.properties");
			tmp.store(output, null);
			output.flush();

			if (output != null) {
				output.close();
			}
		
	}

}
