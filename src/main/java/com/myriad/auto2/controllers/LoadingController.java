package com.myriad.auto2.controllers;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import com.myriad.auto2.engine.ControllerStore;
import com.myriad.auto2.engine.WebsocketServer;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * FXML Controller class
 *
 * @author nshegoka
 */
public class LoadingController implements Initializable {


    @FXML
    private Label status;


    public void setStatus(String statusString) {
        status.setText(statusString);
    }

    public String getStatus() {
        return status.getText();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}
