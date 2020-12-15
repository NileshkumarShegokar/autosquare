/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.myriad.auto2.controllers;

import com.myriad.auto2.engine.ActiveCaseRecorder;
import com.myriad.auto2.engine.ControllerStore;
import com.myriad.auto2.model.Project;
import com.myriad.auto2.model.TestCase;
import com.myriad.auto2.utility.ProjectUtility;
import com.myriad.auto2.utility.Resource;
import com.myriad.auto2.utility.UIUtility;
import de.jensd.fx.glyphs.GlyphsDude;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconName;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 *
 * @author nshegoka
 */
public class MainController implements Initializable {

    @FXML
    private Label label;

    @FXML
    private BorderPane main_pane;

    @FXML
    public Button closeButton;

    @FXML
    public Button minimizeButton;

    @FXML
    private TreeView tree;

    @FXML
    private FlowPane flowPane;

    @FXML
    private TabPane tabPane;

    @FXML
    private ImageView icon;

    /**
     * Initializes the controller class.
     */
    boolean flag = false;

    static Logger log = LogManager.getLogger(MainController.class.getName());

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Sidebar.fxml"));
            Parent contentarea = loader.load();
            SidebarController controller = loader.getController();
            ControllerStore.addController("sidebarController", controller);
            main_pane.setLeft(contentarea);
            TreeItem<String> rootItem = new TreeItem<String>("All Projects", GlyphsDude.createIcon(FontAwesomeIconName.CUBES));
            rootItem.setExpanded(true);
            tree.setRoot(rootItem);
            flowPane.setAlignment(Pos.TOP_RIGHT);
            UIUtility.updateGraphics(closeButton, GlyphsDude.createIcon(FontAwesomeIconName.CLOSE), UIUtility.CSS.DANGER);
            UIUtility.updateGraphics(minimizeButton, GlyphsDude.createIcon(FontAwesomeIconName.MINUS), UIUtility.CSS.SUCCESS);
        } catch (IOException ex) {
            ex.printStackTrace();
            log.error(ex.getMessage(),ex);
        }

        icon.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                BorderPane border_pane = (BorderPane) ((Node) event.getSource()).getScene().getRoot();
                if (flag == true) {
                    try {
                        FXMLLoader loader = new FXMLLoader();
                        Parent sidebar = loader.load(getClass().getResource("/fxml/Sidebar.fxml"));
                        border_pane.setLeft(sidebar);
                        SidebarController controller = loader.getController();
                        ControllerStore.addController("sidebarController", controller);
                        flag = false;
                    } catch (IOException ex) {
                        ex.printStackTrace();
                        log.error(ex.getMessage(),ex);
                    }
                } else {
                    border_pane.setLeft(null);
                    flag = true;
                }
            }
        });

        tree.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if (mouseEvent.getClickCount() == 2) {
                    TreeItem<String> item = (TreeItem) tree.getSelectionModel().getSelectedItem();
                    for (Tab tab : tabPane.getTabs()) {
                        if (tab.getText().equalsIgnoreCase(item.getValue())) {
                            tabPane.getSelectionModel().select(tab);
                            return;
                        }
                    }
                    Project project = Resource.getProject(item.getValue());
                    if (project != null) {
                        Tab tab = new Tab();
                        tab.setGraphic(GlyphsDude.createIcon(FontAwesomeIconName.TASKS));
                        tab.setClosable(true);
                        tab.setText(project.getName());
                        try {
                            FXMLLoader loader = new FXMLLoader(
                                    getClass().getResource(
                                            "/fxml/Project.fxml"
                                    )
                            );

                            Node component = (Node) loader.load();
                            ProjectController controller
                                    = loader.<ProjectController>getController();
                            controller.initProject(project);
                            tab.setContent(component);
                            tabPane.getTabs().add(tab);
                            tabPane.getSelectionModel().select(tab);
                            return;
                        } catch (IOException ex) {
                            ex.printStackTrace();
                            log.error(ex.getMessage(),ex);
                        }
                    }
                    TestCase testCase = Resource.getTestCase(item.getValue());
                    if (testCase != null) {
                        try {
                            Tab tab = new Tab();
                            tab.setGraphic(GlyphsDude.createIcon(FontAwesomeIconName.EDIT));
                            tab.setClosable(true);
                            tab.setText(testCase.getName());

                            FXMLLoader loader = new FXMLLoader(
                                    getClass().getResource(
                                            "/fxml/TestCase.fxml"
                                    )
                            );

                            Node component = (Node) loader.load();
                            TestCaseController controller
                                    = loader.<TestCaseController>getController();
                            controller.initTest(testCase);
                            tab.setContent(component);
                            tabPane.getTabs().add(tab);
                            tabPane.getSelectionModel().select(tab);
                            return;
                        } catch (IOException ex) {
                            ex.printStackTrace();
                            log.error(ex.getMessage(),ex);
                        }
                    }

                }
            }
        });

    }

    @FXML
    private void close_window(ActionEvent event) {
        //save project state
        for (Project project : Resource.getAllProjects()) {
                        ProjectUtility.saveProject(project);
        }
        //close the stage
        Stage stage = (Stage) closeButton.getScene().getWindow();
        stage.close();
        Platform.exit();
    }

    @FXML
    private void minimize_window(ActionEvent event) throws IOException {
        Stage stage = (Stage) closeButton.getScene().getWindow();
        stage.setIconified(true);
    }

    @FXML
    private void maximize_window(ActionEvent event) throws IOException {
        Stage stage = (Stage) closeButton.getScene().getWindow();
        stage.setMaximized(true);
    }

}
