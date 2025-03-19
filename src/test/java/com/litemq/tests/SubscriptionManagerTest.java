package com.litemq.tests;

import com.litemq.core.Consumer;
import com.litemq.core.Producer;
import com.litemq.core.SubscriptionManager;
import com.litemq.log.LogWriter;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;
import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class SubscriptionManagerTest {
    private static final String TEST_LOG_DIR = "logs"; // Ensure correct log path
    private SubscriptionManager subscriptionManager;
    private Producer producer;
    private LogWriter logWriter;

    @BeforeEach
    void setUp() throws IOException, InterruptedException {
        logWriter = new LogWriter("logs", 1024L * 1024);
        subscriptionManager = new SubscriptionManager();
        producer = new Producer(logWriter, subscriptionManager);

        // Ensure a log file exists before Consumer tries to read
        producer.sendMessage("test-topic", "Initial Log Message");
        logWriter.flushBuffer();
        TimeUnit.MILLISECONDS.sleep(100);
    }

    @AfterEach
    void tearDown() throws IOException {
        logWriter.close();
        // Clean up log files after each test
        Files.list(Paths.get(TEST_LOG_DIR)).forEach(path -> path.toFile().delete());
    }

    @Test
    void testProducerSendsMessageToConsumer() throws InterruptedException, IOException {
        Consumer consumer = new Consumer("logs/");
        subscriptionManager.subscribe("test-topic", consumer);

        producer.sendMessage("test-topic", "Hello, World!");

        TimeUnit.MILLISECONDS.sleep(100); // Ensure message is processed

        // Replace receiverMessage() with consumeMessage()
        assertEquals("Hello, World!", consumer.consumeMessage());
    }

}
