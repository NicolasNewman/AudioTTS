package org.audiotts.classes;

import com.google.cloud.texttospeech.v1.*;
import com.google.protobuf.ByteString;
import javafx.application.Platform;
import org.audiotts.application.Global;
import org.audiotts.controller.MainController;

import java.io.FileOutputStream;
import java.io.OutputStream;

public class TTSManager {

    private VoiceSelectionParams voice;
    private AudioConfig audioConfig;
    private MainController mainController;

    public TTSManager(MainController mainController) {
        voice = VoiceSelectionParams.newBuilder()
                .setLanguageCode("en-US")
                .setSsmlGender(SsmlVoiceGender.NEUTRAL)
                .build();
        audioConfig = AudioConfig.newBuilder()
                .setAudioEncoding(AudioEncoding.MP3)
                .build();
        this.mainController = mainController;
    }

    public void processText(String input, String name) {
        new Thread(() -> {
            try (TextToSpeechClient textToSpeechClient = TextToSpeechClient.create()) {
                // Set the text input to be synthesized
                SynthesisInput inputSynthesis = SynthesisInput.newBuilder()
                        .setText(input)
                        .build();

                SynthesizeSpeechResponse response = textToSpeechClient.synthesizeSpeech(inputSynthesis, voice,
                        audioConfig);

                ByteString audioContents = response.getAudioContent();

                try (OutputStream out = new FileOutputStream(Global.APP_AUDIO_PATH + name + Global.AUDIO_FILE_TYPE)) {
                    out.write(audioContents.toByteArray());
                    System.out.println("Audio content written to file \"output.mp3\"");
                    Platform.runLater(() -> mainController.toggleProcess());
                } catch (Exception e) {
                    System.out.println(e);
                }
            } catch (Exception e) {
                System.out.println(e);
            }
        }).start();

    }
}
