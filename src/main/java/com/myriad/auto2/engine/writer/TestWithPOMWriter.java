package com.myriad.auto2.engine.writer;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.swing.table.DefaultTableModel;

import com.google.googlejavaformat.java.Formatter;
import com.google.googlejavaformat.java.FormatterException;
import com.myriad.auto2.controllers.SidebarController;
import org.apache.commons.lang3.ArrayUtils;
import com.fasterxml.jackson.databind.JsonNode;
import com.myriad.auto2.engine.util.DataVerifier;
import com.myriad.auto2.engine.util.ExpectComposer;
import com.myriad.auto2.engine.util.NameResolver;
import com.myriad.auto2.engine.util.ResourceLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Created by nileshkumar_shegokar on 6/12/2017.
 */
public class TestWithPOMWriter {

	static Logger log = LogManager.getLogger(TestWithPOMWriter.class.getName());

	public String className;
	private String param = "";
	boolean requiredDataProvider = false;
	String lastProcessedPage = null;
	String object = null;
	// param counter
	private Integer paramCount = 1;
	private JsonNode mouseDownEvent = null;
	// flag to identify action object is created in case of drag and drop operation
	private Boolean isActionCreated = false;
	// flag to check if user could have change the tab/window while performing
	// operations
	Integer currentWindow = null;

	public void createTest(String filePath, String artifactId, List<JsonNode> events, Map<String, WebPage> webPages,
			String pkg, String className, String baseURL, Properties properties, Boolean overWrite) throws IOException, FormatterException {
		lastProcessedPage = null;
		String selfPackage = pkg+".test";
		String template = ResourceLoader.getTestTemplate();
		this.className = NameResolver.toDisplayCase(className,NameResolver.CLASS,false);
		template = template.replaceAll("@CLASS", this.className);
		template = template.replaceAll("@PACKAGE", selfPackage);
		template = template.replaceAll("@basePackage",pkg);
		template = template.replaceAll("@DATA_PROVIDER", pkg + ".data_provider." + className + "DataProvider");
		template = template.replaceAll("@PROVIDER_NAME", className + "DataProvider");

		// template=template.replaceAll("@DRIVER",getDriver(browser));
		StringBuffer buffer = new StringBuffer();
		StringBuilder testWriter = new StringBuilder();
		testWriter.append("@Test #DATA_PROVIDER \n" + "  public void " + "test(#PARAM) {\n");

		for (JsonNode event : events) {

			JsonNode data = event.path("data");
			if (currentWindow == null) {
				currentWindow = data.path("window").asInt();
			}

			JsonNode dataData = data.path("data");
			String command = data.path("cmd").asText();
			if (command.equalsIgnoreCase("waitBody")) {
				continue;
			}
			// check for drap and drop : check for mousedown event
			if (command.equalsIgnoreCase("mouseDown")) {
				mouseDownEvent = event;
				continue;
			}
			if (mouseDownEvent != null) {
				// check is its drap and drop event
				if (command.equalsIgnoreCase("mouseUp")) {
					processDragDropEvent(mouseDownEvent, event, lastProcessedPage, testWriter, webPages);
				}
			}
			processEvent(event, properties, testWriter, webPages);
		}

		String message = testWriter.toString();
		message = message.replaceAll("#PARAM", param);
		if (requiredDataProvider) {
			String clsName = NameResolver.toDisplayCase(className, NameResolver.CLASS, false);
			message = message.replaceAll("#DATA_PROVIDER", "(dataProvider=\"" + clsName
					+ "DataProvider\",dataProviderClass="+pkg+".data_provider." + clsName + "DataProvider.class)");
		} else {
			message = message.replaceAll("#DATA_PROVIDER", "");
		}
		testWriter = new StringBuilder(message);
		testWriter.append("}\n");

		template = template.replaceAll("@TEST", testWriter.toString());

		String newPath = filePath + File.separator + artifactId + File.separator + "src" + File.separator + "main"
				+ File.separator + "java";

		for (String newVal : selfPackage.split("\\.")) {
			newPath += (File.separator + newVal);
		}
		log.info("new package Structure :" + newPath);
		Files.createDirectories(Paths.get(newPath));
	//	template = new Formatter().formatSource(template);
		Files.write(Paths.get(newPath + File.separator + this.className + ".java"), template.getBytes());

	}

