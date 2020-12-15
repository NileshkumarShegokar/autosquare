/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.myriad.auto2.engine;

import com.myriad.auto2.controllers.SidebarController;
import com.myriad.auto2.controllers.TestCaseController;
import com.myriad.auto2.engine.selenium.DriverFactory;
import javafx.application.Platform;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.controlsfx.control.Notifications;
import org.openqa.selenium.WebDriver;

/**
 *
 * @author nshegoka
 */
public class ActiveCaseRecorder {

    static Logger log = LogManager.getLogger(ActiveCaseRecorder.class.getName());

    //make Driver Singleton
    private static WebDriver driver = null;
    private static ActiveCaseRecorder recorder = new ActiveCaseRecorder();
    private static TestCaseController controller;
    private static WebsocketServer server=null;
    private ActiveCaseRecorder() {
    }




    public static ActiveCaseRecorder getInstance(TestCaseController controller) {
        ActiveCaseRecorder.controller=controller;
        return recorder;
    }



    public WebDriver getWebDriver() {
        if (driver == null) {
            driver = new DriverFactory().getDriver("CHROME");
        }
        return driver;
    }

    public void startRecording(String baseUrl, String endURL){
        try{ Integer port= WebsocketServer.findFreePort();
        server=new WebsocketServer(controller,port);
        server.start();

        int count=0;
        while(count++ < 5)
        {
            if(server.isStarted()){
                driver = getWebDriver();
                if (!driver.getTitle().isEmpty()) {
                    throw new Exception("Error connecting Chrome Driver");
                }
                driver.get(baseUrl+"?port="+WebsocketServer.WEB_SOCKET_PORT+"&url="+endURL);
                break;
            }
            Thread.sleep(1000);
        }

        if(!server.isStarted()){ //check if connection is open properly and has no error while opening websocket on given port
            throw new Exception("Error creating WebSocket on given port :"+WebsocketServer.WEB_SOCKET_PORT);
        }

        }catch(Exception e){
            Notifications.create()
                    .title("Error")
                    .text(e.getMessage())
                    .showError();
        }


    }

    public static void quitDriver() {
        if(server!=null){
            try {
                server.stop(5000);

            } catch (InterruptedException ex) {
                log.error(ex.getMessage(),ex);
            }finally{
                server=null;
            }
        }
        if (driver != null) {
            try {
                driver.quit();
            }catch(Exception e){}finally {

                driver = null;
            }
        }
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                controller.setRecordText("Record Now");
            }
        });


    }

}
