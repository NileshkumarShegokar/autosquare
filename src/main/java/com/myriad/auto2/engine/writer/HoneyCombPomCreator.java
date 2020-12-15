package com.myriad.auto2.engine.writer;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.myriad.auto2.engine.util.NameResolver;
import com.myriad.auto2.engine.util.ResourceLoader;

import java.util.*;

/**
 * Created by nileshkumar_shegokar on 4/17/2017.
 */
public class HoneyCombPomCreator {

    static HashMap<String, JsonNode> listOfWebElement = null;

    public static String createPom(String packageName, String pageTitle, String pageURL, String claimant,
                                   String pageName, LinkedHashMap<String, WebPage> webPages, int timeout, String pageLoadedText)
            throws Exception {
        Integer elementCounter = 1;
        // list of elements to maintain and verify that one web element should exist
        // only once in POM even after eventlist has multiple operation in the same
        // webelement
        listOfWebElement = new HashMap<String, JsonNode>();
        pageTitle = NameResolver.toDisplayCase(pageTitle, NameResolver.CLASS, true);
        StringBuilder template = new StringBuilder(ResourceLoader.getPageObjectModelTemplate());

        addCopyRight(template, claimant);
        addPackageName(template, packageName);
        updateAllInTemplate(template, "#modelName", pageTitle);
        updateAllInTemplate(template, "#timeout", "" + timeout);
        updateAllInTemplate(template, "#pageLoadedText", pageLoadedText);
        updateAllInTemplate(template, "#title", pageTitle);
        if (pageURL != null)
            updateAllInTemplate(template, "#pageUrl", pageURL);

        // variableDeclarations
        StringBuffer variables = new StringBuffer();
        WebPage page = webPages.get(pageName);
        for (JsonNode event : page.getJsonNodes()) {
            JsonNode eventData = event.path("data");
            String command = eventData.path("cmd").asText();
            if (command.equalsIgnoreCase("scrollTo")) {
                continue;
            }
            JsonNode dataData = eventData.path("data");
            String text = dataData.path("text").asText();
            String path = getXPath(event);
            String newPath = path.replaceAll("\"", "\\\\\\\"");
            String xPath = "";
            if (newPath.indexOf("//") != -1) {
                xPath = "xpath=\"" + newPath + "\"";
            } else {
                xPath = "css=\"" + newPath + "\"";
            }
            if (text == null || text.trim().length() == 0) {
                text = "element" + elementCounter++;
                ((ObjectNode) dataData).put("text", text);
            }
            String nameEle = NameResolver.toDisplayCase(text, NameResolver.VARIABLE, true);
            if (isElementAlreadyProcessed(nameEle)) {
                JsonNode existingElement = listOfWebElement.get(nameEle);
                String existingPath = existingElement.path("data").path("data").path("path").asText();
                String currentPath = event.path("data").path("data").path("path").asText();
                if (!existingPath.equalsIgnoreCase(currentPath)) {
                    text = "element" + elementCounter++;
                    ((ObjectNode) dataData).put("text", text);
                    nameEle = NameResolver.toDisplayCase(text, NameResolver.VARIABLE, true);
                }
            }

            if (!isElementAlreadyProcessed(nameEle)) {
                variables.append("@FindBy(" + xPath + ") \n\t");
                variables.append("@CacheLookup \n\t");
                variables.append("private WebElement " + nameEle + "; \n\n\t");

                ObjectNode objectNode = (ObjectNode) event;
                objectNode.put("elementName", nameEle);
                System.out.println("event from POM Creator:" + event.toString());

                listOfWebElement.put(nameEle, event);
            } else {
                ObjectNode objectNode = (ObjectNode) event;
                objectNode.put("elementName", nameEle);
                System.out.println("event from POM Creator:" + event.toString());

            }

        }
        String value = variables.toString();
        updateTemplate(template, "#variableDeclarations", value);

        // Operations
        StringBuffer operations = new StringBuffer();
        processOperation(operations, template, page, pageTitle);
        updateTemplate(template, "#operations", operations.toString());
        updateTemplate(template, "#attributes", createAttributes(pageTitle));
        return template.toString();
    }

