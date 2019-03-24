package org.audiotts.classes;


import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;
import org.audiotts.controller.MainController;

import java.io.File;
import java.net.URI;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Deprecated
public class AudioPlayer {

    private MediaPlayer player;
    private Status status;
    private MainController mainController;
    private Timeline mediaDuration;
    private double maxProgress;

    public enum Status {
        PLAY, PAUSE, STOP
    }

    public AudioPlayer(MainController mainController) {
        this.mainController = mainController;
        this.status = Status.STOP;
    }

    public void setFile(String file) {
        File f = new File(file);
        Media media = new Media(f.toURI().toString());
        player = new MediaPlayer(media);

        mainController.getMediaPlayingControl().setVisible(true);
        mainController.getMediaPlayingControl().setManaged(true);
        mainController.getTextArea().setDisable(true);
        mainController.getListView().setDisable(true);

        player.setOnReady(() -> {
            mainController.getTimeEnd().setText(convertTimeToStr(player.getStopTime().toSeconds()));
            maxProgress = player.getStopTime().toSeconds();
        });

        player.setOnEndOfMedia(() -> {
            player.stop();
            status = Status.STOP;
            mediaDuration.stop();
            String time = convertTimeToStr(player.getCurrentTime().toSeconds());
            mainController.getTimeCurrent().setText(time);
            mainController.getPlayButton().setText("Play");
            mainController.getTextArea().setDisable(false);
            mainController.getListView().setDisable(false);
            mainController.getTimeProgress().setProgress(1f);
        });

        status = Status.PAUSE;

        mediaDuration = new Timeline(new KeyFrame(Duration.seconds(0.5), (e) -> {
            String time = convertTimeToStr(player.getCurrentTime().toSeconds());
            mainController.getTimeCurrent().setText(time);
            mainController.getTimeProgress().setProgress(player.getCurrentTime().toSeconds() / maxProgress);
        }));
        mediaDuration.setCycleCount(Animation.INDEFINITE);
        mediaDuration.play();
    }

    private String convertTimeToStr(double rawTime) {
        int time = (int) Math.ceil(rawTime);
        int minutes = time / 60;
        int seconds = time % 60;

        return minutes + ":" + (seconds < 10 ? ("0" + seconds) : seconds);

    }

    public void play() {
        if (status == Status.PAUSE) {
            player.play();
            player.setBalance(1.0);
            status = Status.PLAY;
            mediaDuration.play();
        }
    }

    public void pause() {
        if (status == Status.PLAY) {
            player.pause();
            status = Status.PAUSE;
            mediaDuration.pause();
        }
    }

    public void stop() {
        player.stop();
        status = Status.STOP;
        mainController.getTextArea().setDisable(false);
        mainController.getListView().setDisable(false);
        mediaDuration.stop();
    }

    public void setPosition(double amount) {
        if (status == Status.PLAY) {
            player.seek(Duration.seconds(player.getCurrentTime().toSeconds() + amount));
            String time = convertTimeToStr(player.getCurrentTime().toSeconds());
            mainController.getTimeCurrent().setText(time);
        }
    }

    public void setRate(double rate) {
        if (status == Status.PLAY) {
            player.setRate(rate);
        }
    }

    public Status getStatus() {
        return status;
    }
}
