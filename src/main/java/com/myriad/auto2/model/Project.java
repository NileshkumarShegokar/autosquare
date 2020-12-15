package com.myriad.auto2.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class Project implements Serializable {

    private String name;

    private String description;

    private String homeURL;

    private String location;

    public Project() {
    }

    public Project(String projectName) {
        this.name = projectName;
    }

    public Project(String projectName, String homeURL, String location, String description) {
        this.name = projectName;
        this.homeURL = homeURL;
        this.location = location;
        this.description = description;
    }

    public Project(String projectName, String homeURL, String location) {
        this.name = projectName;
        this.homeURL = homeURL;
        this.location = location;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    private Set<TestCase> cases = new HashSet<TestCase>();

    public boolean addTestCase(TestCase testCase) {
        if (cases == null) {
            cases = new HashSet<TestCase>();
        }
        return cases.add(testCase);
    }

    public void removeTestCase(TestCase testCase) {
        cases.remove(testCase);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getHomeURL() {
        return homeURL;
    }

    public void setHomeURL(String homeURL) {
        this.homeURL = homeURL;
    }

    public Set<TestCase> getCases() {
        return cases;
    }

    public void setCases(HashSet<TestCase> cases) {
        this.cases = cases;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + Objects.hashCode(this.name);
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
        final Project other = (Project) obj;
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        return true;
    }

}
