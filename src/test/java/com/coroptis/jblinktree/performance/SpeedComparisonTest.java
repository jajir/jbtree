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

import junit.framework.TestCase;

import org.junit.Test;

/**
 * Main test class that allows compare speed of different map implementation.
 * 
 * @author jajir
 * 
 */
public class SpeedComparisonTest extends TestCase {

    private final int cycleCount = 1000 * 1000 * 10;

    private final int threadCount = 10;

    @Test
    public void test_jbTree() throws Exception {
	testFunctionality(new FunctionalityJbTree(), "JbTree");
	testFunctionality(new FunctionalitySynchronizedMap(), "synchronized map");
	testFunctionality(new FunctionalityConcurrentHashMap(), "concurrent hash map");
	testFunctionality(new FunctionalitySynchronizedTreeMap(), "synchronized tree map");
    }

    private void testFunctionality(final TestedTreeFunctionality functionality, final String name)
	    throws Exception {
	JbTreeTest tt1 = new JbTreeTest(functionality, cycleCount, threadCount);
	final long freeMemory_t1 = Runtime.getRuntime().freeMemory();
	final long t1 = System.nanoTime();
	tt1.testForThreadClash();
	final long t2 = System.nanoTime();
	final long freeMemory_t2 = Runtime.getRuntime().freeMemory();
	System.gc();
	printTime(t2 - t1, name);
	printMemory(freeMemory_t1 - freeMemory_t2, name);
    }

    private void printTime(long t, final String name) {
	final long nano_s = t % 1000;
	final long mikro_s = (t / (1000)) % 1000;
	final long mili_s = (t / (1000 * 1000)) % 1000;
	final long s = (t / (1000 * 1000 * 1000)) % 1000;
	final long m = (t / ((long) 1000 * 1000 * 1000 * 60)) % 60;
	System.out.println("m=" + m + ", s=" + s + ", mili=" + mili_s + ", mikro=" + mikro_s
		+ ", nano=" + nano_s + ", original=" + t + ", name= " + name);
    }

    private void printMemory(long t, final String name) {
	final long b = t % 1024;
	final long kb = (t / (1024)) % 1024;
	final long mb = (t / ((long) 1024 * 1024)) % 1024;
	System.out.println("mb=" + mb + ", kb=" + kb + ", b=" + b + ", name= " + name);
    }
}
