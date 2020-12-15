package com.myriad.auto2.engine.selenium;


import com.myriad.auto2.controllers.SidebarController;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author nileshkumar_shegokar
 */
public class URIToNameConvertor {
    static Logger log = LogManager.getLogger(URIToNameConvertor.class.getName());
    public static String toClassName(String uri){
        while(uri.indexOf("/") != -1){
            int index=uri.indexOf("/");            
            String replace="";
            if(index+2 <= uri.length())
            replace=uri.substring(index, index+2);            
            else
            replace=uri.substring(index, index+1);                
            uri=uri.replaceAll(replace, process(replace));
        }
        if(uri.isEmpty()){
            return "Index";
        }
        return uri;
    }
    
    public static String process(String slash){
        if(slash.length()==1){
            return "";
        }
        String letter=slash.substring(slash.length() -1 );
        return letter.toUpperCase();
    }
    
    public static void main(String args[])
    {
        log.info(URIToNameConvertor.get("id('name_3_firstname')"));
    }
    
    public static String get(String data){
    String value=data.substring(data.indexOf("'")+1, data.lastIndexOf("'"));
    return value;
    }
}
