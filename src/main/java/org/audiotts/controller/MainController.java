package org.audiotts.controller;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import org.audiotts.application.Global;
import org.audiotts.classes.AudioPlayer;
import org.audiotts.classes.FileWatcher;
import org.audiotts.classes.TTSManager;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

public class MainController implements Initializable {

    @FXML
    private ListView<String> audioList;

    @FXML
    private TextArea textArea;

    @FXML
    private Button btnPlay, btnProcess;

    @FXML
    HBox mediaControl, processControl;

    private TTSManager manager;
    private FileWatcher watcher;
    private AudioPlayer player;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        manager = new TTSManager();
        player = new AudioPlayer();

        startWatcher();
        fillAudioList();
        initEventListener();

        mediaControl.setVisible(false);
        mediaControl.setManaged(false);
    }

    public void shutdown() {
        watcher.notifyShutdown();
    }

    public void fillAudioList() {
        audioList.getItems().removeAll();

        File folder = new File(Global.APP_AUDIO_PATH);
        File[] listOfFiles = folder.listFiles();
        ArrayList<String> audioFiles = new ArrayList<>();

        for (File f : listOfFiles) {
            if (f.isFile() && f.getName().contains(Global.AUDIO_FILE_TYPE)) {
                audioFiles.add(f.getName());
            }
        }
        ObservableList<String> songList = FXCollections.observableArrayList(audioFiles);
        this.audioList.setItems(songList);
    }

    private void initEventListener() {
        btnProcess.setOnMouseClicked((e) -> {
            String text = textArea.getText();
            int indx = audioList.getItems().size();
            manager.processText(text, ("audio-" + (indx+1)));
        });

        btnPlay.setOnMouseClicked((e) -> {
            String file = audioList.getSelectionModel().getSelectedItem();
            player.setFile(Global.APP_AUDIO_PATH + file);
            player.play();
        });

        textArea.setOnMouseClicked((e) -> {
            processControl.setVisible(true);
            processControl.setManaged(true);
            mediaControl.setVisible(false);
            mediaControl.setManaged(false);
        });

        audioList.setOnMouseClicked((e) -> {
            processControl.setVisible(false);
            processControl.setManaged(false);
            mediaControl.setVisible(true);
            mediaControl.setManaged(true);
        });

//        audioList.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
//            @Override
//            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
//                System.out.println(newValue);
//            }
//        });
    }

    private void startWatcher() {
        try {
            watcher = new FileWatcher(this, Global.APP_AUDIO_PATH);
            watcher.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
