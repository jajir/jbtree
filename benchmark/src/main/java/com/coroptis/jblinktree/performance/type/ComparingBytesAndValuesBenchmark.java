package com.coroptis.jblinktree.performance.type;

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

import com.coroptis.jblinktree.JbNodeBuilder;
import com.coroptis.jblinktree.JbNodeDef;
import com.coroptis.jblinktree.JbNodeDefImpl;
import com.coroptis.jblinktree.Node;
import com.coroptis.jblinktree.NodeImpl;
import com.coroptis.jblinktree.store.CacheListener;
import com.coroptis.jblinktree.store.CacheLru;
import com.coroptis.jblinktree.type.TypeDescriptorInteger;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
public class ComparingBytesAndValuesBenchmark {

    private Integer cx = 0;

    private final TypeDescriptorInteger tdi = new TypeDescriptorInteger();

    private final byte[] field = new byte[4];

    public ComparingBytesAndValuesBenchmark() {
    }

    @Benchmark
    @OperationsPerInvocation(1)
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    public void simple_cmp_objects() {
        tdi.save(field, 0, cx);
        int i = tdi.compareValues(cx, tdi.load(field, 0));
        cx++;
    }

    @Benchmark
    @OperationsPerInvocation(1)
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    public void simple_cmp_bytes() {
        tdi.save(field, 0, cx);
        int i = cmp(field, 0, field, 0);
        cx++;
    }

    private int cmp(byte[] a, int posA, byte[] b, int posB) {
        for (int i = 0; i < 4; i++) {
            int t = b[posB + i] - a[posA + i];
            if (t != 0) {
                return t;
            }
        }
        return 0;
    }

}