    private static void processOperation(StringBuffer operations, StringBuilder template, WebPage page,
                                         String pageTitle) {

        for (JsonNode element : page.getJsonNodes()) {
            String elementName = element.path("elementName").asText();
            JsonNode eventData = element.path("data");
            JsonNode dataData = eventData.path("data");
            String tag = dataData.path("tag").asText();
            String domType = dataData.path("domType").asText();
            String command = eventData.path("cmd").asText();
            String to = dataData.path("to").asText();
            if (!tag.isEmpty()) {
                if (command.equalsIgnoreCase("click")) {
                    ObjectNode objectNode = (ObjectNode) element;
                    String function = " click" + NameResolver.toDisplayCase(elementName + "Link", NameResolver.CLASS, true);
                    objectNode.put("function", function);
                    if (checkFunctionExists(operations, function)) {
                        continue;
                    }
                    operations = operations.append("     /**\n" + "     * Click on " + elementName + " Link.\n"
                            + "     *\n" + "     * @return the " + pageTitle + " c9lass instance.\n" + "     */\n"
                            + "    public " + pageTitle + function + "() {\n" + "        " + elementName + ".click();\n"
                            + "        return this;\n" + "    }\n\n");
                } else if (command.equalsIgnoreCase("mouseDown") || command.equalsIgnoreCase("mouseUp")) {
                    ObjectNode objectNode = (ObjectNode) element;
                    String function = " get" + NameResolver.toDisplayCase(elementName, NameResolver.CLASS, true);
                    objectNode.put("function", function);
                    if (checkFunctionExists(operations, function)) {
                        continue;
                    }
                    operations = operations.append("     /**\n" + "     * get " + elementName + " element.\n"
                            + "     *\n" + "     * @return the web element class instance.\n" + "     */\n"
                            + "    public WebElement" + function + "() {\n" + "        return " + elementName
                            + ";\n};\n\n");
                } else if (command.equalsIgnoreCase("sendKeys")) {
                    ObjectNode objectNode = (ObjectNode) element;
                    String function = " set" + NameResolver.toDisplayCase(elementName + "Field", NameResolver.CLASS, true);
                    objectNode.put("function", function);
                    if (checkFunctionExists(operations, function)) {
                        continue;
                    }
                    operations = operations.append("     /**\n" + "     * Set value to " + elementName + " Field.\n"
                            + "     *\n" + "     * @return the " + pageTitle + " class instance.\n" + "     */\n"
                            + "    public " + pageTitle + function + "(String " + elementName + "Value) {\n"
                            + "        " + elementName + ".sendKeys(" + elementName + "Value);\n"
                            + "        return this;\n" + "    }\n\n");
                } else if (tag.equalsIgnoreCase("select")) {
                    ObjectNode objectNode = (ObjectNode) element;
                    String function = " set"
                            + NameResolver.toDisplayCase(elementName + "DropDownListField", NameResolver.CLASS, true);
                    objectNode.put("function", function);
                    if (checkFunctionExists(operations, function)) {
                        continue;
                    }
                    operations = operations
                            .append("     /**\n" + "     * set value to " + elementName + " drop down list field.\n"
                                    + "     *\n" + "     * @return the " + pageTitle + " class instance.\n"
                                    + "     */\n" + "    public " + pageTitle + function + "(String " + elementName
                                    + "Value ) {\n" + "        new Select(" + elementName + ").selectByVisibleText("
                                    + elementName + "Value);\n" + "        return this;\n" + "    }\n\n");
                    operations = operations.append("     /**\n" + "     * set value to " + elementName
                            + " drop down list field.\n" + "     *\n" + "     * @return the " + pageTitle
                            + " class instance.\n" + "     */\n" + "    public " + pageTitle + " unset"
                            + NameResolver.toDisplayCase(elementName + "DropDownListField", NameResolver.CLASS, true)
                            + "(String " + elementName + "Value ) {\n" + "        new Select(" + elementName
                            + ").deselectByVisibleText(" + elementName + "Value);\n" + "        return this;\n"
                            + "    }\n\n");

                    operations = operations.append("     /**\n" + "     * click to " + elementName
                            + " drop down list field.\n" + "     *\n" + "     * @return the " + pageTitle
                            + " class instance.\n" + "     */\n" + "    public " + pageTitle + " click"
                            + NameResolver.toDisplayCase(elementName + "DropDownListField", NameResolver.CLASS, true)
                            + "() {\n" + "     if (!" + elementName + ".isSelected()) {\n" + "            "
                            + elementName + ".click();\n" + "        }" + "        return this;" + "    }\n\n");
                }
            } else if (command.equalsIgnoreCase("expect")) {
                operations = addExpect(operations, template, element);
            }
        }
    }

