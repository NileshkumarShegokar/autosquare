/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.myriad.auto2.controllers;


import com.fasterxml.jackson.databind.JsonNode;
import com.myriad.auto2.model.Project;
import com.myriad.auto2.model.TestCase;
import com.myriad.auto2.utility.ProjectUtility;
import com.myriad.auto2.utility.Resource;
import java.io.File;
import java.net.URL;
import java.util.Iterator;
import java.util.ResourceBundle;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import org.controlsfx.control.Notifications;

/**
 * FXML Controller class
 *
 * @author nshegoka
 */
public class ProjectController implements Initializable {

    @FXML
    public TextField projectName;

    @FXML
    public TextField location;

    @FXML
    public TextField saveTo;

    @FXML
    public Button browse;
    @FXML
    public Button save;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        browse.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                Window window = ((Node) event.getSource()).getScene().getWindow();
                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Save file");
                fileChooser.setInitialFileName(projectName.getText());
                File file = fileChooser.showSaveDialog(window);
                if (file == null) {
                    Notifications.create()
                            .title("Error")
                            .text("Please select the location to save the project")
                            .showError();
                    return;
                } else {
                    saveTo.setText(file.getAbsolutePath());
                }
            }

        });
        save.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {

                Parent parent = ((Node) event.getSource()).getScene().getRoot();
                TabPane tabPanel = (TabPane) SidebarController.getAllNodes("tabPane", parent);
                Tab tab = tabPanel.getSelectionModel().getSelectedItem();
                String existingProjName = tab.getText();
                Project project = Resource.getProject(existingProjName);
                String loc = location.getText();
                String saveLocation = saveTo.getText();
                String projName = projectName.getText();
                project.setLocation(saveLocation);
                project.setHomeURL(loc);
                project.setName(projName);
                tab.setText(projName);
                Resource.updateTree();

                if (projName.isEmpty() || loc.isEmpty() || saveLocation.isEmpty()) {
                    Notifications.create()
                            .title("Error")
                            .text("Please set all mendatory fields!")
                            .showError();
                    return;
                }

                ProjectUtility.saveProject(project);
                Notifications.create()
                            .title("Success")
                            .text("Project Saved Successfuly!")
                            .showInformation();

            }
        });
    }
    public void initProject(Project project) {
        projectName.setText(project.getName());
        location.setText(project.getHomeURL());
        saveTo.setText(project.getLocation());
    }
}
