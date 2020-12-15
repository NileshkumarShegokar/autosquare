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
 * Created by nileshkumar_shegokar on 6/16/2017.
 */
public class MavenPOMWriter {
    static Logger log = LogManager.getLogger(MavenPOMWriter.class.getName());

    public static void writePOM(String location,String artifactId,String groupId, String versionID, Boolean overWrite) throws IOException, FormatterException {

        if(!location.isEmpty()) {
            String template = ResourceLoader.getPOMTemplate();
            template = template.replaceAll("@GROUP_ID", groupId);
            template = template.replaceAll("@ARTIFACT_ID", artifactId);
            template = template.replaceAll("@VERSION", versionID);
            String filePath=location +File.separator+ artifactId;
            Files.createDirectories(Paths.get(filePath));
            Path path = Files.write(Paths.get(filePath+File.separator+"pom.xml"), template.getBytes());            
        }
    }

}
