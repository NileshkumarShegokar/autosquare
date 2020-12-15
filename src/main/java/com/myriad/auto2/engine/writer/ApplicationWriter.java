/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.myriad.auto2.engine.writer;


import com.google.googlejavaformat.java.Formatter;
import com.google.googlejavaformat.java.FormatterException;
import com.myriad.auto2.engine.util.NameResolver;
import com.myriad.auto2.engine.util.ResourceLoader;
import com.myriad.auto2.model.Project;
import com.myriad.auto2.model.TestCase;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;


/**
 *
 * @author nileshkumar_shegokar
 */
public class ApplicationWriter {
    static Logger log = LogManager.getLogger(ApplicationWriter.class.getName());
    public static void writeApplication(String location,String pkg, String artifactId,Project project, Boolean overWrite) throws IOException, FormatterException {
        String newPath=SpringApplicationWriter.getPath(location,artifactId,pkg,null);
        Files.createDirectories(Paths.get(newPath));
        String template = ResourceLoader.getApplicationTemplate();
        template=template.replaceAll("@package",pkg);
        template=template.replaceAll("@basePackage",pkg);

           Files.createDirectories(Paths.get(newPath));

            StringBuilder builder = new StringBuilder();            
            for(TestCase test:project.getCases()) {
            	builder.append("classes.add(new XmlClass(\"" + pkg+".test."+ NameResolver.toDisplayCase(test.getName(),NameResolver.CLASS,false) + "\"));\n");
            }
            
            template = template.replaceAll("@APPLICATION_CLASSES", builder.toString());
            Files.createDirectories(Paths.get(newPath));
            template = new Formatter().formatSource(template);
            Files.write(Paths.get(newPath + File.separator + "Application.java"), template.getBytes());       
    }
}
