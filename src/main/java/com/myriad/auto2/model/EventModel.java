/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.myriad.auto2.model;

import javafx.beans.property.SimpleStringProperty;

import java.io.Serializable;

/**
 *
 * @author nshegoka
 */
public class EventModel implements Serializable {
    
    private final SimpleStringProperty command;
    private final SimpleStringProperty url;
    private final SimpleStringProperty path;
    private final SimpleStringProperty value;
    private final SimpleStringProperty fixed;

    public EventModel(String command, String url, String path, String value, String fixed) {
        this.command = new SimpleStringProperty(command);
        this.url = new SimpleStringProperty(url);
        this.path = new SimpleStringProperty(path);
        this.value = new SimpleStringProperty(value);
        this.fixed = new SimpleStringProperty(fixed);
    }
    
    
    public String getCommand() {
        return command.get();
    }

    public void setCommand(String command) {
        this.command.set(command);
    }

    public String getUrl() {
        return url.get();
    }

    public void setUrl(String url) {
        this.url.set(url);
    }

    public String getPath() {
        return path.get();
    }

    public void setPath(String path) {
        this.path.set(path);
    }

    public String getValue() {
        return value.get();
    }

    public void setValue(String value) {
        this.value.set(value);
    }

    public String getFixed() {
        return fixed.get();
    }

    public void setFixed(String fixed) {
        this.fixed .set(fixed);
    }
    
    
            
}
