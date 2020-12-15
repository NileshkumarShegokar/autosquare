/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.myriad.auto2.engine.writer;


import com.google.googlejavaformat.java.Formatter;
import com.google.googlejavaformat.java.FormatterException;
import com.myriad.auto2.controllers.SidebarController;
import com.myriad.auto2.engine.util.ResourceLoader;
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
public class SnapshotWriter {

    static Logger log = LogManager.getLogger(SnapshotWriter.class.getName());
    
     public static void writeSnapshot(String location,String artifactId,String pkg, Boolean overWrite) throws IOException, FormatterException {
        String newPath=SpringApplicationWriter.getPath(location,artifactId,pkg,"util");
        Files.createDirectories(Paths.get(newPath));
        String template = ResourceLoader.getSnapshotTemplate();
        template=template.replaceAll("@package",pkg+".util");
        template = new Formatter().formatSource(template);
        Path path = Files.write(Paths.get(newPath+File.separator+"SnapShot.java"), template.getBytes());
        

    }
}
