package com.litemq.tests;

import com.litemq.core.Consumer;
import com.litemq.core.LiteMQ;

import java.io.IOException;
import java.util.Scanner;

public class ConsumerTestApp {
    public static void main(String[] args) {
        try {
            LiteMQ liteMQ = LiteMQ.getInstance();
            Consumer consumer = liteMQ.createConsumer("test-topic");
            System.out.println("[ConsumerTestApp] Consumer subscribed to topic: test-topic");

            System.out.println("[ConsumerTestApp] Consumer started. Press ENTER to stop...");
            Scanner scanner = new Scanner(System.in);
            scanner.nextLine();

            System.out.println("[LiteMQ] Graceful shutdown initiated....");
            consumer.close();
            liteMQ.shutdown();
            System.out.println("[LiteMQ] Shutdown completed");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
