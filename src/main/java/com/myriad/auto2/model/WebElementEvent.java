package com.myriad.auto2.model;

import java.io.Serializable;


public class WebElementEvent implements Serializable {

    private String xPath;
    private String event;
    private String value;
    private String pattern;
    private String type = "N/A";
    private Boolean fixed = true;
    private String elementType;
    private String element;
    private String pageAddress;
    private String pageURL;
    private String title;
    private String outerHTML;
    private String pageName;
    private String pageUrl;
    private String elementName;
    private String function;
    private String angularText;
    
    private String domName;
    private String domId ;
    private String domTitle;
    private String domText;
    private String domTag;
    private String domType;



    public String getDomType() {
		return domType;
	}

	public void setDomType(String domType) {
		this.domType = domType;
	}

	public String getDomTag() {
		return domTag;
	}

	public void setDomTag(String domTag) {
		this.domTag = domTag;
	}

	public String getDomName() {
		return domName;
	}

	public void setDomName(String domName) {
		this.domName = domName;
	}

	public String getDomId() {
		return domId;
	}

	public void setDomId(String domId) {
		this.domId = domId;
	}

	public String getDomTitle() {
		return domTitle;
	}

	public void setDomTitle(String domTitle) {
		this.domTitle = domTitle;
	}

	public String getDomText() {
		return domText;
	}

	public void setDomText(String domText) {
		this.domText = domText;
	}

	public String getAngularText() {
        return angularText;
    }

    public void setAngularText(String angularText) {
        this.angularText = angularText;
    }

    public String getPageName() {
        return pageName;
    }

    public void setPageName(String pageName) {
        this.pageName = pageName;
    }

    public String getPageUrl() {
        return pageUrl;
    }

    public void setPageUrl(String pageUrl) {
        this.pageUrl = pageUrl;
    }

    public WebElementEvent(String elementName, String function) {
        this.elementName = elementName;
        this.function = function;
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

   

    public WebElementEvent() {
    }

    

    public WebElementEvent(String elementName) {
        this.elementName = elementName;
    }

    public String getOuterHTML() {
        return outerHTML;
    }

    public void setOuterHTML(String outerHTML) {
        this.outerHTML = outerHTML;
    }

    public String getPageURL() {
        return pageURL;
    }

    public void setPageURL(String pageURL) {
        this.pageURL = pageURL;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public WebElementEvent(String xPath, String event, String value, String type, boolean fixed, String pattern) {
        this.xPath = xPath;
        this.event = event;
        this.value = value;
        this.type = type;
        this.fixed = fixed;
        this.pattern = pattern;
    }

    public String getElement() {
        return element;
    }

    public void setElement(String element) {
        this.element = element;
    }

    public String getPageAddress() {
        return pageAddress;
    }

    public void setPageAddress(String pageAddress) {
        this.pageAddress = pageAddress;
    }

    public String getElementType() {
        return elementType;
    }

    public void setElementType(String elementType) {
        this.elementType = elementType;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Boolean getFixed() {
        return fixed;
    }

    public void setFixed(Boolean fixed) {
        this.fixed = fixed;
    }

    public String getPattern() {
        return pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    public String getxPath() {
        return xPath;
    }

    public void setxPath(String xPath) {
        this.xPath = xPath;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
        if (pattern == null) {
            this.pattern = value;
        }
    }

    @Override
    public String toString() {
        return "WebElementEvent{" + "xPath=" + xPath + ", event=" + event + ", value=" + value + ", pattern=" + pattern + ", type=" + type + ", fixed=" + fixed + ", elementType=" + elementType + ", element=" + element + ", pageAddress=" + pageAddress + ", pageURL=" + pageURL + ", title=" + title + ", outerHTML=" + outerHTML + ", pageName=" + pageName + ", pageUrl=" + pageUrl + ", elementName=" + elementName + ", function=" + function + ",  angularText=" + angularText + '}';
    }

}
