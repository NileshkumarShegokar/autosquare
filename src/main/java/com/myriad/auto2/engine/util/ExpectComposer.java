package com.myriad.auto2.engine.util;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class ExpectComposer {

	public static String compose(JsonNode event,String object, String function) {
		String condition = "";
		JsonNode data = event.path("data");
		JsonNode dataData = data.path("data");
		String command = data.path("cmd").asText();
		if (!command.equalsIgnoreCase("expect")) {
			return null;
		}
		String type = dataData.path("type").asText();
		String param = dataData.path("params").asText();
		String compare = dataData.path("compare").asText();
		String to = dataData.path("to").asText();
		//equal,notEqual,contain,notContain,above,below,match,notMatch,
		//val,text,displayed,enabled,selected,attr,css,url,title,cookie,localStorage,sessionStorage,alert,jscode,count,imgdiff,
		if(compare.equalsIgnoreCase("equal")) {
			condition="Assert.assertEquals("+object+"."+function.trim()+"(),\""+to+"\");\n";
		}else if(compare.equalsIgnoreCase("notEqual")) {
			condition="Assert.assertNotEquals("+object+"."+function.trim()+"(),\""+to+"\");\n";
		}else if(compare.equalsIgnoreCase("contain")) {
			condition="Assert.assertTrue("+object+"."+function.trim()+"().contains(\""+to+"\"));\n";
		}else if(compare.equalsIgnoreCase("notContain")) {
			condition="Assert.assertTrue(!"+object+"."+function.trim()+"().contains(\""+to+"\"));\n";
		}
		
		return condition;
	}

	public static Object compose(JsonNode event,String selector) {
		String condition = "";
		JsonNode data = event.path("data");
		JsonNode dataData = data.path("data");
		String command = data.path("cmd").asText();
		String type = dataData.path("type").asText();
		String param = dataData.path("params").asText();
		String compare = dataData.path("compare").asText();
		String to = dataData.path("to").asText();
		//equal,notEqual,contain,notContain,above,below,match,notMatch,
		//val,text,displayed,enabled,selected,attr,css,url,title,cookie,localStorage,sessionStorage,alert,jscode,count,imgdiff,
		String value="driver.findElement(" + selector + ")";
		if(compare.equalsIgnoreCase("equal")) {			
			condition="Assert.assertEquals("+value+"."+createFunction(event)+",\""+to+"\");\n";
		}else if(compare.equalsIgnoreCase("notEqual")) {
			condition="Assert.assertNotEquals("+value+"."+createFunction(event)+",\""+to+"\");\n";
		}else if(compare.equalsIgnoreCase("contain")) {
			condition="Assert.assertTrue("+value+"."+createFunction(event)+",\""+to+"\");\n";
		}else if(compare.equalsIgnoreCase("notContain")) {
			condition="Assert.assertTrue(!"+value+"."+createFunction(event)+",\""+to+"\");\n";
		}
		
		return condition;
	}
	
	private static String createFunction(JsonNode element) {
		JsonNode eventData = element.path("data");
		JsonNode dataData = eventData.path("data");
		String to = dataData.path("to").asText();
		String type = dataData.path("type").asText();
		String function="";
		switch (type) {
		case "val": {
			function="getAttribute(\"value\")";
			break;
		}
		case "text": {
			function="getText()";
			break;
		}
		case "displayed": {
			function="isDisplayed()";
			break;
		}
		case "enabled": {
			function="isEnabled()";
			break;
		}
		case "selected": {
			function="isSelected()";
			break;
		}
		case "attr": {
			function="getAttribute(" + to + ")";
			break;
		}
		case "css": {
			function="getAttribute(\"class\").contains("+to+")";
			break;
		}
		case "url": {
			function="driver.getCurrentUrl().equalsIgnoreCase(\"" + to + "\")";
			break;
		}
		case "title": {
			function="driver.getTitle().equalsIgnoreCase(\"" + to + "\")";
			break;
		}
		case "cookie": {

			function="driver.manage().getCookieNamed(\"" + to + "\")";
			break;
		}
		case "localStorage": {
			function="((WebStorage) driver.getLocalStorage()).getItem(\"" + to + "\")";
			break;
		}
		case "sessionStorage": {
			function="((WebStorage) driver.getSessionStorage()).getItem(\"" + to + "\")";
			break;
		}
		case "alert": {
			function="driver.switchTo().alert()";
			break;
		}
		}
		return function;
	}

}
