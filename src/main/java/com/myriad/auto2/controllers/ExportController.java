/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.myriad.auto2.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.googlejavaformat.java.FormatterException;
import com.myriad.auto2.edith.util.Language;
import com.myriad.auto2.engine.writer.*;
import com.myriad.auto2.model.Project;
import com.myriad.auto2.model.TestCase;
import com.myriad.auto2.utility.Resource;
import com.myriad.auto2.utility.UIUtility;
import de.jensd.fx.glyphs.GlyphsDude;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconName;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.controlsfx.control.Notifications;

import javax.swing.*;

/**
 * FXML Controller class
 *
 * @author nshegoka
 */
public class ExportController implements Initializable {

    static Logger log = LogManager.getLogger(ExportController.class.getName());

    @FXML
    private VBox root;

    @FXML
    public Button cancle;

    @FXML
    public Button exportBtn;

    @FXML
    public Button closeButton;

    @FXML
    public CheckBox generateData;

    @FXML
    public CheckBox springApp;

    @FXML
    public CheckBox en;
    @FXML
    public CheckBox de;
    @FXML
    public CheckBox es;
    @FXML
    public CheckBox fr;
    @FXML
    public CheckBox ja;
    @FXML
    public CheckBox ru;

    @FXML
    public CheckBox pomGenerate;

    @FXML
    public Spinner<Integer> noOfTestCases;

    @FXML
    public CheckBox chrome;
    @FXML
    public CheckBox safari;
    @FXML
    public CheckBox opera;
    @FXML
    public CheckBox firefox;
    @FXML
    public CheckBox html;
    @FXML
    public CheckBox edge;

    @FXML
    public Spinner<Integer> timeout;

    @FXML
    public TextField saveTo;

    @FXML
    public Button browse;

    @FXML
    public TextField artifact;
    @FXML
    public TextField group;
    @FXML
    public TextField version;

    @FXML
    public ComboBox<String> project;

    @FXML
    public TextField pack;

    @FXML
    public CheckBox overWrite;

    private final ObservableList<String> projectList = FXCollections.observableList(new ArrayList<>());

