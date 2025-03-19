package com.litemq.tests;

import java.io.IOException;

import com.litemq.log.LogReader;
import com.litemq.log.LogWriter;

public class LogFileManualTest {
    public static void main(String[] args) throws IOException {
        System.out.println("Starting manual log test...");

        // Initialize LogWriter
        LogWriter logWriter = new LogWriter("logs", 1024L);
        logWriter.writeMessage("debug-topic", "This is a debug message.");
        logWriter.flushBuffer();
        logWriter.close();
        System.out.println("[ManualTest] Log message written.");

        // Initialize LogReader
        LogReader logReader = new LogReader("logs", null);
        String message = logReader.readNextMessage();
        System.out.println("[ManualTest] Read message: " + message);
        logReader.close();
    }
}
