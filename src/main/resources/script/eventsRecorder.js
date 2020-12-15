var activeElement = "";
var activeEvent = "";
var value = "";
var tag = "";
var tag_type = "";
var angular_name = "";
var element_index=1;
window.eventsRecorder = {};
window.eventsRecorder.detection = {};
function identifyElement(element){
    console.log("Element",element);
    
    if(element != null && element != ""){
        var _id=element.id;
        var _name=element.getAttribute("name");
        var _text = element.innerText || element.textContent;
        
        if (_id == "" && _name == null && _text == ""){                        
            return identifyElement(element.parentElement);
        }else{
            console.log(_id+" "+_name+" "+_text);
            if(_id != null && _id != ""){
                console.log("Returning ID:",_id);
                return _id;
            }else if(_name != null && _name != ""){
                console.log("Returning Name:",_name);
                return _name;
            }else if(_text != null && _text != ""){
                if(_text.length >20){
                    _text= "webElement"+(element_index++);
                }
                console.log("Returning Text:",_text);
                return _text;
            }
        }                        
    }
}
window.eventsRecorder.notifyJavaCode = function () {
    
    var xhr = new XMLHttpRequest();
    var xp = createXPathFromElement(activeElement);
    var x_path = xp.replace(/"/g, "'");    
    var page_address = window.location.pathname;
    var title = document.title;
    var url = window.location.href;
    var outerHTML="";
    var angular_text= "";     
    if (activeElement!=null && activeElement != "")
    	{
    		angular_text=identifyElement(activeElement);
    		outerHTML=activeElement.outerHTML.replace(/"/g, "'");    
    	}
    var data = '{"xPath": "' + x_path + '","event": "' + activeEvent + '","value": "' + value + '","element": "' + tag + '","elementType": "' + tag_type + '","pageAddress": "' + page_address + '","title": "' + title + '","pageURL": "' + url + '","outerHTML": "' + outerHTML + '","angularText": "' + angular_text + '"}';
    console.log("Request :"+data);
    xhr.open("POST", "/honeycomb-agnes");
    xhr.send(data);
console.log("Loagging Text :"+angular_text);
    activeElement = "";
    activeEvent = "";
    value = "";
    tag = "";
    tag_type = "";

};
// eventsRecorder is a global object that provides:
// detectionObjects is an array of objects as descibed in DetectableWebDriverAction in Java code
// "e" is the event
// convertParametersToQueryString is a function that converts a map
// to a url query string
window.eventsRecorder.domainSpecificEventHandler = function (e) {    
    var detectionObjects = eventsRecorder.detection[e.type];
    for (var i = 0; i < detectionObjects.length; i++) {
        if (detectionObjects[i].isDetected(e)) {
            eventsRecorder.notifyJavaCode();
        }
    }
};

window.eventsRecorder.detection['click'] = [
    {
        
        name: 'click',
        isDetected: function (e) {
                
                var element = e.srcElement;
                console.log("click detected :",element.tagName);
                if(element.tagName == "BUTTON" || element.tagName == "P" ||element.tagName == "A" || (element.tagName.toLowerCase() == "input" && element.getAttribute("type").toLowerCase() == "submit") ){
                    tag = element.tagName;    
                    tag_type = element.getAttribute("type");
                    activeElement = element;
                    activeEvent = e.type;                
                    return true;
                }else{
                    return false;
                }
        }
    }
];
window.eventsRecorder.detection['change'] = [
    {
        name: 'change',
        isDetected: function (e) {
            if (e.srcElement == activeElement && e.type == activeEvent) {
                return false;
            } else {
                var element = e.srcElement;
                tag = element.tagName;
                tag_type = element.getAttribute("type");
                activeElement = element;
                activeEvent = updateEvent(tag,tag_type);
                if (element.tag == "select") {
                    value = element.options[element.selectedIndex].text;
                } else if(tag_type == "file"){
                    var actual=element.value;
                    var regex = /\\/g;
                    value = actual.replace(regex, "\\\\");                    
                }else {
                    value = element.value;
                }
                
                return true;
            }
        }
    }
];
window.eventsRecorder.detection['keypress'] = [
    {
        name: 'keypress',
        isDetected: function (e) {
            if (e.shiftKey && e.key === "T") {
                var ele = window.getSelection().anchorNode.parentNode;
                activeElement = ele;
                activeEvent = "test";
                value = window.getSelection();
                tag_type = ele.getAttribute("type");
                tag = ele.tagName;
                return true;
            }
            if (e.shiftKey && e.key === "C") {
                activeElement = "";
                activeEvent = "screenshot";
                value = "";
                tag = "";
                tag_type = "";
                return true;
            }
        }
    }
];
window.eventsRecorder.detection['dblclick'] = [
    {
        name: 'dblclick',
        isDetected: function (e) {
            if (e.srcElement == activeElement && e.type == activeEvent) {
                console.log("Same Control detected");
                return false;
            } else {
                var element = e.srcElement;
                activeElement = element;
                tag = element.tagName;
                tag_type = element.getAttribute("type");
                activeEvent = e.type;
                return true;
            }
        }
    }
];

window.eventsRecorder.detection['onblur'] = [
    {
        name: 'onblur',
        isDetected: function (e) {
            if (e.srcElement == activeElement && e.type == activeEvent) {
                console.log("Same Control detected");
                return false;
            } else {
                var element = e.srcElement;
                activeElement = element;
                tag = element.tagName;
                tag_type = element.getAttribute("type");
                activeEvent = e.type;
                return true;
            }
        }
    }
];



Object
        .keys(window.eventsRecorder.detection)
        .forEach(eventType => window.addEventListener(eventType, window.eventsRecorder.domainSpecificEventHandler));

//add xpath finder
function createXPathFromElement(element)
{
    if (element && element.id)
        return '//*[@id="' + element.id + '"]';
    else
        return getElementTreeXPath(element);
};
function getElementTreeXPath(element)
{
    var paths = [];  // Use nodeName (instead of localName) 
    // so namespace prefix is included (if any).
    for (; element && element.nodeType == Node.ELEMENT_NODE; 
           element = element.parentNode)
    {
        var index = 0;
        var hasFollowingSiblings = false;
        for (var sibling = element.previousSibling; sibling; 
              sibling = sibling.previousSibling)
        {
            // Ignore document type declaration.
            if (sibling.nodeType == Node.DOCUMENT_TYPE_NODE)
                continue;

            if (sibling.nodeName == element.nodeName)
                ++index;
        }

        for (var sibling = element.nextSibling; 
            sibling && !hasFollowingSiblings;
            sibling = sibling.nextSibling)
        {
            if (sibling.nodeName == element.nodeName)
                hasFollowingSiblings = true;
        }

        var tagName = (element.prefix ? element.prefix + ":" : "") 
                          + element.localName;
        var pathIndex = (index || hasFollowingSiblings ? "[" 
                   + (index + 1) + "]" : "");
        paths.splice(0, 0, tagName + pathIndex);
    }

    return paths.length ? "/" + paths.join("/") : null;
};

/*
function createXPathFromElement(elm) {
	if(elm ==null || elm ==""){
		return "";
	}
    var allNodes = document.getElementsByTagName('*');
    for (var segs = []; elm && elm.nodeType == 1; elm = elm.parentNode) {
        if (elm.hasAttribute('id')) {
            var uniqueIdCount = 0;
            for (var n = 0; n < allNodes.length; n++) {
                if (allNodes[n].hasAttribute('id') && allNodes[n].id == elm.id)
                    uniqueIdCount++;
                if (uniqueIdCount > 1)
                    break;
            }
            ;
            if (uniqueIdCount == 1) {
                segs.unshift('id("' + elm.getAttribute('id') + '")');
                return segs.join('/');
            } else {
                segs.unshift(elm.localName.toLowerCase() + '[@id="' + elm.getAttribute('id') + '"]');
            }
        } else {
            for (i = 1, sib = elm.previousSibling; sib; sib = sib.previousSibling) {
                if (sib.localName == elm.localName)
                    i++;
            }
            ;
            segs.unshift(elm.localName.toLowerCase() + '[' + i + ']');
        }
        ;
    }
    ;
    return segs.length ? '/' + segs.join('/') : null;
}
;
*/
function lookupElementByXPath(path) {
    var evaluator = new XPathEvaluator();
    var result = evaluator.evaluate(path, document.documentElement, null, XPathResult.FIRST_ORDERED_NODE_TYPE, null);
    return result.singleNodeValue;
}

function updateEvent(tag,tag_type){
    var t_tag=tag.toLowerCase();
    if(t_tag == "button"){
        return "click";
    }else if(t_tag == "select"){
        return "select";
    }else if(t_tag == "input" || t_tag == "textarea"){
        if(tag_type != null && (tag_type == "submit" || tag_type== "reset" || tag_type== "radio" || tag_type== "checkbox")){
        return "click";
        }else{
        return "keypress";
        }        
    }
}
