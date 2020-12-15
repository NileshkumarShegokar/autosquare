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
public class ApplicationPropertiesWriter {

    static Logger log = LogManager.getLogger(ApplicationPropertiesWriter.class.getName());
    
    public static void writeApplicationProperties(String location,String artifactId,List<String> browsers, Boolean overWrite,List<Language> languages) throws IOException{
        Properties prop = new Properties();
        OutputStream output = null;

            String newPath=location+File.separator+artifactId+File.separator+"src"+File.separator+"main"+File.separator+"resources";
            Files.createDirectories(Paths.get(newPath));
            output = new FileOutputStream(newPath+File.separator+"application.properties");

            // set the properties value
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < browsers.size(); i++) {
                builder.append(browsers.get(i));
                if (i != (browsers.size() - 1)) {
                    builder.append(",");
                }
            }

            prop.setProperty("browser", builder.toString());
            if(!languages.isEmpty()) {
            	prop.setProperty("language",languages.get(0).name());
            }
            // save properties to project root folder
            prop.store(output, null);
            output.flush();
        
            if (output != null) {
                    output.close();                
            }

        
    }

}
