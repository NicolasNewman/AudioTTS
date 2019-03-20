package org.audiotts.application;

import javafx.application.Application;
import static javafx.application.Application.launch;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.audiotts.controller.MainController;

import java.io.File;
import java.util.Map;
import java.util.Optional;


public class MainApp extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        verifyDataExists();
        verifyEnvVar();

        stage.getIcons().add(new Image(MainApp.class.getResourceAsStream("/icons/icon.png")));
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/scene.fxml"));
        Parent root = loader.load();
        
        Scene scene = new Scene(root);

        scene.getStylesheets().add("/styles/LightTheme.css");

        
        stage.setTitle("AudioTTS");
        stage.setScene(scene);
        stage.show();

        stage.setOnCloseRequest((e) -> {
            MainController controller = loader.getController();
            controller.shutdown();
            Platform.exit();
            System.exit(0);
        });
    }

    /**
     * Checks to make sure each needed directory exists. If not, create it
     * Also creates the log file if debug mode is enabled
     */
    public static void verifyDataExists() {
        File appDir = new File(Global.APP_PATH);
        File audioDir = new File(Global.APP_AUDIO_PATH);
        File dataDir = new File(Global.APP_DATA_PATH);
        if (!appDir.exists()) { appDir.mkdirs(); }
        if (!audioDir.exists()) { audioDir.mkdirs(); }
        if (!dataDir.exists()) { dataDir.mkdirs(); }
    }

    public static void verifyEnvVar() {
        Map<String, String> env = System.getenv();
        boolean pathSet = false;
        boolean fileExists = false;
        for (String envName : env.keySet()) {
            if (envName.equals("GOOGLE_APPLICATION_CREDENTIALS")) {
                pathSet = true;
                File f = new File(env.get(envName));
                fileExists = f.exists();
            }
        }

        String headerText;
        if (!pathSet) {
            headerText = "Error: environment variable not set";
        } else if (!fileExists){
            headerText = "Error: file specified by GOOGLE_APPLICATION_CREDENTIALS not found";
        } else {
            return;
        }

        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(headerText);
        alert.setContentText("Please properly configure your path before proceeding. The software will now exit.");
        alert.showAndWait();

        Platform.exit();
        System.exit(0);
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
