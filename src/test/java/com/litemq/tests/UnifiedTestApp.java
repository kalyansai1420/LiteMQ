package com.litemq.tests;

import java.io.IOException;

import com.litemq.core.Consumer;
import com.litemq.core.LiteMQ;
import com.litemq.core.Producer;

public class UnifiedTestApp {
    public static void main(String[] args) throws IOException {
        LiteMQ mq = LiteMQ.getInstance();

        // Start consumer
        Consumer consumer = mq.createConsumer("test-topic");

        // Start producer
        Producer producer = mq.createProducer();
        producer.sendMessage("test-topic", "Hello from same JVM!");

        // Wait for consumer to receive message
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        mq.shutdown();
    }
}
