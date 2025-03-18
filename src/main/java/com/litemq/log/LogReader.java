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
    private FileChannel fileChannel;
    private ByteBuffer buffer;
    private static final int BUFFER_SIZE = 8192;

    public LogReader(String logDir, String topic) throws IOException {
        File logFile = getLatestLogFile(logDir);
        if (logFile == null) {
            throw new FileNotFoundException("No log files found for topic: " + topic);
        }
        fileChannel = FileChannel.open(logFile.toPath(), StandardOpenOption.READ);
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
        return logFiles[0]; // Pick the latest log file
    }

    public String readNextMessage() throws IOException {
        if (fileChannel.read(buffer) == -1) {
            return null; // No more messages
        }
        buffer.flip();
        byte[] data = new byte[buffer.remaining()];
        buffer.get(data);
        buffer.clear();
        return new String(data);
    }

    public void close() throws IOException {
        if (fileChannel != null) {
            fileChannel.close();
        }
    }
}
