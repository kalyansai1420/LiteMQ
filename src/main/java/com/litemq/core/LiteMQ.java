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
    private static LiteMQ instance;
    private final LogWriter logWriter;
    private static final SubscriptionManager subscriptionManager = new SubscriptionManager();
    private final ExecutorService executorService;
    private final List<Consumer> activeConsumers;

    private LiteMQ() throws IOException {
        this.logWriter = new LogWriter(LOG_DIR, 1024L * 1024);
        this.executorService = Executors.newFixedThreadPool(5);
        this.activeConsumers = new ArrayList<>();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                shutdown();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }));
    }

    public static synchronized LiteMQ getInstance() throws IOException { // 🔥 Singleton Accessor
        if (instance == null) {
            instance = new LiteMQ();
        }
        return instance;
    }

    public Producer createProducer() {
        return new Producer(logWriter, subscriptionManager);
    }

    public Consumer createConsumer(String topic) throws IOException {
        Consumer consumer = new Consumer(topic);
        subscriptionManager.subscribe(topic, consumer);
        System.out.println("[LiteMQ] ✅ Consumer successfully subscribed to topic: " + topic);
        System.out.println("[LiteMQ] 🔥 DEBUG: Total subscribers in SubscriptionManager: "
                + subscriptionManager.getSubscribers(topic));

        activeConsumers.add(consumer);
        return consumer;
    }

    public static SubscriptionManager getSubscriptionManager() {
        return subscriptionManager;
    }

    public void shutdown() throws IOException {
        System.out.println("[LiteMQ] Graceful shutdown initiated....");
        for (Consumer consumer : activeConsumers) {
            consumer.close();
        }
        logWriter.close();
        executorService.shutdown();
        System.out.println("[LiteMQ] Shutdown completed");
    }
}
