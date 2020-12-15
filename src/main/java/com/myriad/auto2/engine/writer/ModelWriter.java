/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.myriad.auto2.engine.writer;


import com.google.googlejavaformat.java.Formatter;
import com.myriad.auto2.controllers.SidebarController;
import com.myriad.auto2.engine.util.NameResolver;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


/**
 *
 * @author nileshkumar_shegokar
 */
public class ModelWriter {

    static Logger log = LogManager.getLogger(ModelWriter.class.getName());

    public static void writePOM(PomCreator pomCreator,String location, String artifactId, String pkg, WebPage page, int timeout) throws Exception {

        String newPath = location + File.separator + artifactId + File.separator + "src" + File.separator + "main" + File.separator + "java";
        for (String newVal : pkg.split("\\.")) {
            newPath += (File.separator + newVal);
        }
        newPath += File.separator + "model";        
        Files.createDirectories(Paths.get(newPath));
        File file=new File(newPath + File.separator + NameResolver.toDisplayCase(page.getTitle(),NameResolver.CLASS,true) + ".java");
        if(file.exists()){
            Path path=Paths.get(newPath + File.separator + NameResolver.toDisplayCase(page.getTitle(),NameResolver.CLASS,true) + ".java");
            String template = pomCreator.updateExistingPOM(pkg+".model", page.getTitle(),new String(Files.readAllBytes(path)));
            log.info("Model Written at :"+newPath);
            template = new Formatter().formatSource(template);
            Files.write(path, template.getBytes());
        }else{
            String template = pomCreator.createPom(pkg+".model", page.getTitle(), timeout);
            log.info("Model Written at :"+newPath);
            template = new Formatter().formatSource(template);
            Files.write(Paths.get(newPath + File.separator + NameResolver.toDisplayCase(page.getTitle(),NameResolver.CLASS,true) + ".java"), template.getBytes());
        }
    }
}
