package com.litemq.benchmark;

import org.openjdk.jmh.annotations.*;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import com.litemq.log.LogWriter;
import com.litemq.log.LogReader;

@BenchmarkMode(Mode.AverageTime) // Measures latency per message
@State(Scope.Thread)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
public class EndToEndBenchmark {
    private LogWriter logWriter;
    private LogReader logReader;

    @Setup(Level.Iteration)
    public void setup() throws IOException {
        logWriter = new LogWriter("benchmark_logs", 10L * 1024 * 1024);
        logReader = new LogReader("benchmark_logs/log-1.txt");
    }

    @Benchmark
    public void produceConsumeMessage() throws IOException {
        logWriter.writeMessage("benchmark_topic", "Test message payload");
        logReader.readNextMessage();
    }

    @TearDown(Level.Iteration)
    public void teardown() throws IOException {
        logWriter.close();
        logReader.close();
    }
}

