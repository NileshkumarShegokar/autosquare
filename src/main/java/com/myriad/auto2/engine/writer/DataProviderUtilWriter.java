package com.myriad.auto2.engine.writer;




import com.google.googlejavaformat.java.Formatter;
import com.myriad.auto2.controllers.SidebarController;
import com.myriad.auto2.engine.util.ResourceLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;


/**
 * Created by nileshkumar_shegokar on 6/20/2017.
 */
public class DataProviderUtilWriter {
    static Logger log = LogManager.getLogger(DataProviderUtilWriter.class.getName());
    public static void dataProvider(String location,String artifactId,String className,String pkg, Boolean overWrite)throws Exception{
        String template= ResourceLoader.getDataProviderUtilTemplate();
        template=template.replaceAll("@CLASS",className+"DataProvider");
        template=template.replaceAll("@PACKAGE",pkg+".data_provider");        
        String path=location+File.separator+artifactId+File.separator+"src"+File.separator+"main"+File.separator+"resources"+ File.separator+"test_data"+ File.separator+className+".xlsx";
        path = path.replace(File.separator, File.separator+File.separator+File.separator+File.separator);
        template=template.replaceAll("@LOCATION",path);
        String newPath=location+File.separator+artifactId+File.separator+"src"+File.separator+"main"+File.separator+"java";
        for(String newVal : pkg.split("\\.")){
            newPath += (File.separator + newVal);
        }        
            newPath += File.separator+"data_provider";
            Files.createDirectories(Paths.get(newPath));
            template = new Formatter().formatSource(template);
            Files.write(Paths.get(newPath+ File.separator+"DataProviderUtil.java"),template.getBytes());
        
    }
}
