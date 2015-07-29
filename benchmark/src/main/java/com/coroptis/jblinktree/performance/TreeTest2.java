/*
 * Copyright (c) 2014, Oracle America, Inc.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  * Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 *  * Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 *
 *  * Neither the name of Oracle nor the names of its contributors may be used
 *    to endorse or promote products derived from this software without
 *    specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF
 * THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.coroptis.jblinktree.performance;

import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import com.coroptis.jblinktree.TreeBuilder;
import com.coroptis.jblinktree.TreeMap;
import com.coroptis.jblinktree.type.Types;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.SECONDS)
public class TreeTest2 {

    private TreeMap<Integer, Integer> tree;

    private Random random;

    private final static int base = 1000 * 1000;

    long t1;

    private long getFreeMem() {
	System.gc();
	return Runtime.getRuntime().freeMemory();
    }

    @Setup
    public void setUp() {
	random = new Random();
	tree = TreeBuilder.builder().setL(2).setKeyType(Types.integer())
		.setValueType(Types.integer()).build();
	t1 = getFreeMem();
	System.out.println("setup tree: " + tree);
    }

    @TearDown
    public void tearDown() {
	long t2 = getFreeMem();
	printMemory(t1 - t2, "jblinktree");
	tree = null;
    }

    private void printMemory(long t, final String name) {
	final long b = t % 1024;
	final long kb = (t / (1024)) % 1024;
	final long mb = (t / ((long) 1024 * 1024)) % 1024;
	System.out.println("mb=" + mb + ", kb=" + kb + ", b=" + b + ", name= " + name);
    }

    @Benchmark
    public void insert_performance() {
	for (int i = 0; i < 100; i++) {
	    Integer j = random.nextInt(base);
	    tree.put(j, -j);
	}
    }

    public static void main(String[] args) throws RunnerException {
	Options opt = new OptionsBuilder().include(TreeTest2.class.getSimpleName())
		.warmupIterations(0).threads(100).measurementIterations(2).forks(1).build();
	new Runner(opt).run();
    }

}
