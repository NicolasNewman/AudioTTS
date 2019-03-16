package org.audiotts.application;
import java.io.File;

public class Global {

    public final static String APP_PATH = getOSPath();
    public final static String APP_AUDIO_PATH = APP_PATH + File.separator + "audio" + File.separator;
    public final static String APP_DATA_PATH = APP_PATH + File.separator + "data" + File.separator;
    public final static String AUDIO_FILE_TYPE = ".mp3";

    /**
     * Gets the game data path based on which OS the user is on
     * @return correct path for the game data
     */
    public static String getOSPath() {
        String OS = System.getProperty("os.name");
        if(OS.toLowerCase().contains("win")) {
            return System.getProperty("user.home") + File.separator + "AppData" + File.separator + "Roaming" + File.separator + "AudioTTS";
        } else if(OS.toLowerCase().contains("mac")) {
            return System.getProperty("user.home") + File.separator + "Library" + File.separator + "Application Support" + File.separator + "AudioTTS";
        }
        return null;
    }
}
