/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.myriad.auto2.utility;


import com.myriad.auto2.MainApp;
import com.myriad.auto2.controllers.SidebarController;
import com.myriad.auto2.model.Project;
import com.myriad.auto2.model.TestCase;
import de.jensd.fx.glyphs.GlyphsDude;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconName;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import javafx.scene.Parent;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;

/**
 *
 * @author nshegoka
 */
public class Resource {

    public static Set<Project> projects = new HashSet<Project>();

    public static Set<Project> getAllProjects() {
        return projects;
    }

    public static boolean addProject(Project project) {
        return projects.add(project);
    }

    public static void updateTree() {
        Parent parent = MainApp.stage.getScene().getRoot();
        TreeView tree = (TreeView) SidebarController.getAllNodes("tree", parent);
        tree.getRoot().getChildren().clear();
        for (Project project : projects) {
            TreeItem<String> projItem = new TreeItem<String>(project.getName(), GlyphsDude.createIcon(FontAwesomeIconName.TASKS));
            if(project.getCases()!=null)
            for (TestCase tCase : project.getCases()) {
                TreeItem<String> caseItem = new TreeItem<String>(tCase.getName(), GlyphsDude.createIcon(FontAwesomeIconName.EDIT));
                projItem.getChildren().add(caseItem);
            }
            tree.getRoot().getChildren().add(projItem);
        }
    }

    public static void deleteProject(Parent parent,Project project) {
        //delete all the test cases of the project before deleting it
        for(TestCase testCase:project.getCases()){
            deleteTestCase(parent,project,testCase);
        }
        //as all testacase tabs closed remove the project and close the project tab
        projects.remove(project);
        TabPane tabPanel = (TabPane) SidebarController.getAllNodes("tabPane", parent);
        Iterator<Tab> tabs = tabPanel.getTabs().iterator();
        while (tabs.hasNext()) {
            Tab tab=tabs.next();
            if (tab.getText().equalsIgnoreCase(project.getName())) {
                tabs.remove();
            }
        }
    }
    
    public static void deleteTestCase(Parent parent,Project project, TestCase test) {
        TabPane tabPanel = (TabPane) SidebarController.getAllNodes("tabPane", parent);
        Iterator<Tab> tabs = tabPanel.getTabs().iterator();
        while (tabs.hasNext()) {
            Tab tab=tabs.next();
            if (tab.getText().equalsIgnoreCase(test.getName())) {
                tabs.remove();
            }
        }
        project.removeTestCase(test);
    }

    public static Project getProject(String projectName) {
        for (Project project : projects) {
            if (project.getName().equalsIgnoreCase(projectName)) {
                return project;
            }
        }
        return null;
    }

    public static TestCase getTestCase(String caseName) {
        for (Project project : projects) {
            for (TestCase tCase : project.getCases()) {
                if (tCase.getName().equalsIgnoreCase(caseName)) {
                    return tCase;
                }
            }
        }
        return null;
    }

    

}
