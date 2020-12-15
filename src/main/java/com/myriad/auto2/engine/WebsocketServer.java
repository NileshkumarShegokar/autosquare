package com.myriad.auto2.engine;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.myriad.auto2.controllers.SidebarController;
import com.myriad.auto2.controllers.TestCaseController;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

public class WebsocketServer extends WebSocketServer {

    static Logger log = LogManager.getLogger(WebsocketServer.class.getName());
    private WebSocket active = null;
    private Boolean isStarted = false;
    private TestCaseController controller;
    public static Integer WEB_SOCKET_PORT = 0;

    public WebsocketServer(TestCaseController controller, Integer port) {

        super(new InetSocketAddress(port));
        this.WEB_SOCKET_PORT=port;
        this.controller = controller;

    }

    public Boolean isStarted() {
        return isStarted;
    }

    @Override
    public void start() {
        super.start(); //To change body of generated methods, choose Tools | Templates.
        System.out.println("Websocket Server started on port :" + this.getPort());
    }

    @Override
    public void stop(int timeout) throws InterruptedException {
        super.stop(timeout);
        isStarted=false;
    }

    public void sendWsMessage(String type, String data) {

        String message = "{\"type\": \"" + type + "\", \"data\": " + data + "}";
        active.send(message);
        log.info("Message send " + message);
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        active = conn;
        sendWsMessage("config", getConfig());
        log.info("New connection from " + conn.getRemoteSocketAddress().getAddress().getHostAddress());
    }

