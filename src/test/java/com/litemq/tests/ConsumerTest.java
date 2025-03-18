package com.litemq.tests;

import com.litemq.core.Consumer;
import com.litemq.log.LogWriter;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ConsumerTest {
    private static final String TEST_LOG_DIR = "test-logs";
    private LogWriter logWriter;
    private Consumer consumer;

    @BeforeEach
    void setUp() throws IOException {
        logWriter = new LogWriter(TEST_LOG_DIR, 1024L * 1024);
        logWriter.writeMessage("consumer-topic", "Test Consumer Message");
        logWriter.flushBuffer();
        logWriter.close();

        consumer = new Consumer(TEST_LOG_DIR);
    }

    @AfterEach
    void tearDown() throws IOException {
        Files.list(Paths.get(TEST_LOG_DIR)).forEach(path -> path.toFile().delete());
    }

    @Test
    void testConsumeMessage() throws IOException {
        String message = consumer.consumeMessage();
        assertNotNull(message);
        assertTrue(message.contains("Test Consumer Message"));
    }
}
