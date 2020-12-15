package com.myriad.auto2.engine.writer;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.swing.table.DefaultTableModel;

import com.google.googlejavaformat.java.Formatter;
import com.google.googlejavaformat.java.FormatterException;
import com.myriad.auto2.controllers.SidebarController;
import org.apache.commons.lang3.ArrayUtils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.myriad.auto2.engine.util.DataVerifier;
import com.myriad.auto2.engine.util.ExpectComposer;
import com.myriad.auto2.engine.util.NameResolver;
import com.myriad.auto2.engine.util.ResourceLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Created by nileshkumar_shegokar on 6/12/2017.
 */
public class TestWithoutPOMWriter {

	static Logger log = LogManager.getLogger(TestWithoutPOMWriter.class.getName());

	public String className;
	Integer elementCounter = 1;
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
	// to keep track of elements (already) processed in this test file
	private HashMap<String, JsonNode> listOfWebElement = null;

	public void createTest(String filePath, String artifactId, List<JsonNode> events, Map<String, WebPage> webPages,
			String pkg, String className, String baseURL, Properties paths, Properties properties, Boolean overWrite)
			throws IOException, FormatterException {
		lastProcessedPage = null;
		listOfWebElement = new HashMap<String, JsonNode>();
		String template = ResourceLoader.getTestTemplate();
		this.className = NameResolver.toDisplayCase(className,NameResolver.CLASS,false);
		template = template.replaceAll("@CLASS", this.className);
		template = template.replaceAll("@PACKAGE", pkg);
		template = template.replaceAll("@basePackage",pkg);
		template = template.replaceAll("@DATA_PROVIDER", pkg + ".data_provider." + className + "DataProvider");
		template = template.replaceAll("@PROVIDER_NAME", className + "DataProvider");
		template = template.replaceAll("import com.org.model.*;", "");
		StringBuilder testWriter = new StringBuilder();
		testWriter.append("@Test #DATA_PROVIDER \n" + "  public void " + "test(#PARAM) {\n");

		for (JsonNode event : events) {

			JsonNode data = event.path("data");
			if (currentWindow == null) {
				currentWindow = data.path("window").asInt();
			}

			JsonNode dataData = data.path("data");
			String command = data.path("cmd").asText();
			if (command.equalsIgnoreCase("waitBody") || command.equalsIgnoreCase("end")) {
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
			processEvent(event, paths, properties, testWriter, webPages);
		}

		String message = testWriter.toString();
		message = message.replaceAll("#PARAM", param);
		if (requiredDataProvider) {
			String clsName = NameResolver.toDisplayCase(className, NameResolver.CLASS, false);
			message = message.replaceAll("#DATA_PROVIDER", "(dataProvider=\"" + clsName
					+ "DataProvider\",dataProviderClass=com.org.data_provider." + clsName + "DataProvider.class)");
		} else {
			message = message.replaceAll("#DATA_PROVIDER", "");
		}
		testWriter = new StringBuilder(message);
		testWriter.append("}\n");

		template = template.replaceAll("@TEST", testWriter.toString());

		String newPath = filePath + File.separator + artifactId + File.separator + "src" + File.separator + "main"
				+ File.separator + "java";
		for (String newVal : pkg.split("\\.")) {
			newPath += (File.separator + newVal);
		}
		log.info("new package Structure :" + newPath);
		Files.createDirectories(Paths.get(newPath));
		template = new Formatter().formatSource(template);
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

	private void processEvent(JsonNode event, Properties paths, Properties properties, StringBuilder testWriter,
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
			String text = dataData.path("text").asText();
			if (text == null || text.trim().length() == 0) {
				text = "element" + elementCounter++;
				((ObjectNode) dataData).put("text", text);
			}
			String nameEle = DataVerifier
					.generateIdentifier(NameResolver.toDisplayCase(text, NameResolver.VARIABLE, true));
			if (isElementAlreadyProcessed(title, nameEle)) {
				JsonNode existingElement = listOfWebElement.get(DataVerifier.generateIdentifier(title) + "." + nameEle);
				String existingPath = existingElement.path("data").path("data").path("path").asText();
				String currentPath = event.path("data").path("data").path("path").asText();
				if (!existingPath.equalsIgnoreCase(currentPath)) {
					text = "element" + elementCounter++;
					((ObjectNode) dataData).put("text", text);
					nameEle = NameResolver.toDisplayCase(text, NameResolver.VARIABLE, true);
					updateEventDetails(title, nameEle, event, paths);
				}
			} else {
				// if element is not already processed then process it
				updateEventDetails(title, nameEle, event, paths);
			}

			String function = event.path("function").asText();
			String tag = dataData.path("tag").asText();
			String type = dataData.path("domType").asText();

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
				testWriter.append(ExpectComposer.compose(event, getSelector(event, title, nameEle)));
			} else if (command.equalsIgnoreCase("click")) {
				// generate driver.getElement functionality to get element from page and perform
				// action if css selector
				testWriter.append("driver.findElement(" + getSelector(event, title, nameEle) + ").click();\n");
			} else if (tag != null && !tag.isEmpty() && !title.isEmpty()) {
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
						String keysData = "";
						String[] keys = value.split("\\{");
						for (int i = 0; i < keys.length; i++) {
							String param = keys[i];
							if (param.lastIndexOf("}") == -1) {
								// delete the array index value
								keys = ArrayUtils.removeElement(keys, keys[i]);
								i--;
							} else {
								keys[i] = "Keys." + param.replaceAll("\\}", "");								
							}
						}
						key = NameResolver.convertStringArrayToString(keys, "}");
					}
					if (key != null) {
						testWriter.append("driver.findElement(" + getSelector(event, title, nameEle)
								+ ").sendKeys(param" + (paramCount++) +"\\+ "+ key + ");\n");

					} else
						testWriter.append("driver.findElement(" + getSelector(event, title, nameEle)
								+ ").sendKeys(param" + (paramCount++) + ");\n");
				}

			}

		}
	}

	private void updateEventDetails(String title, String nameEle, JsonNode event, Properties paths) {
		String key = DataVerifier.generateIdentifier(title) + "." + nameEle;
		listOfWebElement.put(key, event);
		String path = HoneyCombPomCreator.getXPath(event);
		paths.put(key, path);
	}

	public boolean isElementAlreadyProcessed(String title, String elementName) {
		String key = DataVerifier.generateIdentifier(title) + "." + elementName;
		for (String element : listOfWebElement.keySet()) {
			if (element.equalsIgnoreCase(key)) {
				return true;
			}
		}
		return false;
	}

	private String getSelector(JsonNode event, String title, String nameEle) {
		String path = HoneyCombPomCreator.getXPath(event);
		String newPath = path.replaceAll("\"", "\\\\\\\\\"");
		String selector = null;
		if (newPath.indexOf("//") != -1) {
			selector = "By.xpath(XPathProperties.getValue(\"" + DataVerifier.generateIdentifier(title) + "." + nameEle
					+ "\"))";
		} else {
			selector = "By.cssSelector(XPathProperties.getValue(\"" + DataVerifier.generateIdentifier(title) + "."
					+ nameEle + "\"))";
		}
		return selector;
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
