package org.audiotts.classes;

import org.audiotts.controller.MainController;
import org.audiotts.sonic.Sonic;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

public class SonicPlayer {

    private MainController controller;
    private Status status;
    private AudioInputStream stream;
    private AudioFormat format;
    private SourceDataLine line;
    private Thread songThread;
    private int sampleRate;
    private int numChannels;
    private float speed = 1.0f;
    private String path;
    private float timeSum = 0.0f;

    public enum Status {
        PLAY, PAUSE, STOP
    }

    public SonicPlayer(MainController controller) {
        this.controller = controller;
        this.status = Status.STOP;
    }

    public void loadFile(String path) throws IOException, UnsupportedAudioFileException, LineUnavailableException {
        this.path = path;
        timeSum = 0;
        stream = AudioSystem.getAudioInputStream(new File(path));

        format = stream.getFormat();
        sampleRate = (int)format.getSampleRate();
        numChannels = format.getChannels();

        SourceDataLine.Info info = new DataLine.Info(SourceDataLine.class, format, ((int)stream.getFrameLength()*format.getFrameSize()));
        line = (SourceDataLine)AudioSystem.getLine(info);
        line.open(stream.getFormat());
        line.start();
        status = Status.PLAY;
        stream.mark(Integer.MAX_VALUE);
        startThread();
    }

    public void play() {
        if (status == Status.PAUSE) {
            status = Status.PLAY;
            line.start();
        }
    }

    public void pause() {
        if (status == Status.PLAY) {
            status = Status.PAUSE;
            line.stop();
        }
    }

    public void stop() throws IOException {
        status = Status.STOP;
        line.stop();
//        line.drain();
        stream.close();
    }

    public void setPosition(float amount) throws IOException {
        if (status == Status.PLAY) {
            long totalBytes = stream.getFrameLength();
            float totalSeconds = (stream.getFrameLength() / stream.getFormat().getFrameRate());
            float current = (line.getLongFramePosition() / (line.getFormat().getFrameRate() * speed));
            float current2 = current + timeSum;
            if (amount < 0 && current2+amount < 0) {
                amount = -current2;
            }
            float offset = (((amount + current2) * totalBytes) / totalSeconds);
            timeSum += amount;

            stream.reset();
            stream.skip((long) (offset*2));
        }
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    private void startThread() {
        songThread = new Thread(() -> {
            Sonic sonic = new Sonic(sampleRate, numChannels);
            int bufferSize = line.getBufferSize();
            byte inBuffer[] = new byte[bufferSize];
            byte outBuffer[] = new byte[bufferSize];
            int numRead = 1, numWritten;

            sonic.setSpeed(speed);
            sonic.setPitch(1.0f);
            sonic.setRate(1.0f);
            sonic.setVolume(1.0f);
            sonic.setChordPitch(false);
            sonic.setQuality(0);

            do {
                if (status == Status.PLAY) {
                    try {
                        numRead = stream.read(inBuffer, 0, bufferSize);
                        sonic.setSpeed(speed);
                        if (numRead <= 0) {
                            sonic.flushStream();
                        } else {
                            sonic.writeBytesToStream(inBuffer, numRead);
                        }
                        do {
                            numWritten = sonic.readBytesFromStream(outBuffer, bufferSize);
                            if (numWritten > 0) {
                                line.write(outBuffer, 0, numWritten);
                            }
                        } while (numWritten > 0 && status != Status.STOP);
                    } catch (IOException e) {
                        e.printStackTrace();
                        break;
                    }
                } else {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            } while(numRead > 0 && status != Status.STOP);
        });;
        songThread.start();
    }

    public Status getStatus() {
        return status;
    }
}
