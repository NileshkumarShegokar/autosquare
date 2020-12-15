/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.myriad.auto2.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.myriad.auto2.engine.ActiveCaseRecorder;
import com.myriad.auto2.model.EventModel;
import com.myriad.auto2.model.TestCase;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.controlsfx.control.Notifications;

import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.Iterator;
import java.util.ResourceBundle;

/**
 * FXML Controller class
 *
 * @author nshegoka
 */
public class TestCaseController implements Initializable {

    static Logger log = LogManager.getLogger(TestCaseController.class.getName());

    @FXML
    private TextField homeURL;

    @FXML
    private TextField pageURL;
    @FXML
    private TextField path;
    @FXML
    private TextField value;
    @FXML
    private TextField pattern;
    @FXML
    private TextField testCaseName;
    @FXML
    private TextField expectTo;
    @FXML
    private Button record;
    @FXML
    private Button verify;
    @FXML
    private Button create;
    @FXML
    private Button update;
    @FXML
    private Button delete;
    @FXML
    private Button refresh;
    @FXML
    private Button up;
    @FXML
    private Button down;
    @FXML
    private CheckBox fixed;
    @FXML
    private ComboBox<String> command;
    @FXML
    private ComboBox<String> type;
    @FXML
    private ComboBox<String> expect;
    @FXML
    private ComboBox<String> compare;

    @FXML
    private TableView<EventModel> table;
    @FXML
    private HBox box;
    @FXML
    private ScrollPane scroll;
    @FXML
    private VBox vBox;

    private TestCase test;
    ObservableList<EventModel> eventItems = FXCollections.observableArrayList();


    public TestCaseController() {
    }

    public void setRecordText(String status) {
        record.setText(status);
    }

