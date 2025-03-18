package com.litemq.benchmark;

import org.openjdk.jmh.annotations.*;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import com.litemq.log.LogWriter;

@BenchmarkMode(Mode.Throughput) // Measures messages per second
@State(Scope.Thread)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
public class ProducerBenchmark {
    private LogWriter logWriter;

    @Setup(Level.Iteration)
    public void setup() throws IOException {
        logWriter = new LogWriter("benchmark_logs", 10L * 1024 * 1024);
    }

    @Benchmark
    public void writeMessage() throws IOException {
        logWriter.writeMessage("benchmark_topic", "Test message payload");
    }

    @TearDown(Level.Iteration)
    public void teardown() throws IOException {
        logWriter.close();
    }
}

