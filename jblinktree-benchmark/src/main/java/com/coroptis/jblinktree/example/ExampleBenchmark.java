package com.coroptis.jblinktree.example;

import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OperationsPerInvocation;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
public class ExampleBenchmark {

    private Counter counter;

    public ExampleBenchmark() {
        counter = new Counter();
    }

    @Benchmark
    @OperationsPerInvocation(10)
    public void simpleTest() {
        System.out.println("." + counter.getI() + '\'');
        for (int i = 0; i < 10; i++) {
            counter.inc();
        }
    }

    @Setup
    public void setup() {
        System.out.println("setup counter: " + counter.getI());
    }

    @TearDown
    public void tearDown() {
        System.out.println("tear down counter: " + counter.getI());
    }

}