	private void processDragDropEvent(JsonNode mouseDownEvent, JsonNode event, String lastProcessedPage2,
			StringBuilder testWriter, Map<String, WebPage> webPages) {
		JsonNode data = event.path("data");
		JsonNode dataData = data.path("data");
		String command = data.path("cmd").asText();

		// non textual dataData Node i.e. Data_Data is JsonObject which may carry some
		// event information
		String title = data.path("title").asText();
		WebPage page = webPages.get(title);

		// if its a first iteration then no active page is set for processing
		if ((lastProcessedPage == null || (!lastProcessedPage.equalsIgnoreCase(title))) && !title.isEmpty()) {
			// keep track of current page we are processing
			lastProcessedPage = title;
			String name = NameResolver.toDisplayCase(title, NameResolver.CLASS, true);
			object = NameResolver.toDisplayCase(name + "Page", NameResolver.VARIABLE, true);

			/**
			 *check if object is already declred with same name then remove declaration part
			 * Object obj=new Object();
			 *
			 * second time :
			 * obj=new Object();
			 * */
			String newInstanceName ="  " + name + " " + object ;
			if(!testWriter.toString().contains(newInstanceName)) {
				testWriter.append( "  " + name + " " + object + " = PageFactory.initElements(driver, " + name
						+ ".class);\n");
			}else{
				testWriter.append(object + " = PageFactory.initElements(driver, " + name
						+ ".class);\n");
			}

		}

		// checking if operation is performed on the same window
		if (currentWindow != data.path("window").asInt()) {
			testWriter.append("WindowUtility.switchWindow(driver,\"" + data.path("pageAddress").asText() + "\");\n");
			currentWindow = data.path("window").asInt();
		}

		String function = event.path("function").asText();
		String tag = dataData.path("tag").asText();
		String type = dataData.path("domType").asText();
		log.info(tag);
		if (!isActionCreated) {
			testWriter.append("Actions action=new Actions(driver);	\n");
		}
		String function1 = mouseDownEvent.path("function").asText().trim();
		String function2 = event.path("function").asText().trim();
		Integer x = dataData.path("x").asInt();
		Integer y = dataData.path("y").asInt();
		// WebElement ele = driver.findElement(By.cssSelector("#credit2 > a"));
		// WebElement ele2 = driver.findElement(By.cssSelector("body"));

		testWriter.append("action.clickAndHold(" + object + "." + function1 + "()).moveToElement(" + object + "."
				+ function2 + "()," + x + ", " + y + ").release().build().perform();\n");
		// Using Action class for drag and drop.

		// Dragged and dropped.

	}

