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
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;
import org.openjdk.jmh.annotations.Warmup;

import com.coroptis.jblinktree.performance.tool.NumberGeneratorFile;
import com.google.common.base.Preconditions;

/**
 * Abstract test for {@link Map} implementation benchmarking.
 * 
 * @author jajir
 * 
 */
@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
public abstract class AbstractMapTest {

    private final static String RANDOM_DATA_FILE = "src/data/numbers1.txt";

    private final static int NUMBER_OF_WARM_UP_KEYS = 1000 * 100;

    private final static int NUMBER_OF_INSERTS_IN_TEST = 1;

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
	t1 = getFreeMem();
	printMemory(t1, mapName());
	warmUp();
	System.out.println("test is ready, map size = " + map.size());
    }

    /**
     * At the begging are some random values inserted into tree.
     */
    public void warmUp() {
	for (int i = 0; i < NUMBER_OF_WARM_UP_KEYS; i++) {
	    final Integer j = numberGeneratorFile.nextInt();
	    map.put(j, -j);
	}
    }

    protected long getFreeMem() {
	System.gc();
	return Runtime.getRuntime().freeMemory();
    }

    protected void printMemory(long t, final String name) {
	final long b = t % 1024;
	final long kb = (t / (1024)) % 1024;
	final long mb = (t / ((long) 1024 * 1024)) % 1024;
	System.out.println("mb=" + mb + ", kb=" + kb + ", b=" + b + ", name= "
		+ name);
    }

    @TearDown
    public void tearDown() {
	// printMemory(t1 - getFreeMem(), mapName());
	System.out.println("count: " + map.size());
	map = null;
    }

    @Benchmark
    @Warmup(iterations = 1, batchSize = 1, time = 1)
    public void insert_performance() {
	for (int i = 0; i < NUMBER_OF_INSERTS_IN_TEST; i++) {
	    final Integer j = numberGeneratorFile.nextInt();
	    map.put(j, -j);
	}
    }

}