    private static String createAttributes(String pageTitle) {
        StringBuffer attributes = new StringBuffer();

        attributes.append("     /**\n" + "     * Verify that the page loaded completely.\n" + "     *\n"
                + "     * @return the " + pageTitle + " class instance.\n" + "     */\n" + "    public " + pageTitle
                + " verifyPageLoaded() {\n"
                + "        (new WebDriverWait(driver, timeout)).until(new ExpectedCondition<Boolean>() {\n"
                + "            public Boolean apply(WebDriver d) {\n"
                + "                return d.getPageSource().contains(pageLoadedText);\n" + "            }\n"
                + "        });\n" + "        return this;\n" + "    }\n\n");

        attributes.append("     /**\n" + "     * Verify that current page URL matches the expected URL.\n" + "     *\n"
                + "     * @return the " + pageTitle + " class instance.\n" + "     */\n" + "    public " + pageTitle
                + " verifyPageUrl() {\n"
                + "        (new WebDriverWait(driver, timeout)).until(new ExpectedCondition<Boolean>() {\n"
                + "            public Boolean apply(WebDriver d) {\n"
                + "                return d.getCurrentUrl().contains(pageUrl);\n" + "            }\n" + "        });\n"
                + "        return this;\n" + "    }");

        attributes.append("     /**\n" + "     * Verify that current page title matches the expected title.\n"
                + "     *\n" + "     * @return the " + pageTitle + " class instance.\n" + "     */\n" + "    public "
                + pageTitle + " verifyPageTitle() {\n"
                + "        (new WebDriverWait(driver, timeout)).until(new ExpectedCondition<Boolean>() {\n"
                + "            public Boolean apply(WebDriver d) {\n"
                + "                return title.equalsIgnoreCase(d.getTitle());\n" + "            }\n" + "        });\n"
                + "        return this;\n" + "    }\n\n");
        return attributes.toString();
    }

    private static void updateAllInTemplate(StringBuilder template, String valueToReplace,
                                            String valueToBeReplacedWith) {
        Integer index = template.indexOf(valueToReplace);
        while (index != -1) {
            template = template.replace(index, index + valueToReplace.length(), valueToBeReplacedWith);
            index = template.indexOf(valueToReplace);
        }
    }

    private static void updateTemplate(StringBuilder template, String valueToReplace, String valueToBeReplacedWith) {
        Integer index = template.indexOf(valueToReplace);
        template = template.replace(index, index + valueToReplace.length(), valueToBeReplacedWith);
    }

    private static void addPackageName(StringBuilder template, String packageName) {
        if (packageName != null && packageName.trim().length() != 0) {
            String val = "#packageName";
            Integer index = template.indexOf(val);
            template = template.replace(index, index + val.length(), packageName);
        } else {
            String val = "package #packageName;";
            Integer index = template.indexOf(val);
            template = template.replace(index, index + val.length(), "");
        }
    }

    private static void addCopyRight(StringBuilder data, String claimant) {
        // Copyright
        if (claimant != null) {
            StringBuffer copyrights = new StringBuffer();
            copyrights.append("/*\n" + "All the code that follow is\n" + "Copyright (c) "
                    + Calendar.getInstance().get(Calendar.YEAR) + ", " + claimant + ". All Rights Reserved.\n"
                    + "Not for reuse without permission.\n" + "*/");

            data = data.replace(data.indexOf("#copyright"), data.indexOf("#copyright") + 10, copyrights.toString());
        } else {
            data = data.replace(data.indexOf("#copyright"), data.indexOf("#copyright") + 10, "");
        }
    }

