package com.litemq.core;

import com.litemq.log.LogReader;
import java.io.IOException;
import java.nio.file.ClosedWatchServiceException;
import java.nio.file.FileSystems;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Consumer {
    private final String topic;
    private LogReader logReader;
    private WatchService watchService;
    private volatile boolean running = true;
    private final BlockingQueue<String> messageQueue;

    public Consumer(String topic) throws IOException {
        this.topic = topic;
        this.logReader = new LogReader("logs", topic);
        this.watchService = FileSystems.getDefault().newWatchService();
        Paths.get("logs/").register(watchService, StandardWatchEventKinds.ENTRY_MODIFY);
        this.messageQueue = new LinkedBlockingQueue<>();
        startWatcher();
        startProcessing();
    }

    private void startWatcher() {
        new Thread(() -> {
            while (running) {
                try {
                    WatchKey key = watchService.take();
                    for (WatchEvent<?> event : key.pollEvents()) {
                        if (event.kind() == StandardWatchEventKinds.ENTRY_MODIFY) {
                            String newMessage = logReader.readNextMessage();
                            if (newMessage != null) {
                                messageQueue.offer(newMessage); // Add log message to queue
                            }
                        }
                    }
                    key.reset();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    System.out.println("[Consumer] Watcher thread interrupted.");
                } catch (ClosedWatchServiceException e) {
                    System.out.println("[Consumer] WatchService closed.");
                    break;
                } catch (IOException e) {
                    System.err.println("[Consumer] Error reading new messages: " + e.getMessage());
                }
            }
        }).start();
    }

    /**
     * 🚀 New method for direct message delivery from SubscriptionManager
     */
    public void receiveMessage(String message) {
        System.out.println("[Consumer] 🔹 Received message via SubscriptionManager: " + message);

        messageQueue.offer(message);
    }

    private void startProcessing() {
        new Thread(() -> {
            while (running) {
                try {
                    String message = messageQueue.poll(); // Non-blocking check
                    if (message != null) {
                        System.out.println("[Consumer] Received: " + message);
                    } else {
                        Thread.sleep(2000); // Poll every 2 seconds if no message
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }).start();
    }

    public void close() throws IOException {
        running = false;
        logReader.close();
        watchService.close();
        System.out.println("[Consumer] Shutdown completed.");
    }
}