    public String getConfig() {
        return "{\"version\":\"0.1\",\"pathAttrs\":[{\"name\":\"id\",\"on\":true},{\"name\":\"text\",\"on\":true},{\"name\":\"name\",\"on\":true},{\"name\":\"value\",\"on\":true},{\"name\":\"data-id\",\"on\":true},{\"name\":\"data-name\",\"on\":true},{\"name\":\"type\",\"on\":true},{\"name\":\"data-type\",\"on\":true},{\"name\":\"role\",\"on\":true},{\"name\":\"data-role\",\"on\":true},{\"name\":\"data-value\",\"on\":true}],\"attrValueBlack\":\"\",\"classValueBlack\":\"\",\"hideBeforeExpect\":\"\",\"testVars\":{},\"specLists\":[],\"wdproxy\":\"\",\"i18n\":{\"jump_placeholder\":\"Please input url or test file name\",\"jump_alert\":\"Please input correct url or test file name!\",\"start_button\":\"Start Record\",\"icon_record_tip\":\"Recording...click for end record\",\"icon_end_tip\":\"Record ended\",\"icon_end_msg\":\"Record is ended, please reopen client!\",\"module_start_title\":\"Test is loading\",\"module_start_message\":\"Test loading: %s, please dont't press any keyboard or mouse when loading!\",\"module_end_title\":\"Test loaded\",\"module_end_message\":\"Test load %s: %s, please continue recording.\",\"exec_succeed\":\"execute succeed\",\"exec_failed\":\"execute failed\",\"succeed\":\"succeed\",\"failed\":\"failed\",\"loading\":\"Loadingâ€¦â€¦\",\"attr_switch\":\"Attr switch: \",\"attr_black\":\"Attr black: \",\"attr_black_tip\":\"Please input RegExp for filter attr value, E.g.: /black_val/\",\"button_hover_on_text\":\"Add Hover\",\"button_hover_off_text\":\"End Hover\",\"button_sleep_text\":\"Add Sleep\",\"button_expect_text\":\"Add Expect\",\"button_vars_text\":\"Use Var\",\"button_jscode_text\":\"Run JS\",\"button_jump_text\":\"Jump to\",\"button_end_text\":\"End Record\",\"button_text_text\":\"Input Text\",\"button_alert_text\":\"Alert Cmd\",\"button_back_text\":\"Back Button\",\"dialog_ok\":\"Ok\",\"dialog_cancel\":\"Cancel\",\"dialog_expect_title\":\"Add Expectï¼š\",\"dialog_expect_sleep\":\"Delay Time: \",\"dialog_expect_type\":\"Expect Type: \",\"dialog_expect_dom\":\"Expect DOM: \",\"dialog_expect_path\":\"Expect Path: \",\"dialog_expect_param\":\"Expect param: \",\"dialog_expect_compare\":\"Expect compare: \",\"dialog_expect_to\":\"Expect to: \",\"dialog_vars_title\":\"Use Var: \",\"dialog_vars_type\":\"Use type: \",\"dialog_vars_type_insert\":\"Insert var\",\"dialog_vars_type_update\":\"Update var\",\"dialog_vars_name\":\"Var name: \",\"dialog_vars_addname\":\"Add var\",\"dialog_vars_name_empty\":\"Name empty!\",\"dialog_vars_name_duplicated\":\"Name duplicated!\",\"dialog_vars_value\":\"Var value: \",\"dialog_vars_template\":\"Var template: \",\"dialog_vars_template_result\":\"Template result: \",\"dialog_vars_update_type\":\"Update type: \",\"dialog_vars_update_dom\":\"Update DOM: \",\"dialog_vars_update_param\":\"Update param: \",\"dialog_vars_update_regex\":\"Update regex: \",\"dialog_jump_title\":\"Jump to: \",\"dialog_jump_target\":\"Jump target: \",\"dialog_sleep_title\":\"Add sleep: \",\"dialog_sleep_time\":\"Sleep time: \",\"dialog_sleep_time_tip\":\"Please input sleep time\",\"dialog_text_title\":\"Input Text: \",\"dialog_text_content\":\"Text Content: \",\"dialog_text_content_tip\":\"Please input text content\",\"dialog_alert_title\":\"Alert Cmd: \",\"dialog_alert_option\":\"Alert option: \",\"dialog_alert_option_accpet\":\"Accpet\",\"dialog_alert_option_dismiss\":\"Dismiss\",\"dialog_jscode_title\":\"Run JS\",\"dialog_jscode_code\":\"JS Code\",\"dialog_jscode_tip\":\"Please input jscode run in browser sideï¼ŒE.g.: document.title='test';\"}}";
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        active.close();
        log.info("Closed connection to " + conn.getRemoteSocketAddress().getAddress().getHostAddress());
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        log.info("Message from client: " + message);
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode event = null;
        try {
            event = objectMapper.readTree(message);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        String type = event.path("type").asText();
        switch (type) {
            case "saveCmd": {
                onCommand(event);
                break;
            }
            case "save": {
                JsonNode actualObj = null;
                try {
                    actualObj = objectMapper.readTree("{\"type\":\"save\",\"data\":{\"cmd\":\"end\"}}");
                    active.close();

                    ActiveCaseRecorder.quitDriver();
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    log.debug(e.getMessage(),e);
                }
                onCommand(actualObj);
                break;
            }
            case "end": {
                active.close();
                ActiveCaseRecorder.quitDriver();
                break;
            }
        }
        // active.send(message);

    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        if (active != null) {
            active.close();
            active = null;
        }
        ex.printStackTrace();
        log.error(ex.getMessage(),ex);
    }

    List<String> arrSendKeys = new ArrayList<String>();
    JsonNode lastKeyEvent, lastClickEvent, lastDoubleClickEvent;
    List<JsonNode> cmdQueue = new ArrayList<JsonNode>();

    public void onCommand(JsonNode event) {
        // Merge command stream
        //testCaseUI = AutoSquare.getTestCaseUI();
        log.info("Command Received: " + event.toString());
        String cmd = event.path("data").path("cmd").asText();
        if (Pattern.matches("/^!/", cmd)) {
            // generate code for event now
            controller.addEvent(event,false);
        } else {
            // filter more on event
            sendKeysFilter(event);
        }
    }

    public void sendKeysFilter(JsonNode event) {
        // sendKeys
        JsonNode dataObject = event.path("data");
        String cmd = dataObject.path("cmd").asText();
        JsonNode dataData = dataObject.path("data");

        if (cmd.equalsIgnoreCase("sendKeys")) {
            // push the keys to arr of send keys until find escape sequence
            arrSendKeys.add(dataData.path("keys").asText());
        } else {
            if (arrSendKeys.size() > 0) {
                // Meet the conditions and merge
                JsonNode lastDataObject = lastKeyEvent.path("data");
                JsonNode lastData = lastDataObject.path("data");
                ((ObjectNode) lastData).put("keys", joinQueueContents(arrSendKeys));
                // **********************check its processing key strings
                // properly*******************
                clickFilter(lastKeyEvent);
                // empty the priority queue
                arrSendKeys.clear();
            }
            clickFilter(event);
        }
        lastKeyEvent = event;
    }

    private String joinQueueContents(List<String> queue) {
        Iterator<String> itr = queue.iterator();
        StringBuilder builder = new StringBuilder();
        while (itr.hasNext()) {
            builder.append(itr.next());
        }
        return builder.toString();
    }

    public void clickFilter(JsonNode event) {
        // Merged into click，Increase compatibility (mouseDown not support button
        // parameter)
        JsonNode dataObject = event.path("data");
        String cmd = dataObject.path("cmd").asText();
        JsonNode data = dataObject.path("data");
        String lastCmd = null;
        JsonNode lastDataObject = null;
        if (lastClickEvent != null) {
            lastDataObject = lastClickEvent.path("data");
            lastCmd = lastDataObject.path("cmd").asText();
        }
        if (lastClickEvent != null && lastCmd.equalsIgnoreCase("mouseDown")) {
            JsonNode lastData = lastDataObject.path("data");
            log.info("last click event path:" + lastDataObject.path("data").path("path").asText());
            log.info("Current click event path:" + data.path("path").asText());
            if (cmd.equalsIgnoreCase("mouseUp")
                    && dataObject.path("window").asInt() == lastDataObject.path("window").asInt()
                    && dataObject.path("frame").asText().equalsIgnoreCase(lastDataObject.path("frame").asText())
                    && lastDataObject.path("data").path("path").asText().equalsIgnoreCase(data.path("path").asText())
                    && Math.abs(lastData.path("x").asDouble() - data.path("x").asDouble()) < 20
                    && Math.abs(lastData.path("y").asDouble() - data.path("y").asDouble()) < 20) {
                // Condition satisfied, merged into click
                ((ObjectNode) dataObject).put("cmd", "click");
                /*
				 * cmdInfo = { window: cmdInfo.window, frame: cmdInfo.frame, cmd: 'click', data:
				 * data, text: cmdInfo.text };
                 */
            } else {
                // No need to merge, restore before the old mouseDown
                dblClickFilter(lastClickEvent);
            }

        }
        if (!cmd.equalsIgnoreCase("mouseDown")) {
            // mouseDown Cache to next time, confirm if you need to merge click，non -
            // mouseDown Execute immediately
            dblClickFilter(event);
        }
        lastClickEvent = event;
    }

    public void dblClickFilter(JsonNode event) {
        // Merged into dblClick，Increase compatibility, some browsers do not support two
        // consecutive times click
        JsonNode dataObject = event.path("data");
        String cmd = dataObject.path("cmd").asText();
        JsonNode data = dataObject.path("data");
        JsonNode lastDataObject = null;
        String lastCmd = null;
        if (lastDoubleClickEvent != null) {
            lastDataObject = lastDoubleClickEvent.path("data");
            lastCmd = lastDataObject.path("cmd").asText();
        }
        if (lastDoubleClickEvent != null && lastCmd.equalsIgnoreCase("click")) {
            JsonNode lastData = lastDataObject.path("data");

            // clearTimeout(dblClickFilterTimer);
            if (cmd.equalsIgnoreCase("click")
                    && dataObject.path("window").asInt() == lastDataObject.path("window").asInt()
                    && dataObject.path("frame").asText().equalsIgnoreCase(lastDataObject.path("frame").asText())
                    && lastDataObject.path("data").path("path").asText().equalsIgnoreCase(data.path("path").asText())
                    && Math.abs(lastData.path("x").asDouble() - data.path("x").asDouble()) < 20
                    && Math.abs(lastData.path("y").asDouble() - data.path("y").asDouble()) < 20) {
                // Condition satisfied, merged into dblClick
                ((ObjectNode) dataObject).put("cmd", "dblClick");
                /*
				 * cmdInfo = { window: cmdInfo.window, frame: cmdInfo.frame, cmd: 'dblClick',
				 * data: data, text: cmdInfo.text };
                 */
            } else {
                // No need to merge, restore before the old click
                controller.addEvent(lastDoubleClickEvent,false);
            }

        }
        if (!cmd.equalsIgnoreCase("click")) {
            // click Cache to the next time, confirm whether you need to merge dblClick,
            // non-click immediately
            controller.addEvent(event,false);
        } else {

            /*
			 * dblClickFilterTimer = setTimeout(function(){ cmdQueue.push(lastCmdInfo2, 2);
			 * lastCmdInfo2 = null; }, 500);
             */
        }
        lastDoubleClickEvent = event;
    }

    @Override
    public void onStart() {

        isStarted = true;
    }

    public static Integer findFreePort(){
        Integer port=0;
        try {
            ServerSocket s = new ServerSocket(port);
            s.setReuseAddress(true);
            port=s.getLocalPort();
            s.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return port;
    }
}
