package com.litemq.log;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicLong;

public class LogWriter {
    private static final String LOG_DIR = "logs";
    private static final long MAX_LOG_FILE_SIZE = 10L * 1024 * 1024; // 10MB
    private static final int BUFFER_SIZE = 8192;
    private static final int FORCE_EVERY_N_WRITES = 5; // 🔥 Reduce flush interval for safety

    private final String logDir;
    private final long maxLogFileSize;
    private AtomicLong messageId = new AtomicLong();
    private File logFile;
    private FileChannel fileChannel;
    private ByteBuffer buffer;
    private int writeCount = 0;

    public LogWriter() throws IOException {
        this(LOG_DIR, MAX_LOG_FILE_SIZE);
    }

    public LogWriter(String logDir, long maxLogFileSize) throws IOException {
        this.logDir = logDir;
        this.maxLogFileSize = maxLogFileSize;

        File logDirFile = new File(logDir);
        if (!logDirFile.exists() && !logDirFile.mkdirs()) {
            throw new IOException("Failed to create log directory: " + logDir);
        }
        if (!logDirFile.canWrite()) {
            throw new IOException("Log directory is not writable: " + logDir);
        }

        this.buffer = ByteBuffer.allocate(BUFFER_SIZE);
        rotateLogFile(); // 🔥 Ensure a log file is created
    }

    private void rotateLogFile() throws IOException {
        try {
            if (fileChannel != null && fileChannel.isOpen()) {
                flushBuffer();
                fileChannel.close();
            }
        } catch (IOException e) {
            System.err.println("Failed to close previous log file: " + e.getMessage());
        }

        logFile = new File(logDir, "log-" + System.currentTimeMillis() + ".log");

        if (!logFile.exists()) {
            boolean created = logFile.createNewFile(); // 🔥 Ensure the file exists
            if (created) {
                System.out.println("[LogWriter] Created new log file: " + logFile.getAbsolutePath());
            } else {
                System.err.println("[LogWriter] Failed to create log file!");
            }
        }

        fileChannel = new FileOutputStream(logFile, true).getChannel();
    }

    public synchronized void writeMessage(String topic, String payload) throws IOException {
        String logEntry = Instant.now().getEpochSecond() + " " + messageId.getAndIncrement() + " " + topic + " " + payload + "\n";
        byte[] data = logEntry.getBytes();

        if (logFile.length() + data.length >= maxLogFileSize) {
            rotateLogFile();
        }

        if (buffer.remaining() < data.length) {
            flushBuffer();
        }

        buffer.put(data);
        System.out.println("[LogWriter] Message written to buffer: " + logEntry);
        
        flushBuffer(); // 🔥 Immediately flush after every write
    }

    public void flushBuffer() throws IOException {
        if (fileChannel == null || !fileChannel.isOpen()) {
            System.out.println("FlushBuffer called on closed file.");
            return;
        }
        buffer.flip();
        while (buffer.hasRemaining()) {
            fileChannel.write(buffer);
        }
        fileChannel.force(true); // 🔥 Ensure write to disk
        buffer.clear();
    }

    public void close() throws IOException {
        if (fileChannel != null && fileChannel.isOpen()) {
            flushBuffer();
            fileChannel.close();
            fileChannel = null;
            System.out.println("LogWriter closed successfully.");
        } else {
            System.out.println("LogWriter already closed.");
        }
    }
}
