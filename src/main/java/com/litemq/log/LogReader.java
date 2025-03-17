package main.java.com.litemq.log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Comparator;

public class LogReader {
    private final String logFilePath;
    private BufferedReader reader;

    public LogReader(String logFilePath) throws IOException {
        this.logFilePath = logFilePath;
        openFile();
    }

    private void openFile() throws IOException {
        File logDirFile = new File(logFilePath);
        File[] logFiles = logDirFile.listFiles((dir, name) -> name.startsWith("log-") && name.endsWith(".txt"));

        if (logFiles == null || logFiles.length == 0) {
            throw new FileNotFoundException("No log files found in directory: " + logFilePath);
        }

        // Select the most recent log file
        Arrays.sort(logFiles, Comparator.comparingLong(File::lastModified));
        String currentLogFile = logFiles[logFiles.length - 1].getAbsolutePath(); // Pick latest log file

        System.out.println("Opening log file: " + currentLogFile);
        reader = new BufferedReader(new FileReader(currentLogFile));
    }

    public String readNextMessage() throws IOException {
        String message = reader.readLine();
        if (message == null) {
            close();
        }
        return message;
    }

    public void close() throws IOException {
        if (reader != null) {
            reader.close();
        }
    }
}