    private static StringBuffer addExpect(StringBuffer operations, StringBuilder template, JsonNode element) {
        String elementName = element.path("elementName").asText();
        JsonNode eventData = element.path("data");
        JsonNode dataData = eventData.path("data");
        String to = dataData.path("to").asText();
        String type = dataData.path("type").asText();
        switch (type) {
            case "val": {
                ObjectNode objectNode = (ObjectNode) element;
                String function = " get" + NameResolver.toDisplayCase(elementName + "Value", NameResolver.CLASS, true);
                objectNode.put("function", function);
                if (checkFunctionExists(operations, function)) {
                    return operations;
                }
                operations.append("     /**\n" + "     * Set value to " + elementName + " Field.\n" + "     *\n"
                        + "     * @return the string class instance.\n" + "     */\n" + "    public String" + function
                        + "() {\n" + "        return " + elementName + ".getAttribute(\"value\");\n" + "    }\n\n");
                break;
            }
            case "text": {
                ObjectNode objectNode = (ObjectNode) element;
                String function = " get" + NameResolver.toDisplayCase(elementName + "Text", NameResolver.CLASS, true);
                objectNode.put("function", function);
                if (checkFunctionExists(operations, function)) {
                    return operations;
                }
                operations.append("     /**\n" + "     * Set value to " + elementName + " Field.\n" + "     *\n"
                        + "     * @return the string class instance.\n" + "     */\n" + "    public String" + function
                        + "() {\n" + "        return " + elementName + ".getText();\n" + "    }\n\n");
                break;
            }
            case "displayed": {
                ObjectNode objectNode = (ObjectNode) element;
                String function = " get" + NameResolver.toDisplayCase(elementName + "Displayed", NameResolver.CLASS, true);
                objectNode.put("function", function);
                if (checkFunctionExists(operations, function)) {
                    return operations;
                }
                operations.append("     /**\n" + "     * Set value to " + elementName + " Field.\n" + "     *\n"
                        + "     * @return the string class instance.\n" + "     */\n" + "    public Boolean" + function
                        + "() {\n" + "        return " + elementName + ".isDisplayed();\n" + "    }\n\n");
                break;
            }
            case "enabled": {
                ObjectNode objectNode = (ObjectNode) element;
                String function = " get" + NameResolver.toDisplayCase(elementName + "Enabled", NameResolver.CLASS, true);
                objectNode.put("function", function);
                if (checkFunctionExists(operations, function)) {
                    return operations;
                }
                operations.append("     /**\n" + "     * Set value to " + elementName + " Field.\n" + "     *\n"
                        + "     * @return the string class instance.\n" + "     */\n" + "    public Boolean" + function
                        + "() {\n" + "        return " + elementName + ".isEnabled();\n" + "    }\n\n");
                break;
            }
            case "selected": {
                ObjectNode objectNode = (ObjectNode) element;
                String function = " get" + NameResolver.toDisplayCase(elementName + "Selected", NameResolver.CLASS, true);
                objectNode.put("function", function);
                if (checkFunctionExists(operations, function)) {
                    return operations;
                }
                operations.append("     /**\n" + "     * get value to " + elementName + " Field.\n" + "     *\n"
                        + "     * @return the boolean class instance.\n" + "     */\n" + "    public Boolean" + function
                        + "() {\n" + "        return " + elementName + ".isSelected();\n" + "    }\n\n");
                break;
            }
            case "attr": {
                ObjectNode objectNode = (ObjectNode) element;
                String function = " is" + NameResolver.toDisplayCase(elementName + "AttrAvailable", NameResolver.CLASS, true);
                objectNode.put("function", function);
                if (checkFunctionExists(operations, function)) {
                    return operations;
                }
                operations.append("     /**\n" + "     * get value to " + elementName + " Field attirbute.\n" + "     *\n"
                        + "     * @return the boolean class instance.\n" + "     */\n" + "    public Boolean" + function
                        + "() {\nBoolean result = false;\r\n" + "    try {\r\n" + "        String value = " + elementName
                        + ".getAttribute(" + to + ");\r\n" + "        if (value != null){\r\n"
                        + "            result = true;\r\n" + "        }\r\n" + "    } catch (Exception e) {}\r\n" + "\r\n"
                        + "    return result;}\n\n");
                break;
            }
            case "css": {
                ObjectNode objectNode = (ObjectNode) element;
                String function = " get" + NameResolver.toDisplayCase(elementName + "Selected", NameResolver.CLASS, true);
                objectNode.put("function", function);
                if (checkFunctionExists(operations, function)) {
                    return operations;
                }
                operations.append("     /**\n" + "     * get value to " + elementName + " Field.\n" + "     *\n"
                        + "     * @return the boolean class instance.\n" + "     */\n" + "    public Boolean" + function
                        + "() {\nString classes = " + elementName + ".getAttribute(\"class\");\r\n"
                        + "					    for (String c : classes.split(\" \")) {\r\n"
                        + "					        if (c.equals(" + to + ")) {\r\n"
                        + "					            return true;\r\n" + "					        }\r\n"
                        + "					    }\r\n" + "\r\n" + "					    return false;}\n\n");
                break;
            }
            case "url": {
                ObjectNode objectNode = (ObjectNode) element;
                String function = NameResolver.toDisplayCase("checkCurrentURL", NameResolver.CLASS, true);
                objectNode.put("function", function);
                if (checkFunctionExists(operations, function)) {
                    return operations;
                }
                operations.append("     /**\n" + "     * check current url value.\n" + "     *\n"
                        + "     * @return the boolean class instance.\n" + "     */\n" + "    public Boolean" + function
                        + "() {\n" + "        return driver.getCurrentUrl().equalsIgnoreCase(\"" + to + "\");\n"
                        + "    }\n\n");
                break;
            }
            case "title": {
                ObjectNode objectNode = (ObjectNode) element;
                String function = NameResolver.toDisplayCase("checkCurrentTitle", NameResolver.CLASS, true);
                objectNode.put("function", function);
                if (checkFunctionExists(operations, function)) {
                    return operations;
                }
                operations.append("     /**\n" + "     * check current url value.\n" + "     *\n"
                        + "     * @return the boolean class instance.\n" + "     */\n" + "    public Boolean" + function
                        + "() {\n" + "        return driver.getTitle().equalsIgnoreCase(\"" + to + "\");\n" + "    }\n\n");
                break;
            }
            case "cookie": {

                ObjectNode objectNode = (ObjectNode) element;
                String function = NameResolver.toDisplayCase("checkCookie", NameResolver.CLASS, true);
                objectNode.put("function", function);
                if (checkFunctionExists(operations, function)) {
                    return operations;
                }
                operations.append("     /**\n" + "     * check current url value.\n" + "     *\n"
                        + "     * @return the boolean class instance.\n" + "     */\n" + "    public Boolean" + function
                        + "() {\n     Cookie cookie= driver.manage().getCookieNamed(\"" + to + "\");\r\n"
                        + "						boolean status=false;\r\n" + "						if (cookie!=null) {\r\n"
                        + "							return true;\r\n" + "						}\r\n"
                        + "						return status;\n" + "    }\n\n");
                break;
            }
            case "localStorage": {
                ObjectNode objectNode = (ObjectNode) element;
                String function = NameResolver.toDisplayCase("checkLocalStorage", NameResolver.CLASS, true);
                objectNode.put("function", function);
                if (checkFunctionExists(operations, function)) {
                    return operations;
                }
                operations.append("     /**\n" + "     * check current url value.\n" + "     *\n"
                        + "     * @return the boolean class instance.\n" + "     */\n" + "    public Boolean" + function
                        + "() {\n String item=((WebStorage) driver.getLocalStorage()).getItem(\"" + to + "\"); \r\n"
                        + "						boolean status=false;\r\n"
                        + "						if(!item.isEmpty()) {\r\n" + "							status=true;\r\n"
                        + "						}\r\n" + "						return status;" + "    }\n\n");
                break;
            }
            case "sessionStorage": {
                ObjectNode objectNode = (ObjectNode) element;
                String function = NameResolver.toDisplayCase("checkSessionStorage", NameResolver.CLASS, true);
                objectNode.put("function", function);
                if (checkFunctionExists(operations, function)) {
                    return operations;
                }
                operations.append("     /**\n" + "     * check current url value.\n" + "     *\n"
                        + "     * @return the boolean class instance.\n" + "     */\n" + "    public Boolean" + function
                        + "() {\n String item=((WebStorage) driver.getSessionStorage()).getItem(\"" + to + "\"); \r\n"
                        + "						boolean status=false;\r\n"
                        + "						if(!item.isEmpty()) {\r\n" + "							status=true;\r\n"
                        + "						}\r\n" + "						return status;" + "    }\n\n");
                break;
            }
            case "alert": {
                ObjectNode objectNode = (ObjectNode) element;
                String function = NameResolver.toDisplayCase("checkAlert", NameResolver.CLASS, true);
                objectNode.put("function", function);
                if (checkFunctionExists(operations, function)) {
                    return operations;
                }
                operations.append("     /**\n" + "     * check current url value.\n" + "     *\n"
                        + "     * @return the boolean class instance.\n" + "     */\n" + "    public Boolean" + function
                        + "() {\n Alert alert = driver.switchTo().alert();\r\n"
                        + "						Boolean status=false;\r\n" + "						if(alert!=null)\r\n"
                        + "						{\r\n" + "							status=true;\r\n"
                        + "						}\r\n" + "						return status; " + "    }\n\n");
                break;
            }
        }
        return operations;
    }

