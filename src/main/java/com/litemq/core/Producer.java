package com.litemq.core;

import java.io.IOException;
import java.util.List;

import com.litemq.log.LogWriter;

public class Producer {

    private final LogWriter logWriter;
    private final SubscriptionManager subscriptionManager;

    public Producer(LogWriter logWriter, SubscriptionManager subscriptionManager) {
        this.logWriter = logWriter;
        this.subscriptionManager = subscriptionManager;
    }

    public void sendMessage(String topic, String message) throws IOException {
        System.out.println("[Producer] Attempting to send message: " + message);

        List<Consumer> consumers = subscriptionManager.getSubscribers(topic);
        System.out.println("[Producer] DEBUG: Consumers found for " + topic + ": " + consumers.size());

        if (consumers.isEmpty()) {
            System.out.println("[Producer] No consumers available for topic: " + topic + ". Storing message.");
            subscriptionManager.notifySubscribers(topic, message);
            return;
        }

        logWriter.writeMessage(topic, message);
        subscriptionManager.notifySubscribers(topic, message);
        System.out.println("[Producer] Message sent: " + message);
    }

}
