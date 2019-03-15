package org.audiotts.classes;

import com.google.cloud.texttospeech.v1.*;
import com.google.protobuf.ByteString;
import org.audiotts.application.Global;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class TTSManager {

    private VoiceSelectionParams voice;
    private AudioConfig audioConfig;

    public TTSManager() {
        voice = VoiceSelectionParams.newBuilder()
                .setLanguageCode("en-US")
                .setSsmlGender(SsmlVoiceGender.NEUTRAL)
                .build();
        audioConfig = AudioConfig.newBuilder()
                .setAudioEncoding(AudioEncoding.MP3)
                .build();
    }

    public void processText(String input, String name) {
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
            } catch (Exception e) {
                System.out.println(e);
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