    static String updatePOM(String packageName, String pageTitle, String pageName,
                            LinkedHashMap<String, WebPage> webPages, StringBuilder template) {
        Integer elementCounter = 1;
        // variableDeclarations
        StringBuffer variables = new StringBuffer("//Variable declarations \n");
        WebPage page = webPages.get(pageName);
        List<JsonNode> newElements = new ArrayList<JsonNode>();
        for (JsonNode event : page.getJsonNodes()) {
            JsonNode eventData = event.path("data");
            String command = eventData.path("cmd").asText();
            if (command.equalsIgnoreCase("scrollTo")) {
                continue;
            }
            JsonNode dataData = eventData.path("data");
            String text = dataData.path("text").asText();
            String path = getXPath(event);
            String newPath = path.replaceAll("\"", "\\\\\\\"");
            String xPath = "";
            if (newPath.indexOf("#") != -1) {
                xPath = "css=\"" + newPath + "\"";
            } else if (newPath.indexOf("//") != -1) {
                xPath = "xpath=\"" + newPath + "\"";
            } else {
                xPath = "css=\"" + newPath + "\"";
            }

            // check if element is already available in existing POM
            if (template.indexOf(xPath) != -1) {
                // load element name from template to get earlier created element name
                operateExistingElement(xPath, event, template);
                // as element name is loaded; skip element from iteration as the element with
                // XPATH is already exist in POM
                continue;
            }
            // checking element existance in POM completed

            if (text == null || text.trim().length() == 0) {
                text = "element" + elementCounter++;
                ((ObjectNode) dataData).put("text", text);
            }
            String nameEle = NameResolver.toDisplayCase(text, NameResolver.VARIABLE, true);
            if (isElementAlreadyProcessed(nameEle)) {
                JsonNode existingElement = listOfWebElement.get(nameEle);
                String existingPath = existingElement.path("data").path("data").path("path").asText();
                String currentPath = event.path("data").path("data").path("path").asText();
                if (!existingPath.equalsIgnoreCase(currentPath)) {
                    text = "element" + elementCounter++;
                    ((ObjectNode) dataData).put("text", text);
                    nameEle = NameResolver.toDisplayCase(text, NameResolver.VARIABLE, true);
                }
            }

            if (!isElementAlreadyProcessed(nameEle)) {
                variables.append("@FindBy(" + xPath + ") \n\t");
                variables.append("@CacheLookup \n\t");
                variables.append("private WebElement " + nameEle + "; \n\n\t");

                ObjectNode objectNode = (ObjectNode) event;
                objectNode.put("elementName", nameEle);
                System.out.println("event from POM Creator:" + event.toString());

                listOfWebElement.put(nameEle, event);
            } else {
                ObjectNode objectNode = (ObjectNode) event;
                objectNode.put("elementName", nameEle);
                System.out.println("event from POM Creator:" + event.toString());

            }

        }

        updateTemplate(template, "//Variable declarations", variables.toString());

        // Operations
        StringBuffer operations = new StringBuffer("//Operations \n");

        processOperation(operations, template, page, pageTitle);

        updateTemplate(template, "//Operations", operations.toString());

        return template.toString();
    }

    private static boolean checkFunctionExists(StringBuffer operations, String function) {
        return (operations.toString().contains(function));
    }

    private static void operateExistingElement(String xPath, JsonNode event, StringBuilder template) {
        String startingOfElement = template.substring(template.indexOf(xPath));
        String elementDetails = startingOfElement.substring(0, startingOfElement.indexOf(";"));
        String val = "private WebElement ";
        String nameEle = elementDetails.substring(elementDetails.indexOf(val) + val.length());
        ObjectNode objectNode = (ObjectNode) event;
        objectNode.put("elementName", nameEle);
    }

    public static boolean isElementAlreadyProcessed(String elementName) {
        for (String element : listOfWebElement.keySet()) {
            if (element.equalsIgnoreCase(elementName)) {
                return true;
            }
        }
        return false;
    }

    public static String getXPath(JsonNode event) {
        JsonNode eventData = event.path("data");
        JsonNode dataData = eventData.path("data");
        String command = eventData.path("cmd").asText();
        if (command.equalsIgnoreCase("expect")) {
            ArrayNode node = (ArrayNode) dataData.path("params");
            return node.get(0).asText();
        } else {
            return dataData.path("path").asText();
        }
    }
}