	private void processEvent(JsonNode event, Properties properties, StringBuilder testWriter,
			Map<String, WebPage> webPages) {

		JsonNode data = event.path("data");
		JsonNode dataData = data.path("data");
		String command = data.path("cmd").asText();
		// check if data node under data is String not JsonNode i.e.URL command and need
		// to execute driver.get("")
		if (command.equalsIgnoreCase("URL") && dataData.isTextual() && !dataData.isMissingNode()) {
			testWriter.append("driver.get(\"" + dataData.asText() + "\");\n");
		} else {
			// non textual dataData Node i.e. Data_Data is JsonObject which may carry some
			// event information
			String title = data.path("title").asText();
			WebPage page = webPages.get(title);

			// checking if operation is performed on the same window
			if (currentWindow != data.path("window").asInt()) {
				testWriter
						.append("WindowUtility.switchWindow(driver,\"" + data.path("pageAddress").asText() + "\");\n");
				currentWindow = data.path("window").asInt();
			}

			// if its a first iteration then no active page is set for processing
			if ((lastProcessedPage == null || (!lastProcessedPage.equalsIgnoreCase(title))) && !title.isEmpty()) {
				// keep track of current page we are processing
				lastProcessedPage = title;
				String name = NameResolver.toDisplayCase(title, NameResolver.CLASS, true);
				object = NameResolver.toDisplayCase(name + "Page", NameResolver.VARIABLE, true);
				/**
				 *check if object is already declred with same name then remove declaration part
				 * Object obj=new Object();
				 *
				 * second time :
				 * obj=new Object();
				 * */
				String newInstanceName ="  " + name + " " + object ;
				if(!testWriter.toString().contains(newInstanceName)) {
					testWriter.append( "  " + name + " " + object + " = PageFactory.initElements(driver, " + name
							+ ".class);\n");
				}else{
					testWriter.append(object + " = PageFactory.initElements(driver, " + name
							+ ".class);\n");
				}
			}

			String function = event.path("function").asText();
			String tag = dataData.path("tag").asText();
			String type = dataData.path("domType").asText();
			String text = dataData.path("text").asText();

			log.info(tag);
			if (command.equalsIgnoreCase("scrollTo")) {
				Double x = dataData.path("x").asDouble();
				Double y = dataData.path("y").asDouble();
				testWriter.append(
						"((JavascriptExecutor)driver).executeScript(\"window.scrollBy(" + x + "," + y + ")\");\n");
			} else if (command.equalsIgnoreCase("capture")) {
				testWriter.append("SnapShot.takeSnapShot(driver, \"/" + className
						+ "_\\\"+(iterationCounter++)+\\\".png\\\");\n");
			} else if (command.equalsIgnoreCase("expect")) {
				String to = dataData.path("to").asText();
				String propkey = className + "." + text;
				properties.put(propkey, DataVerifier.processIdentifier(to));
				testWriter.append(ExpectComposer.compose(event, object, function));
			} else if (command.equalsIgnoreCase("click")) {
				testWriter.append(object + "." + function.trim() + "();\n");
			} else if (tag != null && !tag.isEmpty() && !title.isEmpty()) {
				if (tag.equalsIgnoreCase("textarea") || tag.equalsIgnoreCase("input")
						|| tag.equalsIgnoreCase("select")) {

					if (!type.equalsIgnoreCase("checkbox") && !type.equalsIgnoreCase("radio")
							&& !type.equalsIgnoreCase("submit") && !type.equalsIgnoreCase("button")) {
						requiredDataProvider = true;
						if (param.isEmpty()) {
							param = ("String param" + (paramCount));
						} else {
							param += (", String param" + (paramCount));
						}
						if (function != null) {
							String key = null;
							if (command.equalsIgnoreCase("sendKeys")) {
								String value = dataData.path("keys").asText();
								String[] keys = value.split("\\{");
								for (int i = 0; i < keys.length; i++) {
									String param = keys[i];
									if (param.lastIndexOf("}") == -1) {
										// delete the array index value
										keys = ArrayUtils.removeElement(keys, keys[i]);
										i--;
									} else {
										String keyVal=param.replaceAll("\\}", "");
										if(keyVal.equalsIgnoreCase("ENTER") || keyVal.equalsIgnoreCase("TAB")) {
											keys[i] = "Keys." +keyVal;
										}else{
											keys = ArrayUtils.removeElement(keys, keys[i]);
											i--;
										}
									}
								}
								key = NameResolver.convertStringArrayToString(keys, "}");
							}
							if (key != null) {
								testWriter.append(
										object + "." + function.trim() + "( param" + (paramCount++) +"\\+ "+ key + " );\n");
							} else
								testWriter
										.append(object + "." + function.trim() + "( param" + (paramCount++) + " );\n");
						}
					} else {
						if (function != null) {
							testWriter.append(object + "." + function.trim() + "();\n");
						}
					}

				} else {
					if (function != null) {
						testWriter.append(object + "." + function.trim() + "();\n");
					}
				}
			}

		}
	}


	private static String getDriver(String browser) {
		String driver = null;
		switch (browser) {
		case "Chrome": {
			driver = "ChromeDriver";
			break;
		}
		case "Firefox": {
			driver = "FirefoxDriver";
			break;
		}
		case "Remote Web Driver": {
			driver = "RemoteWebDriver";
			break;
		}
		}
		return driver;
	}

	public static String[] getRowAt(DefaultTableModel tableModel, int row) {
		int colNumber = 3;
		String[] result = new String[colNumber];
		for (int i = 0; i < colNumber; i++) {
			result[i] = (String) tableModel.getValueAt(row, i);
		}
		return result;
	}

}
