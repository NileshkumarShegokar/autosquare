package com.myriad.auto2.engine.selenium;

import com.myriad.auto2.engine.exceptions.BrowserNotSupportedException;
import com.myriad.auto2.engine.exceptions.SeleniumDriverException;
import com.myriad.auto2.engine.util.ResourceLoader;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

/**
 * Created by nileshkumar_shegokar on 6/11/2017.
 */
public class DriverFactory {
	static Logger log = LogManager.getLogger(DriverFactory.class.getName());
	WebDriver driver = null;

	public WebDriver getDriver(String browser) throws BrowserNotSupportedException {

		switch (browser) {
		case "CHROME": {
			try {
				initializeChromeDriver();
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;
		}		
		default: {
			throw new BrowserNotSupportedException();
		}
		}
		return driver;
	}



	private void initializeChromeDriver()
			throws SeleniumDriverException, IOException, URISyntaxException {
		WebDriverManager.chromedriver().setup();
		DesiredCapabilities capabilities = getChromeCapabilities();

		driver = new ChromeDriver(capabilities);
		driver.manage().window().maximize();
	}

	private DesiredCapabilities getChromeCapabilities() throws URISyntaxException {

		ChromeOptions options = new ChromeOptions();
		options.addArguments("disable-infobars");
		log.info("adding chrome extension to Browser capabilities");
		options.addExtensions(new File(ResourceLoader.getChromeExtension()));
		options.setExperimentalOption("useAutomationExtension", true);
		DesiredCapabilities chrome = new DesiredCapabilities().chrome();
		chrome.setCapability("goog:chromeOptions",options);
		/**
		 the chromeOption name has been change from chromeOption to goog:chromeoption in driver 76
		 */
		//capabilities.setCapability(ChromeOptions., options);
		return chrome;
	}

	private String getChromeDriverOSPath() throws SeleniumDriverException, IOException, URISyntaxException {
		return ResourceLoader.getChomeDriverPath();
	}


}
