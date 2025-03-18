package com.litemq.benchmark;

import org.openjdk.jmh.annotations.*;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import com.litemq.log.LogReader;

@BenchmarkMode(Mode.Throughput) // Measures messages per second
@State(Scope.Thread)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
public class ConsumerBenchmark {
    private LogReader logReader;

    @Setup(Level.Iteration)
    public void setup() throws IOException {
        logReader = new LogReader("benchmark_logs/log-1.txt");
    }

    @Benchmark
    public void readMessage() throws IOException {
        logReader.readNextMessage();
    }

    @TearDown(Level.Iteration)
    public void teardown() throws IOException {
        logReader.close();
    }
}

