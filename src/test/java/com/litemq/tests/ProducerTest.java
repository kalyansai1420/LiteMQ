package com.litemq.tests;

import com.litemq.core.Producer;
import com.litemq.log.LogWriter;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ProducerTest {
    private static final String TEST_LOG_DIR = "test-logs";
    private LogWriter logWriter;
    private Producer producer;

    @BeforeEach
    void setUp() throws IOException {
        logWriter = new LogWriter(TEST_LOG_DIR, 1024L * 1024);
        producer = new Producer(logWriter, null);
    }

    @AfterEach
    void tearDown() throws IOException {
        logWriter.close();
    }

    @Test
    void testProducerWritesMessageToLog() throws IOException {
        producer.sendMessage("test-topic", "Logging Test Message");

        // Verify log file exists
        assertTrue(Files.list(Paths.get(TEST_LOG_DIR))
                .anyMatch(path -> path.toString().endsWith(".log")));

        // Verify log contains the expected message
        String logContent = new String(Files.readAllBytes(Files.list(Paths.get(TEST_LOG_DIR))
                .filter(path -> path.toString().endsWith(".log"))
                .findFirst()
                .orElseThrow(() -> new IOException("Log file not found"))));

        assertTrue(logContent.contains("test-topic Logging Test Message"));
    }
}
