package com.litemq.tests;

import java.io.IOException;

import com.litemq.core.Consumer;
import com.litemq.core.LiteMQ;

public class MultiConsumerTestApp {
    public static void main(String[] args) throws IOException {
        LiteMQ liteMQ = new LiteMQ();

        Consumer consumer1 = liteMQ.createConsumer("test-topic");
        Consumer consumer2 = liteMQ.createConsumer("test-topic");

        Thread t1 = new Thread(() -> {
            try {
                consumer1.consumeMessage();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        Thread t2 = new Thread(() -> {
            try {
                consumer2.consumeMessage();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        t1.start();
        t2.start();

        // Add shutdown hooks
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                consumer1.close();
                consumer2.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }));
    }
}

