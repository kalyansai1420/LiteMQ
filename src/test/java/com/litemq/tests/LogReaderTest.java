//package com.litemq.tests;
//
//import com.litemq.log.LogReader;
//import com.litemq.log.LogWriter;
//import org.junit.jupiter.api.*;
//
//import java.io.File;
//import java.io.IOException;
//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.nio.file.Paths;
//import java.io.FileReader;
//import java.io.BufferedReader;
//import java.io.FileNotFoundException;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//public class LogReaderTest {
//    private static final String TEST_LOG_DIR = "logs/test/";
//    private static final String TEST_LOG_FILE = TEST_LOG_DIR + "test-log.txt";
//    private LogWriter logWriter;
//
//    @BeforeEach
//    void setUp() throws IOException {
//        Files.createDirectories(Paths.get(TEST_LOG_DIR));
//        logWriter = new LogWriter(TEST_LOG_DIR, 1024);
//        logWriter.writeMessage("test_topic", "Message 1");
//        logWriter.writeMessage("test_topic", "Message 2");
//        logWriter.close();
//    }
//
//    @AfterEach
//    void tearDown() throws IOException {
//        Files.walk(Paths.get(TEST_LOG_DIR))
//                .filter(Files::isRegularFile)
//                .map(Path::toFile)
//                .forEach(File::delete);
//    }
//
//    @Test
//    void testReadMessages() throws IOException {
//        assertTrue(Files.exists(Paths.get(TEST_LOG_FILE)), "Test log file does not exist!");
//
//        // Read messages using LogReader
//        LogReader logReader = new LogReader(TEST_LOG_FILE);
//        assertEquals("Message 1", logReader.readNextMessage().split(" ", 4)[3]); // Extract payload
//        assertEquals("Message 2", logReader.readNextMessage().split(" ", 4)[3]);
//
//        // Ensure EOF returns null
//        assertNull(logReader.readNextMessage());
//
//        logReader.close();
//    }
//}
