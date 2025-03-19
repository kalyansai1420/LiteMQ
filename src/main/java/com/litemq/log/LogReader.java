package com.litemq.log;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.Comparator;
import java.io.File;
import java.io.FileNotFoundException;

public class LogReader {
    private static final int BUFFER_SIZE = 8192;
    private final String topic;
    private final String logDir;
    private final String offsetFilePath;
    private ByteBuffer buffer;
    private FileChannel fileChannel;
    private long currentOffset = 0;

    public LogReader(String logDir, String topic) throws IOException {
        this.topic = topic;
        this.logDir = logDir;
        this.offsetFilePath = "offsets/" + topic + ".offset";

        File logFile = getLatestLogFile(logDir);
        if (logFile == null) {
            throw new FileNotFoundException("No log files found for topic: " + topic);
        }
        this.currentOffset = readSavedOffset();
        fileChannel = FileChannel.open(logFile.toPath(), StandardOpenOption.READ);
        fileChannel.position(currentOffset);
        buffer = ByteBuffer.allocate(BUFFER_SIZE);
    }

    private File getLatestLogFile(String logDir) {
        File directory = new File(logDir);
        File[] logFiles = directory.listFiles((dir, name) -> name.startsWith("log-") && name.endsWith(".log"));

        if (logFiles == null || logFiles.length == 0) {
            return null;
        }

        // Sort log files by timestamp (latest first)
        Arrays.sort(logFiles, Comparator.comparingLong(File::lastModified).reversed());
        for (File file : logFiles) {
            if (file.length() > 0) { // Ensure the file is not empty
                System.out.println("[LogReader] Selected log file: " + file.getName());
                return file;
            }
        }
        return logFiles[0]; // Pick the latest log file
    }

    private long readSavedOffset() {
        try {
            File offsetFile = new File(offsetFilePath);
            if (offsetFile.exists()) {
                String offsetStr = new String(Files.readAllBytes(offsetFile.toPath())).trim();
                return Long.parseLong(offsetStr);
            }
        } catch (IOException | NumberFormatException e) {
            System.out.println("[LogReader] No valid offset found.Starting from beginning");
        }
        return 0;
    }

    private void saveOffset(long offset) {
        try {
            File offsetFile = new File(offsetFilePath);
            offsetFile.getParentFile().mkdirs(); // Ensure the directory exists
            Files.write(Paths.get(offsetFilePath), String.valueOf(offset).getBytes(),
                    StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            System.err.println("[LogReader] Failed to save offset: " + e.getMessage());
        }
    }

    public String readNextMessage() throws IOException {
        System.out.println("[LogReader] Reading from offset: " + currentOffset);
        if (fileChannel.read(buffer) == -1) {
            System.out.println("[LogReader] No more messages to read.");
            return null; // No more messages
        }
        buffer.flip();
        byte[] data = new byte[buffer.remaining()];
        buffer.get(data);
        buffer.clear();
        currentOffset = fileChannel.position();
        saveOffset(currentOffset);
        String message = new String(data);
        System.out.println("[LogReader] Read message: " + message);
        return message;
    }

    public void close() throws IOException {
        if (fileChannel != null) {
            fileChannel.close();
        }
    }
}
