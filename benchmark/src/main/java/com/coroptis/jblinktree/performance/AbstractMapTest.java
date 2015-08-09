package com.coroptis.jblinktree.performance;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
public abstract class AbstractMapTest {

    protected Map<Integer, Integer> tree;

    protected Random random;

    protected final static int base = 1000 * 1000;

    private final static int warmUpNumbers = 1000 * 100;

    long t1;

    protected void setup() {

    }

    protected void warmUp() {
	random = new Random();
	for (int i = 0; i < warmUpNumbers; i++) {
	    Integer j = random.nextInt(base);
	    tree.put(j, -j);
	}
	t1 = getFreeMem();
	printMemory(t1, "jblinktree");
    }

    protected long getFreeMem() {
	System.gc();
	return Runtime.getRuntime().freeMemory();
    }

    protected void printMemory(long t, final String name) {
	final long b = t % 1024;
	final long kb = (t / (1024)) % 1024;
	final long mb = (t / ((long) 1024 * 1024)) % 1024;
	System.out.println("mb=" + mb + ", kb=" + kb + ", b=" + b + ", name= " + name);
    }

    @TearDown
    public void tearDown() {
	long t2 = getFreeMem();
	printMemory(t1 - t2, "jblinktree");
	System.out.println("count: " + tree.size());
	tree = null;
    }

    @Benchmark
    public void insert_performance() {
	Integer j = random.nextInt(base) + warmUpNumbers;
	tree.put(j, -j);
    }

}
