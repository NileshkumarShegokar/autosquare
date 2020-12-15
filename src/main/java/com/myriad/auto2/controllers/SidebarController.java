package com.myriad.auto2.controllers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.myriad.auto2.engine.ControllerStore;
import com.myriad.auto2.engine.util.Validator;
import com.myriad.auto2.model.Project;
import com.myriad.auto2.model.TestCase;
import com.myriad.auto2.utility.ProjectUtility;
import com.myriad.auto2.utility.Resource;
import de.jensd.fx.glyphs.GlyphsDude;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconName;
import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import org.controlsfx.control.Notifications;

public class SidebarController implements Initializable {

    @FXML
    private VBox sidebar;

    private static Node searchNode = null;

    @FXML
    private HBox newProject;

    @FXML
    private HBox newTest;

    @FXML
    private HBox export;

    @FXML
    private HBox save;
    @FXML
    private HBox open;
    @FXML
    private HBox closeTest;
    @FXML
    private HBox closeProject;
    @FXML
    private HBox settings;

    @FXML
    private HBox support;
    @FXML
    private HBox social;

    // create a popup 
    private Popup popup = new Popup();

    static Logger log = LogManager.getLogger(SidebarController.class.getName());

    public SidebarController() {
        try {
            FXMLLoader loader = new FXMLLoader();
            Node component = (Node) loader.load(SidebarController.this.getClass().getResource("/fxml/settings.fxml"));
            SettingsController controller = loader.getController();
            ControllerStore.addController("settingsController", controller);
            component.setStyle("-fx-background-color:white;-fx-border-color: black;-fx-border-width:2;-fx-border-radius:3;-fx-hgap:3;-fx-vgap:5;");
            popup.getContent().add((Parent) component);
        } catch (IOException ex) {
            ex.printStackTrace();
            log.error(ex.getMessage(),ex);
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        addEvents();
    }

    public void closeSettings() {
      
                if (popup.isShowing()) {
                    popup.hide();
                }
    }

    private void addEvents() {
        newProject.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                Project project = new Project("New Project");

                Boolean status = Resource.addProject(project);
                if (!status) {
                    Notifications.create()
                            .title("Error")
                            .text("Error Creating New Project!")
                            .showError();
                    return;
                }
                log.info("New project created");
                Parent parent = ((Node) event.getSource()).getScene().getRoot();
                TabPane tabPanel = (TabPane) getAllNodes("tabPane", parent);
                TreeView tree = (TreeView) getAllNodes("tree", parent);

                TreeItem<String> item = new TreeItem<String>(project.getName(), GlyphsDude.createIcon(FontAwesomeIconName.TASKS));
                tree.getRoot().getChildren().add(item);
                Tab tab = new Tab();
                tab.setGraphic(GlyphsDude.createIcon(FontAwesomeIconName.TASKS));
                tab.setClosable(true);
                tab.setText(project.getName());
                try {
                    FXMLLoader loader = new FXMLLoader();
                    Node component = (Node) loader.load(SidebarController.this.getClass().getResource("/fxml/Project.fxml"));
                    ProjectController controller = loader.getController();
                    ControllerStore.addController("projectController", controller);
                    tab.setContent(component);
                    tabPanel.getTabs().add(tab);
                } catch (IOException ex) {
                    ex.printStackTrace();
                    log.error(ex.getMessage(),ex);
                }

            }
        });
        newTest.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {

                Parent parent = ((Node) event.getSource()).getScene().getRoot();
                TabPane tabPanel = (TabPane) getAllNodes("tabPane", parent);
                TreeView tree = (TreeView) getAllNodes("tree", parent);
                TreeItem selectedItem = (TreeItem) tree.getSelectionModel().getSelectedItem();
                if (selectedItem == null || Resource.getProject((String) selectedItem.getValue()) == null) {
                    Notifications.create()
                            .title("Error")
                            .text("Select the Project in which you want to create new test case!")
                            .showError();
                    return;
                }
                TextInputDialog dialog = new TextInputDialog("New Test Case Name");
                dialog.setHeaderText(null);
                dialog.setWidth(400);
                dialog.setGraphic(GlyphsDude.createIcon(FontAwesomeIconName.EDIT));
                dialog.setTitle("New Test Case");
                // dialog.setHeaderText("Enter your name:");
                dialog.setContentText("Name:");

                Optional<String> result = dialog.showAndWait();
                if (!result.isPresent()) {

                    return;
                }

                String testName = result.get();
                if (!Validator.validateTestCaseName(testName)) {
                    Notifications.create()
                            .title("Error")
                            .text("Error in Creating new Test Case! Test case name validation failed.")
                            .showError();
                    return;
                }
                Project project = Resource.getProject((String) selectedItem.getValue());
                TestCase testCase = new TestCase(testName);
                if (!project.addTestCase(testCase)) {
                    Notifications.create()
                            .title("Error")
                            .text("Error Creating New Test Case!")
                            .showError();
                    return;
                }
                log.info("creating new test case :"+testName);
                TreeItem<String> item = new TreeItem<>(testCase.getName(), GlyphsDude.createIcon(FontAwesomeIconName.EDIT));
                selectedItem.getChildren().add(item);
                selectedItem.setExpanded(true);
                Tab tab = new Tab();
                tab.setGraphic(GlyphsDude.createIcon(FontAwesomeIconName.EDIT));
                tab.setClosable(true);
                tab.setText(testCase.getName());
                try {

                    FXMLLoader loader = new FXMLLoader(
                            getClass().getResource(
                                    "/fxml/TestCase.fxml"
                            )
                    );

                    Node component = (Node) loader.load();
                    TestCaseController controller
                            = loader.<TestCaseController>getController();
                    ControllerStore.addController("testController", controller);
                    testCase.setLocation(project.getHomeURL());
                    controller.initTest(testCase);
                    tab.setContent(component);
                    tabPanel.getTabs().add(tab);
                    tabPanel.getSelectionModel().select(tab);
                } catch (IOException ex) {
                    ex.printStackTrace();
                    log.error(ex.getMessage(),ex);
                }

            }
        });

        save.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                log.info("saving project");
                Parent parent = ((Node) event.getSource()).getScene().getRoot();
                TreeView tree = (TreeView) getAllNodes("tree", parent);
                TreeItem selectedItem = (TreeItem) tree.getSelectionModel().getSelectedItem();
                if (selectedItem == null) {
                    Notifications.create()
                            .title("Error")
                            .text("Select the Project you want to save!")
                            .showError();
                    return;
                }
                String selectedProjectName = (String) selectedItem.getValue();
                if (selectedProjectName.equalsIgnoreCase("All Projects")) {
                    for (Project project : Resource.getAllProjects()) {
                        ProjectUtility.saveProject(project);
                    }
                }
                Project project = Resource.getProject(selectedProjectName);

                if (project == null) {
                    Notifications.create()
                            .title("Error")
                            .text("Select the Project you want to save")
                            .showError();
                    return;
                } else {
                    //save the selected project
                    ProjectUtility.saveProject(project);
                    Notifications.create()
                            .title("Success")
                            .text("Project saved successfully!")
                            .showInformation();
                }
            }
        });

        closeTest.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                log.info("closing test");
                Parent parent = ((Node) event.getSource()).getScene().getRoot();
                TreeView tree = (TreeView) getAllNodes("tree", parent);
                TreeItem selectedItem = (TreeItem) tree.getSelectionModel().getSelectedItem();
                if (selectedItem == null) {
                    Notifications.create()
                            .title("Error")
                            .text("Select the Test case you want to remove!")
                            .showError();
                    return;
                }
                TestCase testCase = Resource.getTestCase((String) selectedItem.getValue());
                if (testCase == null) {
                    Notifications.create()
                            .title("Error")
                            .text("Select a Test Case to Close!")
                            .showError();
                    return;
                }
                TreeItem projectNode = selectedItem.getParent();
                Project project = Resource.getProject((String) projectNode.getValue());
                //close tab if open
                Resource.deleteTestCase(parent, project, testCase);
                if (project != null) {
                    project.removeTestCase(testCase);
                }
                Resource.updateTree();
                Notifications.create()
                        .title("Success")
                        .text("Test case removed successfully!")
                        .showWarning();
            }
        });

        open.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                log.info("opening project");
                Window window = ((Node) event.getSource()).getScene().getWindow();
                ProjectUtility.openProject(window);
                ///
                Resource.updateTree();

            }
        });

        closeProject.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                log.info("closing project");
                Parent parent = ((Node) event.getSource()).getScene().getRoot();
                TreeView tree = (TreeView) getAllNodes("tree", parent);
                TreeItem selectedItem = (TreeItem) tree.getSelectionModel().getSelectedItem();
                if (selectedItem == null) {
                    Notifications.create()
                            .title("Error")
                            .text("Select the Project you want to remove!")
                            .showError();
                    return;
                }
                Project project = Resource.getProject((String) selectedItem.getValue());
                if (project != null) {
                    Resource.deleteProject(parent, project);
                    Resource.updateTree();
                }
                Notifications.create()
                        .title("Success")
                        .text("Project removed successfully!")
                        .showWarning();
            }

        });

        export.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                log.info("before exporting save All project");
                for (Project project : Resource.getAllProjects()) {
                    ProjectUtility.saveProject(project);
                }
                log.info("export requested");
                Parent parent = ((Node) event.getSource()).getScene().getRoot();
                Window currentStage = ((Node) event.getSource()).getScene().getWindow();
                TreeView tree = (TreeView) getAllNodes("tree", parent);
                TreeItem selectedItem = (TreeItem) tree.getSelectionModel().getSelectedItem();
                if (selectedItem == null || Resource.getProject((String) selectedItem.getValue()) == null) {
                    Notifications.create()
                            .title("Error")
                            .text("Select the Project in which you want to export!")
                            .showError();
                    return;
                }
                try {
                    FXMLLoader loader = new FXMLLoader();
                    Node component = (Node) loader.load(SidebarController.this.getClass().getResource("/fxml/Export.fxml"));
                    ExportController controller = loader.getController();
                    ControllerStore.addController("exportController", controller);
                    StackPane secondaryLayout = new StackPane();
                    secondaryLayout.getChildren().add(component);
                    // New window (Stage)
                    Stage newWindow = new Stage();
                    newWindow.setTitle("Second Stage");
                    newWindow.setScene(new Scene(secondaryLayout));

                    // Specifies the modality for new window.
                    newWindow.initModality(Modality.APPLICATION_MODAL);

                    // Specifies the owner Window (parent) for new window
                    newWindow.initOwner(currentStage);
                    newWindow.initStyle(StageStyle.UNDECORATED);

                    newWindow.show();
                } catch (IOException ex) {
                    ex.printStackTrace();
                    log.error(ex.getMessage(),ex);
                }

            }
        });

        settings.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                Window stage = ((Node) event.getSource()).getScene().getWindow();

                if (!popup.isShowing()) {
                    popup.show(stage);
                } else {
                    popup.hide();
                }
            }

        });
    }

    public static Node getAllNodes(String name, Parent root) {
        searchNode = null;
        return addAllDescendents(name, root);

    }

    private static Node addAllDescendents(String name, Parent parent) {

        for (Node node : parent.getChildrenUnmodifiable()) {
            String id = node.getId();
            if (id != null && id.equalsIgnoreCase(name)) {
                searchNode = node;
                break;
            }
            if (searchNode == null && node instanceof Parent) {
                addAllDescendents(name, (Parent) node);
            }
        }
        return searchNode;
    }

}
