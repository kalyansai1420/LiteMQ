package com.litemq.tests;

import com.litemq.core.Consumer;
import com.litemq.core.LiteMQ;
import com.litemq.core.Producer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.IOException;
import java.nio.file.*;
import static org.junit.jupiter.api.Assertions.*;

public class LiteMQTest {
    private static final String LOG_DIR = "logs";
    private static final String TEST_TOPIC = "test-topic";
    private static final Path LOG_FILE_PATH = Paths.get(LOG_DIR, TEST_TOPIC + ".log");

    private LiteMQ liteMQ;
    private Producer producer;
    private Consumer consumer;

    @BeforeEach
    void setUp() throws IOException {
        // Ensure logs directory exists
        Files.createDirectories(Paths.get(LOG_DIR));

        // Ensure test log file exists
        if (!Files.exists(LOG_FILE_PATH)) {
            Files.createFile(LOG_FILE_PATH);
        }

        liteMQ = new LiteMQ();
        producer = liteMQ.createProducer();
        consumer = liteMQ.createConsumer(TEST_TOPIC);
    }

    @AfterEach
    void tearDown() throws IOException {
        liteMQ.shutdown();
        // Cleanup: Delete test log files
        Files.deleteIfExists(LOG_FILE_PATH);
    }

    @Test
    void testEndToEndMessageFlow() throws IOException {
        producer.sendMessage(TEST_TOPIC, "Hello, LiteMQ!");

        String receivedMessage = consumer.consumeMessage();
        assertEquals("Hello, LiteMQ!", receivedMessage);
    }

    @Test
    void testMultipleConsumers() throws IOException {
        Consumer consumer2 = liteMQ.createConsumer(TEST_TOPIC);

        producer.sendMessage(TEST_TOPIC, "Message for multiple consumers");

        assertEquals("Message for multiple consumers", consumer.consumeMessage());
        assertEquals("Message for multiple consumers", consumer2.consumeMessage());
    }

    @Test
    void testConsumerDoesNotReceiveAfterUnsubscribing() throws IOException {
        liteMQ.createConsumer(TEST_TOPIC);
        liteMQ.shutdown(); // Simulate consumer stopping

        producer.sendMessage(TEST_TOPIC, "Message after unsubscribe");

        assertNull(consumer.consumeMessage()); // Should return null
    }
}
