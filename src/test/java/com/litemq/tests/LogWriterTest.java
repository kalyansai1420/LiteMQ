package com.litemq.tests;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.assertTrue;


import com.litemq.log.LogWriter;

public class LogWriterTest {
    private static final String TEST_LOG_DIR = "logs/test/";
    private LogWriter logWriter;

    @BeforeEach
    void setUp() throws IOException {
        Files.createDirectories(Paths.get(TEST_LOG_DIR));
        logWriter = new LogWriter(TEST_LOG_DIR, 1024);
    }

    @AfterEach
    void tearDown() throws IOException {
        Files.walk(Paths.get(TEST_LOG_DIR))
                .filter(Files::isRegularFile)
                .map(Path::toFile)
                .forEach(File::delete);
    }

    @Test
    void testWriteMessage() throws IOException {
        logWriter.writeMessage("orders", "{order_id: 1, amount: 50}");
        logWriter.writeMessage("payments", "{user: Alice, amount: 20}");

        Path logFile = Files.list(Paths.get(TEST_LOG_DIR)).findFirst().orElseThrow();
        String content = Files.readString(logFile);

        assertTrue(content.contains("orders"));
        assertTrue(content.contains("payments"));
    }

    @Test
    void testRotateLogFile() throws IOException {
        String largeMessage = "A".repeat(1024); // Exceed file limit

        logWriter.writeMessage("large_topic", largeMessage);
        logWriter.writeMessage("next_topic", "Next message"); // Should be in a new file

        long logFileCount = Files.list(Paths.get(TEST_LOG_DIR)).count();
        assertTrue(logFileCount > 1, "Log rotation did not occur");
    }

    @Test
    void testFileFlushAndPersistence() throws IOException {
        logWriter.writeMessage("persistent_topic", "Persistent message");

        // Read back and verify persistence
        Path logFile = Files.list(Paths.get(TEST_LOG_DIR)).findFirst().orElseThrow();
        String content = Files.readString(logFile);

        assertTrue(content.contains("persistent_topic"));
    }
}
