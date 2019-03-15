package org.audiotts.application;

import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.audiotts.controller.MainController;

import java.io.File;


public class MainApp extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        verifyDataExists();

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/scene.fxml"));
        Parent root = loader.load();
        
        Scene scene = new Scene(root);

        scene.getStylesheets().add("/styles/Styles.css");
        
        stage.setTitle("JavaFX and Maven");
        stage.setScene(scene);
        stage.show();

        stage.setOnCloseRequest((e) -> {
            MainController controller = loader.getController();
            controller.shutdown();
        });
    }

    /**
     * Checks to make sure each needed directory exists. If not, create it
     * Also creates the log file if debug mode is enabled
     */
    public static void verifyDataExists() {
        File appDir = new File(Global.APP_PATH);
        File audioDir = new File(Global.APP_AUDIO_PATH);
        if (!appDir.exists()) {
            appDir.mkdirs();
        }
        if (!audioDir.exists()) {
            audioDir.mkdirs();
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
