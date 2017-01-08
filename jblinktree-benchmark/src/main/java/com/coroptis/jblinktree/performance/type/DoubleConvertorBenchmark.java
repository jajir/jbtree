package com.coroptis.jblinktree.performance.type;

import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OperationsPerInvocation;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;

import com.coroptis.jblinktree.type.TypeDescriptorInteger;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
public class DoubleConvertorBenchmark {

    private double cx = 0.032;

    public DoubleConvertorBenchmark() {
    }

    @Benchmark
    @OperationsPerInvocation(1)
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    public void simple_valueOf() {
        String s = String.valueOf(cx);
        cx++;
        reset();
    }

    @Benchmark
    @OperationsPerInvocation(1)
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    public void simple_manuall() {
        int i = (int) cx;
        Integer.toString(i);
        cx++;
        reset();
    }

    private void reset() {
        if (cx >= 100) {
            cx = cx - 100;
        }
    }
}
