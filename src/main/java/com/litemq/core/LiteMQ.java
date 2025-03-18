package com.litemq.core;

import com.litemq.log.LogWriter;
import com.litemq.log.LogReader;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.*;

public class LiteMQ {
    private static final String LOG_DIR = "logs/";
    private final LogWriter logWriter;
    private final SubscriptionManager subscriptionManager;
    private final ExecutorService executorService;

    public LiteMQ() throws IOException {
        this.logWriter = new LogWriter(LOG_DIR, 1024L * 1024);
        this.subscriptionManager = new SubscriptionManager();
        this.executorService = Executors.newFixedThreadPool(5);
    }

    public Producer createProducer() {
        return new Producer(logWriter,subscriptionManager);
    }

    public Consumer createConsumer(String topic) throws IOException {
        Consumer consumer = new Consumer(topic);
        subscriptionManager.subscribe(topic, consumer);
        return consumer;
    }

    public void shutdown() throws IOException {
        logWriter.close();
        executorService.shutdown();
    }
}