    /**
     * Initializes the controller class.
     */
    public void refreshProjects() {
        projectList.clear();
        Resource.getAllProjects().forEach((proj) -> {
            projectList.add(proj.getName());
        });
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        project.setItems(projectList);
        refreshProjects();

        // TODO
        root.setStyle("-fx-border-style: solid inside;"
                + "-fx-border-width: 5;"
                + "-fx-border-insets: 5;"
                + "-fx-border-color: rgb(58,69,88);");

        Text icon1 = GlyphsDude.createIcon(FontAwesomeIconName.CLOSE);
        icon1.setFill(Paint.valueOf("#b8b1b1"));
        closeButton.setGraphic(icon1);
        noOfTestCases.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 1000, 10));
        timeout.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 1000, 100));
        UIUtility.updateGraphics(closeButton, GlyphsDude.createIcon(FontAwesomeIconName.CLOSE), UIUtility.CSS.DANGER);
        UIUtility.updateGraphics(cancle, GlyphsDude.createIcon(FontAwesomeIconName.CLOSE), UIUtility.CSS.DANGER);
        UIUtility.updateGraphics(exportBtn, GlyphsDude.createIcon(FontAwesomeIconName.ANGLE_DOUBLE_RIGHT), UIUtility.CSS.SUCCESS);
        EventHandler keyHandler =new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if(event.getCharacter().equalsIgnoreCase(" ")){
                    event.consume();
                }
            }};
        pack.addEventHandler(KeyEvent.KEY_TYPED,keyHandler);
        group.addEventHandler(KeyEvent.KEY_TYPED,keyHandler);
        artifact.addEventHandler(KeyEvent.KEY_TYPED,keyHandler);

        browse.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                Window window = ((Node) event.getSource()).getScene().getWindow();
                DirectoryChooser chooser = new DirectoryChooser();
                chooser.setTitle("Export Selenium Project to");

                File file = chooser.showDialog(window);
                if (file == null) {
                    Notifications.create()
                            .title("Error")
                            .text("Please select the location to Export Selenium Project")
                            .showError();
                    return;
                } else {
                    saveTo.setText(file.getAbsolutePath());
                }
            }

        });
        
        exportBtn.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent event) -> {

            if (!chrome.isSelected() && !firefox.isSelected() && !safari.isSelected() && !opera.isSelected()
                    && !edge.isSelected() && !html.isSelected()) {
                Notifications.create()
                        .title("Export Error")
                        .text("Please Select Browser!")
                        .showInformation();
            }
            Boolean spring = springApp.isSelected();
            Properties properties = new Properties();
            Properties paths = new Properties();
            String save = saveTo.getText();
            String projectName = project.getSelectionModel().getSelectedItem();
            Project activeProject = Resource.getProject(projectName);
            String mavenArtifact = artifact.getText();

            String testPackage = pack.getText();
            String version_ = version.getText();
            PomCreator pomCreator = new PomCreator();
            TestWithPOMWriter testWriter = new TestWithPOMWriter();
            TestWithoutPOMWriter testWithoutPomWriter = new TestWithoutPOMWriter();

            log.info("######################## Export Configuration########################");
            log.info("Spring :"+spring);
            log.info("Languages Selected :"+getSelectedLanguages());
            log.info("Browser Selected :"+getSelectedBrowserList());
            log.info("Timeout :"+timeout.getValue());
            log.info("Generate Test Data :"+generateData.isSelected());
            log.info("POM Generate  :"+pomGenerate.isSelected());
            log.info("No  of Test Data :"+noOfTestCases.getValue());
            log.info("######################## Test Cases Recorded for Export ########################");
            try {
                log.info("Project :\n"+new ObjectMapper().writeValueAsString(activeProject));
            } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
                e.printStackTrace();
                log.error(e.getMessage(),e);
            }
            log.info("################################################");
            try {
                // iterate over project's test cases to export them one by one into selenium code
                for (TestCase testCase : activeProject.getCases()) {
                    String className = testCase.getName();
                    String homeURL = testCase.getLocation();
                    /// get the list of selected language to generate excels and assert files
                    List<Language> languages = getSelectedLanguages();
                    // analyze test cases to create pages for page object model
                    pomCreator.analyseWebElements(testCase.getEvents());
                    // create Page Object Model from the analyzed web pages
                    // iterate over the web pages identified and generate POM
                    if (pomGenerate.isSelected()) {
                        pomCreator.getWebPages().entrySet().forEach((entry) -> {
                            try {
                                // generate POM
                                ModelWriter.writePOM(pomCreator, save, mavenArtifact, testPackage,
                                        entry.getValue(), 30000);
                                log.info("Model file written for :" + entry.getValue().getTitle());

                            } catch (Exception ex) {
                                ex.printStackTrace();
                                log.error("ERROR:" + ex.toString());
                            }
                        });
                        testWriter.createTest(save, mavenArtifact, testCase.getEvents(),
                                pomCreator.getWebPages(), testPackage, className, homeURL, properties,
                                true);
                        log.info("Test File  write completed");
                    } else {
                        testWithoutPomWriter.createTest(save, mavenArtifact, testCase.getEvents(),
                                pomCreator.getWebPages(), testPackage, className, homeURL, paths,
                                properties, true);
                        log.info("Test File write without POM structure completed");
                    }

                    log.info("writing excel file for language :"+languages);
                    ExcelWriter.createExcel(testCase.getEvents(), save, mavenArtifact, className, noOfTestCases.getValue(), languages, true);
                    log.info("EXCEL File write completed");

                    DataProviderWriter.dataProvider(save, mavenArtifact, className, testPackage, true);
                    log.info("DATA PROVIDER for Test File write completed");

                    DataProviderUtilWriter.dataProvider(save, mavenArtifact, className, testPackage, true);
                    log.info("DATA PROVIDER UTILITY File write completed");

                    if(spring) {
                        createSpringApplication(save, testPackage, mavenArtifact, activeProject, true);
                    }else {
                        ApplicationWriter.writeApplication(save, testPackage, mavenArtifact, activeProject, true);
                        log.info("Application Main file write completed");
                    }
                }

                MavenPOMWriter.writePOM(save, mavenArtifact, group.getText(), version_, true);
                log.info("Maven POM (pom.xml) File  write completed");

                DriverFactoryWriter.writeFactory(save, mavenArtifact,
                        testPackage,timeout.getValue(), true);
                log.info("DRIVER FACTORY File write completed");

                SnapshotWriter.writeSnapshot(save, mavenArtifact,testPackage, true);
                log.info("SNAPSHOT File  write completed");

                WindowUtilityWriter.writeWindowUtility(save, mavenArtifact, testPackage,true);
                log.info("Window Utility File  write completed");

                AssertPropertyWriter.writeAssert(save, mavenArtifact, testPackage,true);
                log.info("Assert Written File  write completed");

                PathsPropertyWriter.writePathProperties(save, mavenArtifact,testPackage, true);
                log.info("Assert Written File write completed");

                PathsWriter.writePathsPropertiesFile(save, mavenArtifact,paths);
                log.info("Path properties File write completed");

                AssertPropertiesResourceWriter.writeResourcePropertiesFile(save, mavenArtifact, properties,
                        getSelectedLanguages());
                log.info("Assert properties File write completed");

                ScreenshotUtility.writeScreenshotUtility(save, mavenArtifact,testPackage, true);
                log.info("SNAPSHOT Utility File write completed");

                LanguageWriter.writeLanguage(save, mavenArtifact,testPackage, true);
                log.info("Language Utility File write completed");

                ReportListenerWriter.writeReportListener(save, mavenArtifact, testPackage,true);
                log.info("Report Listener File  write completed");

                ApplicationPropertiesWriter.writeApplicationProperties(save, mavenArtifact,
                        getSelectedBrowserList(), true, getSelectedLanguages());
                log.info("Application Properties  write completed");

                PropertyManagerWriter.writePropertyManager(save, mavenArtifact,testPackage, true);
                log.info("Application Manager  write completed");

                // TestNGWriter.xmlWriter(save, artifact,testPackage, getSelectedBrowsers(),
                // project, true);
                // log.info("TESTNG CONFIG File Written");
                Notifications.create()
                        .title("Export Completed")
                        .text("Project Exported Succesfully!")
                        .showInformation();

            } catch (IOException ex) {
                ex.printStackTrace();
                log.error(ex.getMessage(),ex);
                Notifications.create()
                        .title("Export Failed")
                        .text("Project Export Failed!")
                        .showError();
            } catch (Exception ex) {
                ex.printStackTrace();
                log.error(ex.getMessage(),ex);
                Notifications.create()
                        .title("Export Failed")
                        .text("Project Export Failed!")
                        .showError();
            }
        });
    }

    private void createSpringApplication(String location,String pkg, String artifactId,Project project, Boolean overWrite) throws IOException, FormatterException {
        SpringApplicationWriter.writeSpringApplicationClass(location,pkg,artifactId,project,overWrite);
        SpringApplicationWriter.writeApp(location,pkg,artifactId,project,overWrite);
        SpringApplicationWriter.writeAutoSquareReportListener(location,pkg,artifactId,project,overWrite);
        SpringApplicationWriter.writeModelAutoSquareSuite(location,pkg,artifactId,project,overWrite);
        SpringApplicationWriter.writeModelAutoSquareTest(location,pkg,artifactId,project,overWrite);
        SpringApplicationWriter.writeModelReport(location,pkg,artifactId,project,overWrite);
        SpringApplicationWriter.writeReportController(location,pkg,artifactId,project,overWrite);
        SpringApplicationWriter.writeWebSocketConfiguration(location,pkg,artifactId,project,overWrite);
        SpringApplicationWriter.writeWebSocketMessage(location,pkg,artifactId,project,overWrite);
        SpringApplicationWriter.writeApp(location,pkg,artifactId,project,overWrite);
        SpringApplicationWriter.extractUI(location,artifactId);

    }

    @FXML

    private void close_window(ActionEvent event) throws IOException {
        Stage stage = (Stage) cancle.getScene().getWindow();
        stage.close();
    }

    private List<Language> getSelectedLanguages() {

        List<Language> languages = new ArrayList<>();
        if (en.isSelected()) {
            languages.add(Language.EN);
        }
        if (de.isSelected()) {
            languages.add(Language.DE);
        }
        if (es.isSelected()) {
            languages.add(Language.ES);
        }
        if (fr.isSelected()) {
            languages.add(Language.FR);
        }
        if (ja.isSelected()) {
            languages.add(Language.JA);
        }
        if (ru.isSelected()) {
            languages.add(Language.RU);
        }
        return languages;
    }

    private List<String> getSelectedBrowserList() {
        List<String> list = new ArrayList<>();
        if (chrome.isSelected()) {
            list.add("Chrome");
        }
        if (firefox.isSelected()) {
            list.add("Firefox");
        }
        if (safari.isSelected()) {
            list.add("Safari");
        }
        if (edge.isSelected()) {
            list.add("Edge");
        }
        if (html.isSelected()) {
            list.add("HTML");
        }
        if (opera.isSelected()) {
            list.add("Opera");
        }
        return list;
    }

}
