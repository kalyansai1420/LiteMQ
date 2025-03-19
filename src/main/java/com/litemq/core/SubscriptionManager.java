package com.litemq.core;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class SubscriptionManager {
    private final Map<String, CopyOnWriteArrayList<Consumer>> topicSubscribers;
    private final Map<String, AtomicInteger> topicRoundRobinIndex;
    private final Map<String, Queue<String>> messageBuffer;

    public SubscriptionManager() {
        this.topicSubscribers = new ConcurrentHashMap<>();
        this.topicRoundRobinIndex = new ConcurrentHashMap<>();
        this.messageBuffer = new ConcurrentHashMap<>();
    }

    public void subscribe(String topic, Consumer consumer) {
        topicSubscribers.computeIfAbsent(topic, k -> new CopyOnWriteArrayList<>()).add(consumer);
        topicRoundRobinIndex.putIfAbsent(topic, new AtomicInteger(0));

        System.out.println("[SubscriptionManager] Consumer subscribed to topic: " + topic);
        System.out.println("[SubscriptionManager] 🔎 DEBUG: Total subscribers for " + topic + " → "
                + topicSubscribers.get(topic).size());

        System.out.println("[SubscriptionManager] Current Consumers for " + topic + ": "
                + topicSubscribers.getOrDefault(topic, new CopyOnWriteArrayList<>()));

        // If there are buffered messages, deliver them now
        Queue<String> bufferedMessages = messageBuffer.get(topic);
        if (bufferedMessages != null) {
            while (!bufferedMessages.isEmpty()) {
                String message = bufferedMessages.poll();
                consumer.receiveMessage(message);
                System.out.println("[SubscriptionManager] Delivered buffered message to new consumer: " + message);

            }
        }
        System.out.println("[SubscriptionManager] All registered topics and consumers: " + topicSubscribers);

    }

    public void unsubscribe(String topic, Consumer consumer) {
        List<Consumer> subscribers = topicSubscribers.get(topic);
        if (subscribers != null) {
            subscribers.remove(consumer);
            if (subscribers.isEmpty()) {
                topicSubscribers.remove(topic);
                topicRoundRobinIndex.remove(topic);
            }
        }
    }

    public void notifySubscribers(String topic, String message) throws IOException {
        List<Consumer> subscribers = topicSubscribers.getOrDefault(topic, new CopyOnWriteArrayList<>());
        System.out.println("[SubscriptionManager] 🔥 DEBUG: Notifying Subscribers for " + topic);
        System.out.println("[SubscriptionManager] 🔥 DEBUG: Current Subscribers → " + subscribers);

        if (subscribers.isEmpty()) {
            System.out.println("[SubscriptionManager] ❌ No consumers available for topic: " + topic);
            messageBuffer.computeIfAbsent(topic, k -> new LinkedList<>()).add(message);
            return;
        }

        int index = topicRoundRobinIndex.get(topic).getAndIncrement() % subscribers.size();
        Consumer selectedConsumer = subscribers.get(index);
        selectedConsumer.receiveMessage(message);
        System.out.println("[SubscriptionManager] ✅ Assigned message to Consumer #" + index);
    }

    public List<Consumer> getSubscribers(String topic) {
        return topicSubscribers.getOrDefault(topic, new CopyOnWriteArrayList<>());
    }

}
