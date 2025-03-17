package com.litemq.core;

import java.io.IOException;

import com.litemq.log.LogWriter;

public class Producer {

    private final LogWriter logWriter;

    public Producer(LogWriter logWriter) {
        this.logWriter = logWriter;
    }

    public synchronized void sendMessage(String topic, String message) throws IOException {
        logWriter.writeMessage(topic, message);
    }

}
