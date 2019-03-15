package org.audiotts.classes;


import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

import java.io.File;
import java.net.URI;

public class AudioPlayer {

    private Media media;
    private MediaPlayer player;
    private Status status;

    public enum Status {
        PLAY, PAUSE, STOP
    }

    public AudioPlayer() {
        this.status = Status.STOP;
    }

    public void setFile(String file) {
        File f = new File(file);
        media = new Media(f.toURI().toString());
        player = new MediaPlayer(media);
        status = Status.PAUSE;
    }

    public void play() {
        if (status == Status.PAUSE) {
            player.play();
            status = Status.PLAY;
        }
    }

    public void pause() {
        if (status == Status.PLAY) {
            player.pause();
            status = Status.PAUSE;
        }
    }

    public void stop() {
        player.stop();
        status = Status.STOP;
    }

    public void setPosition(double amount) {
        if (status == Status.PLAY) {
            player.seek(Duration.seconds(player.getCurrentTime().toSeconds() + amount));
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
