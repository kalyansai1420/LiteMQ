package com.litemq.core;

import com.litemq.log.LogReader;
import java.io.IOException;

public class Consumer {
    private final String topic;
    private LogReader logReader;

    public Consumer(String topic) throws IOException {
        this.topic = topic;
        this.logReader = new LogReader("logs", topic);
    }

    public String consumeMessage() throws IOException {
        return logReader.readNextMessage();
    }

    public void close() throws IOException {
        logReader.close();
    }
}
