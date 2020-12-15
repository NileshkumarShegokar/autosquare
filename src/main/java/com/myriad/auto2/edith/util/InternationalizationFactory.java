package com.myriad.auto2.edith.util;



import com.myriad.auto2.controllers.SidebarController;
import com.myriad.auto2.edith.exception.LanguageFileNotFoundException;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;


/**
 * Created by nileshkumar_shegokar on 6/27/2017.
 */
public class InternationalizationFactory {
    static Logger log = LogManager.getLogger(InternationalizationFactory.class.getName());

    private final Language language;

    public InternationalizationFactory(Language language){
        this.language=language;
    }

    public String loadContent(String contentFile)throws LanguageFileNotFoundException {
    	
    	InputStream url=ResourceLoader.class.getResourceAsStream("/lang/"+language+"/"+contentFile);
        String content=null;
        try {
            	content=IOUtils.toString(url);
        } catch (IOException e) {
            throw new LanguageFileNotFoundException("Error loading language files for data factory",e);
        }
        return content;        
    }
    public String getAddressSuffix() throws LanguageFileNotFoundException{
        return loadContent("address_suffix.data");
    }

    public String getBusinessType() throws LanguageFileNotFoundException{
        return loadContent("business_type.data");
    }

    public String getCity() throws LanguageFileNotFoundException{
        return loadContent("city.data");
    }

    public String getEmailHost()throws LanguageFileNotFoundException {
        return loadContent("email_host.data");
    }

    public String getFirstName() throws LanguageFileNotFoundException{
        return loadContent("first_name.data");
    }

    public String getLastName() throws LanguageFileNotFoundException{
        return loadContent("last_name.data");
    }

    public String getNamePrefix() throws LanguageFileNotFoundException{
        return loadContent("name_prefix.data");
    }

    public String getNameSuffix() throws LanguageFileNotFoundException{
        return loadContent("name_suffix.data");
    }

    public String getStreet() throws LanguageFileNotFoundException{
        return loadContent("street.data");
    }

    public String getTlds() throws LanguageFileNotFoundException{
        return loadContent("tlds.data");
    }

    public String getWords() throws LanguageFileNotFoundException{
        return loadContent("words.data");
    }
}
