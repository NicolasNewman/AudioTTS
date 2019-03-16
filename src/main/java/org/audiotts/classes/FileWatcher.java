package org.audiotts.classes;

import org.audiotts.application.Global;
import org.audiotts.controller.MainController;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;

public class FileWatcher extends Thread {

    private MainController controller;
    private WatchService watchService;
    private boolean shutdown = false;

    public FileWatcher(MainController controller, String pathStr) throws IOException {
        this.controller = controller;
        watchService = FileSystems.getDefault().newWatchService();
        Path path = Paths.get(pathStr);
        path.register(watchService,
                StandardWatchEventKinds.ENTRY_CREATE,
                StandardWatchEventKinds.ENTRY_MODIFY,
                StandardWatchEventKinds.ENTRY_DELETE);
    }

    public void run() {
        try {
            startWatcher();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void startWatcher() throws InterruptedException {
        WatchKey watchKey;
        while ((watchKey = watchService.take()) != null && !shutdown) {
            for (WatchEvent<?> event : watchKey.pollEvents()) {
                controller.fillAudioList();
                System.out.println("Event kind: " + event.kind() + "\nFile efffected:" + event.context());
            }
            watchKey.reset();
        }
    }

    public void notifyShutdown() {
        shutdown = true;
    }
}
