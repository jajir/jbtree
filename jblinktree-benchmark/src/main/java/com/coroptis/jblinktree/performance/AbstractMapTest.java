package com.coroptis.jblinktree.performance;

/*
 * #%L
 * jblinktree
 * %%
 * Copyright (C) 2015 coroptis
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;

import com.coroptis.jblinktree.performance.tool.NumberGeneratorFile;
import com.google.common.base.Preconditions;

/**
 * Abstract test for {@link Map} implementation benchmarking.
 * 
 * @author jajir
 * 
 */
@State(Scope.Benchmark)
@BenchmarkMode(Mode.SingleShotTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
public abstract class AbstractMapTest {

    public final static String RANDOM_DATA_FILE = "src/data/numbers1.txt";

    private NumberGeneratorFile numberGeneratorFile;

    protected Map<Integer, Integer> map;

    long t1;

    /**
     * Create and return {@link Map} instance.
     * 
     * @return {@link Map} instance
     */
    abstract protected Map<Integer, Integer> initialize();

    /**
     * Get name of {@link Map} implementation for logging purposes.
     * 
     * @return {@link Map} implementation name.
     */
    protected String mapName() {
        return map.getClass().getSimpleName();
    }

    public AbstractMapTest() {
        System.out.println("starting test");
        numberGeneratorFile = new NumberGeneratorFile(RANDOM_DATA_FILE);
        map = Preconditions.checkNotNull(initialize());
    }

    protected long getFreeMem() {
        System.gc();
        return Runtime.getRuntime().freeMemory();
    }

    protected void printMemory(long t, final String name) {
        final long b = t % 1024;
        final long kb = (t / (1024)) % 1024;
        final long mb = (t / ((long) 1024 * 1024)) % 1024;
        System.out.println(
                "mb=" + mb + ", kb=" + kb + ", b=" + b + ", name= " + name);
    }

    @Setup
    public void setup() {
        System.out.println("setup count: " + map.size());
    }

    @TearDown
    public void tearDown() {
        System.out.println("tear down count: " + map.size());
        map = null;
    }

    @Benchmark
    public void insert_performance() {
        final Integer j = numberGeneratorFile.nextInt();
        map.put(j, -j);
    }

}
