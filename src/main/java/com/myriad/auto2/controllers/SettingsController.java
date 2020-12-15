package com.myriad.auto2.controllers;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import com.myriad.auto2.engine.ControllerStore;
import com.myriad.auto2.engine.WebsocketServer;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

/**
 * FXML Controller class
 *
 * @author nshegoka
 */
public class SettingsController implements Initializable {

    @FXML
    private TextField port;
    @FXML
    private Button save;
    @FXML
    private Button cancel;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        port.setText(WebsocketServer.WEB_SOCKET_PORT + "");
        initEvents();
    }

    public TextField getPort() {
        return port;
    }

    private void initEvents() {
        save.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                WebsocketServer.WEB_SOCKET_PORT = Integer.parseInt(port.getText());
                System.out.println(ControllerStore.getAllControllers());
                SidebarController controller = (SidebarController) ControllerStore.getController("sidebarController");
                controller.closeSettings();
            }
        });
        cancel.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                SidebarController controller = (SidebarController) ControllerStore.getController("sidebarController");
                controller.closeSettings();
            }
        });
    }

}
