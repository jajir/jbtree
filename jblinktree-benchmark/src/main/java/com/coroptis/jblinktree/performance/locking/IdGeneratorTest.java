package com.coroptis.jblinktree.performance.locking;

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

/**
 * Abstract test for {@link Map} implementation benchmarking.
 * 
 * @author jajir
 * 
 */
@State(Scope.Benchmark)
@BenchmarkMode(Mode.SingleShotTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
public class IdGeneratorTest {

    protected IdGenerator g1, g2;

    @Setup
    public void setup() {
        g1 = new IdGeneratorAtomicInt();
        g2 = new IdGeneratorReentrantLock();
    }

    @TearDown
    public void tearDown() {
        g1 = null;
        g2 = null;
    }

    @Benchmark
    public void g1_atomicInt() {
        g1.getNextId();
    }

    @Benchmark
    public void g2_reentrantLock() {
        g2.getNextId();
    }

}
