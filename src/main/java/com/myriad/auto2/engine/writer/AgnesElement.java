/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.myriad.auto2.engine.writer;

import com.myriad.auto2.model.WebElementEvent;



/**
 *
 * @author nileshkumar_shegokar
 */
public class AgnesElement {

	private String elementName;
	private String function;
	private WebElementEvent event;
	private String DOMXPath;
	private String angularText;
	private WebPage webPage;
	
	

	public WebPage getWebPage() {
		return webPage;
	}

	public void setWebPage(WebPage webPage) {
		this.webPage = webPage;
	}

	public String getAngularText() {
		return angularText;
	}

	public void setAngularText(String angularText) {
		this.angularText = angularText;
	}

	public String getDOMXPath() {
		return DOMXPath;
	}

	public void setDOMXPath(String DOMXPath) {
		this.DOMXPath = DOMXPath;
	}

	public WebElementEvent getEvent() {
		return event;
	}

	public void setEvent(WebElementEvent event) {
		this.event = event;
	}

	public String getElementName() {
		return elementName;
	}

	public void setElementName(String elementName) {
		this.elementName = elementName;
	}

	public String getFunction() {
		return function;
	}

	public void setFunction(String function) {
		this.function = function;
	}

}
