package com.litemq.tests;

import com.litemq.core.LiteMQ;
import com.litemq.core.Producer;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class ProducerTestApp {
    public static void main(String[] args) throws IOException, InterruptedException {
        LiteMQ mq = LiteMQ.getInstance();
        Producer producer = mq.createProducer();

        // 🔹 Wait a few seconds to ensure LiteMQ initializes
        Thread.sleep(2000);

        Scanner scanner = new Scanner(System.in);
        System.out.println("[Producer] Enter messages (type 'exit' to stop):");

        while (true) {
            String message = scanner.nextLine();
            if (message.equalsIgnoreCase("exit"))
                break;

            producer.sendMessage("test-topic", message);
            System.out.println("[Producer] Message sent: " + message);
        }

        // 🔥 Verify if the log file was created
        File logDir = new File("logs");
        File[] logFiles = logDir.listFiles((dir, name) -> name.startsWith("log-") && name.endsWith(".log"));

        if (logFiles != null && logFiles.length > 0) {
            System.out.println("[ProducerTestApp] ✅ Log file found: " + logFiles[0].getAbsolutePath());
        } else {
            System.err.println("[ProducerTestApp] ❌ No log file found!");
        }

        mq.shutdown();
    }
}
