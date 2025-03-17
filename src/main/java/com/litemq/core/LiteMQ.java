package main.java.com.litemq.core;

import main.java.com.litemq.log.LogWriter;
import main.java.com.litemq.log.LogReader;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LiteMQ {
    private static final String LOG_DIR = "logs/";
    private final LogWriter logWriter;
    private final LogReader logReader;
    private final ExecutorService executorService;

    public LiteMQ() throws IOException {
        this.logWriter = new LogWriter(LOG_DIR, 1024L * 1024);
        this.logReader = new LogReader(LOG_DIR);
        this.executorService = Executors.newFixedThreadPool(5);
    }

    public Producer createProducer() {
        return new Producer(logWriter);
    }

    public Consumer createConsumer() {
        return new Consumer(logReader);
    }

    public void shutdown() throws IOException {
        logWriter.close();
        logReader.close();
        executorService.shutdown();
    }
}
