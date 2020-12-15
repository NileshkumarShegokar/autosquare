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
 * Created by nileshkumar_shegokar on 6/22/2017.
 */
public class DriverFactoryWriter {

    static Logger log = LogManager.getLogger(DriverFactoryWriter.class.getName());
    public static void writeFactory(String location,String artifactId,String pkg, Integer timeout, Boolean overWrite) throws IOException, FormatterException {
        String newPath=SpringApplicationWriter.getPath(location,artifactId,pkg,"util");
        Files.createDirectories(Paths.get(newPath));
        String template = ResourceLoader.getDriverFactoryTemplate();
        template=template.replaceAll("@package",pkg+".util");
        template=template.replaceAll("#TIMEOUT", timeout.toString());
        Files.createDirectories(Paths.get(newPath));
        template = new Formatter().formatSource(template);
        Files.write(Paths.get(newPath+File.separator+"DriverFactory.java"), template.getBytes());
        

    }
}
