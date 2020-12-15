package com.myriad.auto2.engine.writer;

import java.io.IOException;
import java.util.LinkedHashMap;
import com.fasterxml.jackson.databind.JsonNode;
import java.util.List;

/**
 *
 * @author nileshkumar_shegokar
 */
public class PomCreator {

	private LinkedHashMap<String, WebPage> webPages = new LinkedHashMap<String, WebPage>();

	public void analyseWebElements(List<JsonNode> events) {
		// iterate over list of events for given test case
		for (JsonNode event : events) {
			JsonNode data = event.path("data");
			WebPage page = webPages.get(data.path("title").asText());
			String cmd=data.path("cmd").asText();
			if (!cmd.equalsIgnoreCase("url") && !cmd.equalsIgnoreCase("waitBody")&& !cmd.equalsIgnoreCase("end"))
				addElementToPage(page, event);
		}
	}

	private void addElementToPage(WebPage webPage, JsonNode event) {
		JsonNode data = event.path("data");
		String title = data.path("title").asText();
		String pageAddress = data.path("pageAddress").asText();
		
		if (webPage == null) {
			webPage = new WebPage();
			webPage.setTitle(title);
			webPage.setPageUrl(pageAddress);
		}
		webPage.addElement(event);
		webPages.put(title, webPage);
	}

	public String createPom(String packageName, String pageName, int timeout) throws IOException, Exception {
		WebPage webPage = webPages.get(pageName);
		return HoneyCombPomCreator.createPom(packageName, webPage.getTitle(), webPage.getPageUrl(), "HoneyComb Agnes",
				pageName, webPages, timeout, "Page Loaded Text");
	}

	public String updateExistingPOM(String packageName, String pageName, String template) {
		WebPage webPage = webPages.get(pageName);
		return HoneyCombPomCreator.updatePOM(packageName, webPage.getTitle(), pageName, webPages, new StringBuilder(template));
	}

	public LinkedHashMap<String, WebPage> getWebPages() {
		return webPages;
	}

	public void setWebPages(LinkedHashMap<String, WebPage> webPages) {
		this.webPages = webPages;
	}

}
