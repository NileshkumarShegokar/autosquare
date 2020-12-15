/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.myriad.auto2.engine.writer;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;



/**
 *
 * @author nileshkumar_shegokar
 */
public class WebPage {

	
	private String pageUrl;

	private String title;

	public String getPageUrl() {
		return pageUrl;
	}

	public void setPageUrl(String pageUrl) {
		this.pageUrl = pageUrl;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public List<JsonNode> JsonNodes = new ArrayList<JsonNode>();

	public WebPage(String pageName, List<JsonNode> JsonNodes) {
		this.title = pageName;
		this.JsonNodes = JsonNodes;
	}

	public WebPage() {
	}


	public List<JsonNode> getJsonNodes() {
		return JsonNodes;
	}

	public void setJsonNodes(List<JsonNode> JsonNodes) {
		this.JsonNodes = JsonNodes;
	}

	public void addElement(JsonNode element) {
		this.JsonNodes.add(element);
	}

}
