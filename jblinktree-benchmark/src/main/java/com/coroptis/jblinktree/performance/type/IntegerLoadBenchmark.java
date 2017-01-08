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
public class IntegerLoadBenchmark {

    private int cx = 0;

    private final TypeDescriptorInteger tdi = new TypeDescriptorInteger();

    private final byte[] field = new byte[4];

    public IntegerLoadBenchmark() {
    }

    @Benchmark
    @OperationsPerInvocation(1)
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    public void simple_valueOf() {
        Integer i = Integer.valueOf(cx);
        cx++;
    }

    @Benchmark
    @OperationsPerInvocation(1)
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    public void simple_boxing() {
        Integer i = cx;
        cx++;
    }
}
