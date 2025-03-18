package com.litemq.tests;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.litemq.log.LogReader;
import com.litemq.log.LogWriter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import static org.junit.jupiter.api.Assertions.*;

public class LogReaderTest {
    private static final String TEST_LOG_DIR = "logs";
    private LogWriter logWriter;
    private LogReader logReader;

    @BeforeEach
    void setUp() throws IOException {
        Files.createDirectories(Paths.get(TEST_LOG_DIR));
        logWriter = new LogWriter(TEST_LOG_DIR, 1024L * 1024);
        logWriter.writeMessage("test-topic", "JUnit test message");
        logWriter.flushBuffer();
        logWriter.close();
        logReader = new LogReader(TEST_LOG_DIR, null);
    }

    @AfterEach
    void tearDown() throws IOException {
        logReader.close();
        File logDir = new File(TEST_LOG_DIR);
        for (File file : logDir.listFiles()) {
            file.delete();
        }
        logDir.delete();
    }

    @Test
    void testReadNextMessage() throws IOException {
        String message = logReader.readNextMessage();
        assertNotNull(message);
        assertTrue(message.contains("JUnit test message"));
    }

    @Test
    void testReadEndOfFile() throws IOException {
        logReader.readNextMessage(); // Read the only message
        String message = logReader.readNextMessage(); // Should return null
        assertNull(message);
    }
}
