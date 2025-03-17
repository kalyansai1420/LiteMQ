package main.java.com.litemq.core;

import java.io.IOException;

import main.java.com.litemq.log.LogReader;

public class Consumer {
    private final LogReader logReader;

    public Consumer(LogReader logReader) {
        this.logReader = logReader;
    }

    public synchronized String consumeMessage() throws IOException {
        String message = logReader.readNextMessage();
        if( message != null) {
            acknowledgeMessage();
        }
        return message;
    }

    private void acknowledgeMessage() {
        System.out.println("Message acknowledge");
    }
}
