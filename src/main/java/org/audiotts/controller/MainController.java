package org.audiotts.controller;

import com.jfoenix.controls.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.HBox;
import org.audiotts.application.Global;
import org.audiotts.classes.AudioPlayer;
import org.audiotts.classes.FileWatcher;
import org.audiotts.classes.SonicPlayer;
import org.audiotts.classes.TTSManager;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    @FXML
    private JFXListView<String> audioList;

    @FXML
    private JFXTextArea textArea;

    @FXML
    private JFXButton btnPlay, btnStop, btnProcess, forwardSmall, forwardLarge, reverseSmall, reverseLarge;

    @FXML
    private JFXTextField filenameField;

    @FXML
    private HBox mediaControl, mediaPlayingControl, processControl;

    @FXML
    private MenuItem menuAudioDir;

    @FXML
    private Label timeCurrent, timeEnd;

    @FXML
    private JFXProgressBar timeProgress;

    @FXML
    private JFXComboBox<String> comboPlayback;

    private TTSManager manager;
    private FileWatcher watcher;
    private SonicPlayer player;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        manager = new TTSManager(this);
        player = new SonicPlayer(this);

        startWatcher();
        fillAudioList();
        initEventListener();

        mediaControl.setVisible(false);
        mediaControl.setManaged(false);
        mediaPlayingControl.setVisible(false);
        mediaPlayingControl.setManaged(false);

        comboPlayback.getItems().addAll("x0.5", "x0.75", "x1", "x1.25", "x1.5");
    }

    public void shutdown() {
        watcher.notifyShutdown();
    }

    public void fillAudioList() {
        audioList.getItems().removeAll();

        File folder = new File(Global.APP_AUDIO_PATH);
        File[] listOfFiles = folder.listFiles();
        ArrayList<String> audioFiles = new ArrayList<>();

        for (File f : listOfFiles != null ? listOfFiles : new File[0]) {
            if (f.isFile() && f.getName().contains(Global.AUDIO_FILE_TYPE)) {
                audioFiles.add(f.getName().replace(Global.AUDIO_FILE_TYPE, ""));
            }
        }
        ObservableList<String> songList = FXCollections.observableArrayList(audioFiles);
        this.audioList.setItems(songList);
    }

    private void initEventListener() {
        btnProcess.setOnMouseClicked((e) -> {
            String text = textArea.getText();
            int indx = audioList.getItems().size();
            String name = filenameField.getText().length() != 0 ? filenameField.getText() : ("audio-" + (indx+1));
            manager.processText(text, name);
            btnProcess.setDisable(true);
        });

        btnPlay.setOnMouseClicked((e) -> {
            switch (player.getStatus()) {
                case PLAY:
                    player.pause();
                    btnPlay.setText("Resume");
                    break;
                case PAUSE:
                    player.play();
                    btnPlay.setText("Pause");
                    break;
                case STOP:
                    String file = audioList.getSelectionModel().getSelectedItem().concat(Global.AUDIO_FILE_TYPE);
                    try {
                        player.loadFile(Global.APP_AUDIO_PATH + file);
                    } catch (IOException | UnsupportedAudioFileException | LineUnavailableException e1) {
                        e1.printStackTrace();
                    }
//                    player.play();
                    btnPlay.setText("Pause");
                    break;
                default:
                    break;
            }
        });

        btnStop.setOnMouseClicked((e) -> {
            try {
                player.stop();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            btnPlay.setText("Play");
        });

        forwardSmall.setOnMouseClicked((e) -> {
            try {
                player.setPosition(5.0f);
            } catch (IOException e1) { e1.printStackTrace(); }
        });
        forwardLarge.setOnMouseClicked((e) -> {
            try {
                player.setPosition(15.0f);
            } catch (IOException e1) { e1.printStackTrace(); }
        });
        reverseSmall.setOnMouseClicked((e) -> {
            try {
                player.setPosition(-5.0f);
            } catch (IOException e1) { e1.printStackTrace(); }
        });
        reverseLarge.setOnMouseClicked((e) -> {
            try {
                player.setPosition(-15.0f);
            } catch (IOException e1) { e1.printStackTrace(); }
        });

        comboPlayback.valueProperty().addListener((prop, old, val) -> {
            comboPlayback.setPromptText(val);
            float value = Float.parseFloat(val.replace("x", "").concat("f"));
            System.out.println("Setting speed: " + value);
            player.setSpeed(value);
        });

        textArea.setOnMouseClicked((e) -> {
            processControl.setVisible(true);
            processControl.setManaged(true);
            mediaControl.setVisible(false);
            mediaControl.setManaged(false);
            mediaPlayingControl.setVisible(false);
            mediaPlayingControl.setManaged(false);
        });

        audioList.setOnMouseClicked((e) -> {
            processControl.setVisible(false);
            processControl.setManaged(false);
            mediaControl.setVisible(true);
            mediaControl.setManaged(true);
            mediaPlayingControl.setVisible(false);
            mediaPlayingControl.setManaged(false);
        });

        menuAudioDir.setOnAction((e) -> {
            try { Desktop.getDesktop().open(new File(Global.APP_AUDIO_PATH)); }
            catch (IOException e1) { e1.printStackTrace(); }
        });
    }

    private void startWatcher() {
        try {
            watcher = new FileWatcher(this, Global.APP_AUDIO_PATH);
            watcher.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void toggleProcess() {
        if (btnProcess.isDisable()) { btnProcess.setDisable(false); }
        else { btnProcess.setDisable(true); }
    }

    public JFXButton getPlayButton() { return btnPlay; }
    public HBox getMediaPlayingControl() { return mediaPlayingControl; }
    public Label getTimeCurrent() { return timeCurrent; }
    public Label getTimeEnd() { return timeEnd; }
    public JFXTextArea getTextArea() { return textArea; }
    public JFXListView getListView() { return audioList; }
    public JFXProgressBar getTimeProgress() { return timeProgress; }

}
