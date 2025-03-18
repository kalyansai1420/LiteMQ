package com.litemq.core;


import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class SubscriptionManager {
    private final Map<String,CopyOnWriteArrayList<Consumer>> topicSubscribers;

    public SubscriptionManager() {
        this.topicSubscribers = new ConcurrentHashMap<>();
    }

    public void subscribe(String topic,Consumer consumer) {
        topicSubscribers.computeIfAbsent(topic, k -> new CopyOnWriteArrayList<>()).add(consumer);
    }

    public void unsubscribe(String topic, Consumer consumer) {
        List<Consumer> subscribers = topicSubscribers.get(topic);
        if (subscribers != null) {
            subscribers.remove(consumer);
            if (subscribers.isEmpty()) {
                topicSubscribers.remove(topic);
            }
        }
    }

    public void notifySubscribers(String topic, String message) throws IOException {
        List<Consumer> subscribers = topicSubscribers.getOrDefault(topic, new CopyOnWriteArrayList<>());
        for (Consumer consumer : subscribers) {
            consumer.consumeMessage(); // 🔹 Send message to each consumer
        }
    }

    public List<Consumer> getSubscribers(String topic) {
        return topicSubscribers.getOrDefault(topic, new CopyOnWriteArrayList<>());
    }
    
}
