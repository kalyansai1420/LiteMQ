package com.litemq.tests;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.litemq.log.LogWriter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import static org.junit.jupiter.api.Assertions.*;

public class LogWriterTest {
    private static final String TEST_LOG_DIR = "test-logs";
    private LogWriter logWriter;

    @BeforeEach
    void setUp() throws IOException {
        Files.createDirectories(Paths.get(TEST_LOG_DIR));
        logWriter = new LogWriter(TEST_LOG_DIR, 1024L * 1024);
    }

    @AfterEach
    void tearDown() throws IOException {
        logWriter.close();
        File logDir = new File(TEST_LOG_DIR);
        for (File file : logDir.listFiles()) {
            file.delete();
        }
        logDir.delete();
    }

    @Test
    void testWriteMessage() throws IOException {
        logWriter.writeMessage("test-topic", "Hello, JUnit!");
        logWriter.flushBuffer();

        File logDir = new File(TEST_LOG_DIR);
        File[] logFiles = logDir.listFiles((dir, name) -> name.startsWith("log-") && name.endsWith(".log"));

        assertNotNull(logFiles);
        assertEquals(1, logFiles.length);

        String logContent = new String(Files.readAllBytes(logFiles[0].toPath()));
        assertTrue(logContent.contains("test-topic Hello, JUnit!"));
    }

    @Test
    void testRotateLogFile() throws IOException {
        for (int i = 0; i < 1000; i++) {
            logWriter.writeMessage("rotate-test", "Msg " + i);
        }
        logWriter.flushBuffer();

        File logDir = new File(TEST_LOG_DIR);
        File[] logFiles = logDir.listFiles((dir, name) -> name.startsWith("log-") && name.endsWith(".log"));

        assertNotNull(logFiles);
        assertTrue(logFiles.length > 0); // At least one log file should exist
    }
}
