package main.java.com.litemq.log;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class LogReader {
    private final String logFilePath;
    private BufferedReader reader;

    public LogReader(String logFilePath) throws IOException {
        this.logFilePath = logFilePath;
        openFile();
    }

    private void openFile() throws IOException {
        if(!Files.exists(Paths.get(logFilePath))) {
            throw new FileNotFoundException("Log file not found: "+ logFilePath);
        }
        reader = new BufferedReader(new FileReader(logFilePath));
    }

    public String readNextMessage() throws IOException {
        String message = reader.readLine();
        if (message == null) {
            close();
        }
        return message;
    }

    public void close() throws IOException {
        if(reader != null) {
            reader.close();
        }
    }
}
