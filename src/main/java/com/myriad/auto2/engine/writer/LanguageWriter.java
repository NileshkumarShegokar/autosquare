package com.myriad.auto2.engine.writer;




import com.myriad.auto2.engine.util.ResourceLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;




/**
 * Created by nileshkumar_shegokar on 6/22/2017.
 */
public class LanguageWriter {

    static Logger log = LogManager.getLogger(LanguageWriter.class.getName());

    public static void writeLanguage(String location,String artifactId,String pkg, Boolean overWrite)throws IOException
    {
        String newPath=SpringApplicationWriter.getPath(location,artifactId,pkg,"util");
        Files.createDirectories(Paths.get(newPath));
        String template = ResourceLoader.getLanguageTemplate();
        template=template.replaceAll("@package",pkg+".util");
        Files.createDirectories(Paths.get(newPath));
        Files.write(Paths.get(newPath+File.separator+"Language.java"), template.getBytes());
        

    }
}
