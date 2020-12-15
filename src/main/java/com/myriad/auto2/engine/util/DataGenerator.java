package com.myriad.auto2.engine.util;

import com.myriad.auto2.controllers.SidebarController;
import com.myriad.auto2.edith.DataFactory;
import com.myriad.auto2.edith.util.Language;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class DataGenerator {
	static Logger log = LogManager.getLogger(DataGenerator.class.getName());
	private DataFactory factory;
	private static DataGenerator generator=null;
	private DataGenerator() {}
	public static DataGenerator initialiseDataGenerator(Language language) {
		log.info("Language :" + language);
		if(generator == null) {
			generator=new DataGenerator();
		}
		generator.factory=new DataFactory(language);
		return generator;
	}

	public Object getValue(String type) {
		switch (type) {
		case "First Name": {
			return factory.getFirstName();
		}
		case "Last Name": {
			return factory.getLastName();
		}
		case "Full Name": {
			return factory.getName();
		}
		case "E-Mail": {
			return factory.getEmailAddress();
		}
		case "Address": {
			return factory.getAddress();
		}
		case "Address Line": {
			return factory.getAddressLine2();
		}
		case "DOB": {
			return factory.getBirthDate();
		}
		case "City": {
			return factory.getCity();
		}
		case "Business Name": {
			return factory.getBusinessName();
		}
		case "Street Name": {
			return factory.getStreetName();
		}
		case "Number": {
			return (Integer) factory.getNumber();
		}
		case "Contact Number": {
			return (Long) factory.getContactNumber();
		}
		case "Random Word": {
			return factory.getRandomWord();
		}

		}
		return null;
	}
}