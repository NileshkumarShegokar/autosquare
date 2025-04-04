package com.myriad.auto2;

import com.myriad.auto2.controllers.MainController;
import com.myriad.auto2.engine.ControllerStore;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Calendar;
import java.util.Date;

public class MainApp extends Application {

    static Logger log = LogManager.getLogger(MainApp.class.getName());
    public static Stage stage = null;

    @Override
    public void start(Stage stage)  {
        try {
            String home = "/fxml/Main.fxml";
            //Dec 31, 2019 12:00:00 AM for Evolution copy expire date
//            Date date = new Date(1626535546000L);
//            long current = Calendar.getInstance().getTimeInMillis();
//            if (current >= date.getTime()) {
//                home = "/fxml/License.fxml";
//            } else {
//                stage.setMaximized(true);
//            }
            FXMLLoader loader = new FXMLLoader();
            Parent root = loader.load(getClass().getResource(home));
            MainController controller = (MainController) loader.getController();
            ControllerStore.addController("mainController", controller);
            Scene scene = new Scene(root);
            stage.setScene(scene);

            //stage.setFullScreen(true);
            stage.setTitle("AutoSquare");

            //stage.setMaximized(true);
            stage.show();
            MainApp.stage = stage;
        }catch (Exception e)
        {
            e.printStackTrace();
            log.error(e.getMessage(),e);
        }
    }

    /**
     * The main() method is ignored in correctly deployed JavaFX application.
     * main() serves only as fallback in case the application can not be
     * launched through deployment artifacts, e.g., in IDEs with limited FX
     * support. NetBeans ignores main().
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

}
