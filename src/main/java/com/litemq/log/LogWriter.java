package main.java.com.litemq.log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicLong;

public class LogWriter {
    private static final String LOG_DIR = "logs";
    private static final long MAX_LOG_FILE_SIZE = 10L * 1024 * 1024;
    private final String logDir;
    private final long maxLogFileSize;
    private AtomicLong messageId = new AtomicLong();
    private File logFile;
    private BufferedWriter writer;

    public LogWriter() throws IOException {
        this(LOG_DIR, MAX_LOG_FILE_SIZE);
    }

    public LogWriter(String logDir, long maxLogFileSize) throws IOException {
        this.logDir = logDir;
        this.maxLogFileSize = maxLogFileSize;
        Files.createDirectories(Paths.get(this.logDir));
        rotateLogFile();
    }

    private void rotateLogFile() throws IOException {
        if(writer != null ) {
            writer.close();
        }
        logFile = new File(logDir, "log-" + System.currentTimeMillis()+".txt");
        System.out.println("Creating log file at: " + logFile.getAbsolutePath());
        writer = new BufferedWriter(new FileWriter(logFile,true));
    }

    public synchronized void writeMessage(String topic ,String payload) throws IOException {
        if (logFile.length() >= maxLogFileSize) {
            rotateLogFile();
        }

        String logEntry = Instant.now().getEpochSecond() + " "+ messageId.getAndIncrement() + " " + topic + " " + payload;
        writer.write(logEntry);
        writer.newLine();
        writer.flush();
    }

    public void close() throws IOException {
        if(writer!= null){
            writer.close();
        }
    }

}

