package com.myriad.auto2.model;

import java.io.Serializable;
import java.util.ArrayList;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

public class TestCase implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private String name;

    private String location;

    private ArrayList<JsonNode> events = new ArrayList<JsonNode>();

    private String encoding;

    public TestCase() {
    }
    
    public boolean addEvent(JsonNode event){
        return events.add(event);
    }

    public String getEncoding() {
        return encoding;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public void setEvents(ArrayList<JsonNode> events) {
        this.events = events;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public TestCase(String testCaseName) {
        this.name = testCaseName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<JsonNode> getEvents() {
        return events;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 67 * hash + Objects.hashCode(this.name);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final TestCase other = (TestCase) obj;
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        return true;
    }

}