    public Button getRecordButton() {
        return record;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        ObservableList<String> commandItems
                = FXCollections.observableArrayList(
                "url", "click", "mouseDown", "mouseUp", "waitBody", "sendKeys", "end", "screenshot", "expect"
        );
        command.setItems(commandItems);
        ObservableList<String> typeItems
                = FXCollections.observableArrayList(
                "N/A", "First Name", "Last Name", "Full Name", "E-Mail", "Date of Birth", "Address", "Street Name", "Address Line", "City", "Number", "Business Name", "Random Word"
        );
        type.setItems(typeItems);
        type.valueProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (newValue.equalsIgnoreCase("N/A")) {
                    fixed.setSelected(true);
                } else {
                    fixed.setSelected(false);
                    pattern.setText("{{" + newValue + "}}");
                }
            }

        });

        ObservableList<String> compareItems
                = FXCollections.observableArrayList(
                "equal", "notEqual", "contain", "notContain"
        );
        compare.setItems(compareItems);

        ObservableList<String> expectItems
                = FXCollections.observableArrayList(
                "val", "text", "displayed", "enabled", "selected", "attr", "css", "url", "title", "cookie", "localStorage", "sessionStorage", "alert"
        );
        expect.setItems(compareItems);

        TableColumn<EventModel, String> commandColumn = new TableColumn("Command");
        TableColumn<EventModel, String> urlColumn = new TableColumn("URL");
        TableColumn<EventModel, String> pathColumn = new TableColumn("XPath");
        TableColumn<EventModel, String> valueColumn = new TableColumn("Value");
        TableColumn<EventModel, String> isFixedColumn = new TableColumn("is Fixed");

        commandColumn.setCellValueFactory(new PropertyValueFactory<>("command"));
        urlColumn.setCellValueFactory(new PropertyValueFactory<>("url"));
        pathColumn.setCellValueFactory(new PropertyValueFactory<>("path"));
        valueColumn.setCellValueFactory(new PropertyValueFactory<>("value"));
        isFixedColumn.setCellValueFactory(new PropertyValueFactory<>("fixed"));

        commandColumn.setSortable(false);
        urlColumn.setSortable(false);
        pathColumn.setSortable(false);
        valueColumn.setSortable(false);
        isFixedColumn.setSortable(false);

        table.setMaxWidth(Region.USE_PREF_SIZE);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        commandColumn.prefWidthProperty().bind(table.widthProperty().multiply(0.2));
        urlColumn.prefWidthProperty().bind(table.widthProperty().multiply(0.2));
        pathColumn.prefWidthProperty().bind(table.widthProperty().multiply(0.3));
        valueColumn.prefWidthProperty().bind(table.widthProperty().multiply(0.2));
        isFixedColumn.prefWidthProperty().bind(table.widthProperty().multiply(0.1));
        table.getColumns().clear();
        table.getColumns().addAll(commandColumn, urlColumn, pathColumn, valueColumn, isFixedColumn);
        table.setItems(eventItems);

        table.getSelectionModel().selectedIndexProperty().addListener((obs, oldSelection, newSelection) -> {
            System.out.println("OBS:" + obs);
            System.out.println("OLDSelectionIndex:" + oldSelection);
            // table.getSelectionModel().select(obs.getValue().intValue());
            if (newSelection != null) {
                System.out.println("New Selection model:" + newSelection.intValue());
                JsonNode event = test.getEvents().get(newSelection.intValue());

                JsonNode data = event.path("data");
                String cmd = data.path("cmd").asText();
                JsonNode dataData = data.path("data");
                if (cmd.equalsIgnoreCase("expect")) {
                    ArrayNode node = (ArrayNode) dataData.path("params");
                    path.setText(node.get(0).asText());
                    expectTo.setEditable(true);
                    type.setEditable(true);
                    compare.setEditable(true);
                    expect.getSelectionModel().select(dataData.path("type").asText());
                    compare.getSelectionModel().select(dataData.path("compare").asText());
                    expectTo.setText(dataData.path("to").asText());
                } else {
                    path.setText(dataData.path("path").asText());
                    value.setText(dataData.path("keys").asText());
                    expectTo.setEditable(false);
                    expect.setEditable(false);
                    compare.setEditable(false);
                }
                command.getSelectionModel().select(cmd);
                type.getSelectionModel().select(data.path("type").asText());
                fixed.setSelected(data.path("fix").asBoolean());
                pattern.setText(data.path("pattern").asText());
                pageURL.setText(dataData.asText());

            }

        });

        record.setOnAction(event -> {

                    Button node = (Button) event.getSource();
                    String text = node.getText();

                    if (text.equalsIgnoreCase("Record Now")) {
                        ActiveCaseRecorder activeCaseRecorder = ActiveCaseRecorder.getInstance(TestCaseController.this);
                        activeCaseRecorder.startRecording("chrome-extension://ildkpdfgnhebgecchoefmnilbfgihbhh/start.html", homeURL.getText());
                        record.setText("Opening...");

                    } else if (text.equalsIgnoreCase("Opening...")) {
                        Notifications.create()
                                .title("Error")
                                .text("AutoSquare is having browser operation in progress, please ensure you have close existing recording OR browser opening operation is already in progress ")
                                .showError();
                    }


                }
        );
        refresh.addEventHandler(MouseEvent.MOUSE_CLICKED,
                (MouseEvent event) -> {
                    eventItems.clear();
                    test.getEvents().forEach((node) -> {
                        addEvent(node, true);
                    });
                }
        );

        create.addEventHandler(MouseEvent.MOUSE_CLICKED,
                (MouseEvent event) -> {
                    log.error("Yet to implement Create method");
                }
        );

        update.addEventHandler(MouseEvent.MOUSE_CLICKED,
                (MouseEvent event) -> {
                    Integer row = table.getSelectionModel().selectedIndexProperty().intValue();
                    JsonNode eventData = test.getEvents().get(row);
                    EventModel model = eventItems.get(row);
                    JsonNode data = eventData.path("data");
                    JsonNode dataData = data.path("data");
                    if (!dataData.isTextual() && !dataData.isMissingNode()) {
                        model.setPath(path.getText());
                        model.setValue(value.getText());
                        ((ObjectNode) dataData).put("path", path.getText());
                        ((ObjectNode) dataData).put("keys", value.getText());
                    }

                    if (command.getSelectionModel().selectedItemProperty().get().equalsIgnoreCase("expect")) {
                        ObjectMapper mapper = new ObjectMapper();
                        JsonNode paramsNode;
                        try {
                            String xPath = path.getText().replaceAll("\"", "\\\\\\\"");
                            String param = "[\"" + xPath + "\"]";
                            paramsNode = mapper.readTree(param);
                            ((ObjectNode) dataData).set("params", paramsNode);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        ((ObjectNode) dataData).put("type", expect.getSelectionModel().selectedItemProperty().get());
                        ((ObjectNode) dataData).put("compare", compare.getSelectionModel().selectedItemProperty().get());
                        ((ObjectNode) dataData).put("to", expectTo.getText());
                    }
                    model.setCommand(command.getSelectionModel().selectedItemProperty().get());
                    model.setFixed("" + fixed.isSelected());
                    ((ObjectNode) data).put("cmd", command.getSelectionModel().selectedItemProperty().get());
                    ((ObjectNode) data).put("type", type.getSelectionModel().selectedItemProperty().get());
                    ((ObjectNode) data).put("fix", fixed.isSelected());
                    ((ObjectNode) data).put("pattern", pattern.getText());
                    if (command.getSelectionModel().selectedItemProperty().get().equalsIgnoreCase("url")) {
                        ((ObjectNode) data).put("data", pageURL.getText());
                    }

                    table.refresh();
                }
        );


        delete.addEventHandler(MouseEvent.MOUSE_CLICKED,
                (MouseEvent event) -> {

                    int row = table.getSelectionModel().selectedIndexProperty().intValue();
                    test.getEvents().remove(row);
                    eventItems.remove(row);

                }
        );

        up.addEventHandler(MouseEvent.MOUSE_CLICKED,
                (MouseEvent event) -> {
                    int selectedRow = table.getSelectionModel().getSelectedIndex();
                    try {
                        //swap in UI
                        Collections.swap(eventItems, selectedRow, (selectedRow - 1));
                        //swap in test case
                        Collections.swap(test.getEvents(), selectedRow, (selectedRow - 1));
                    } catch (IndexOutOfBoundsException exception) {
                        Notifications.create()
                                .title("Warning")
                                .text("Can not shift row further.")
                                .showWarning();
                    }
                }
        );

        down.addEventHandler(MouseEvent.MOUSE_CLICKED,
                (MouseEvent event) -> {
                    int selectedRow = table.getSelectionModel().getSelectedIndex();
                    try {
                        //swap in UI
                        Collections.swap(eventItems, selectedRow, (selectedRow + 1));
                        //swap in test case
                        Collections.swap(test.getEvents(), selectedRow, (selectedRow + 1));
                    } catch (IndexOutOfBoundsException exception) {
                        Notifications.create()
                                .title("Warning")
                                .text("Can not shift row further.")
                                .showWarning();
                    }
                }
        );
    }

    public void addEvent(JsonNode event, boolean init) {
        System.out.println("event received:" + event.toString());
        try {
            JsonNode data = event.path("data");
            String cmd = data.path("cmd").asText();
            JsonNode dataData = data.path("data");
            String pathLocal, keys;
            if (cmd.equalsIgnoreCase("expect")) {
                ArrayNode node = (ArrayNode) dataData.path("params");
                pathLocal = node.get(0).asText();
                keys = dataData.path("to").asText();
            } else {
                pathLocal = dataData.path("path").asText();
                keys = dataData.path("keys").asText();
            }
            String dataDataString = dataData.asText();
            Boolean fix = data.path("fix").asBoolean();

            if (!init)
                test.addEvent(event);

            eventItems.add(new EventModel(cmd, dataDataString, pathLocal, keys, fix.toString()));

        } catch (Exception e) {
            e.printStackTrace();
            log.error(e);
        }
    }

    public void initTest(TestCase test) {
        this.test = test;
        //ensure while loding test case from file, it should not add event to event-list multiple times, add flag fr init
        boolean init = true;
        testCaseName.setText(test.getName());
        homeURL.setText(test.getLocation());
        if (!test.getEvents().isEmpty()) {
            Iterator<JsonNode> node = test.getEvents().iterator();
            while (node.hasNext()) {
                addEvent(node.next(), true);
            }
        }
    }


}
