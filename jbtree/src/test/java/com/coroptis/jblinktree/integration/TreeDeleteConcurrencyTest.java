package com.coroptis.jblinktree.integration;

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

import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.coroptis.jblinktree.Executer;
import com.coroptis.jblinktree.TreeBuilder;
import com.coroptis.jblinktree.TreeMap;
import com.coroptis.jblinktree.Worker;
import com.coroptis.jblinktree.type.Types;

import junit.framework.TestCase;

/**
 * test verify that add and remove operations works in thread environment.
 *
 * @author jajir
 *
 */
public class TreeDeleteConcurrencyTest extends TestCase {

    private final Logger logger = LoggerFactory
            .getLogger(TreeDeleteConcurrencyTest.class);

    private TreeMap<Integer, Integer> tree;

    private Random random;

    @Test
    public void testForThreadClash() throws Exception {
        final int cycleCount = 100;
        final int threadCount = 100;
        final CountDownLatch doneLatch = new CountDownLatch(
                cycleCount * threadCount);
        final CountDownLatch startLatch = new CountDownLatch(1);

        for (int i = 0; i < threadCount; ++i) {
            Runnable runner = new Executer(new Worker() {

                @Override
                public void doWork() {
                    doWorkNow();
                }
            }, startLatch, doneLatch, cycleCount);
            new Thread(runner, "TestThread" + i).start();
        }

        startLatch.countDown();
        doneLatch.await(2, TimeUnit.MINUTES);
        assertEquals("Some thread didn't finished work", 0,
                doneLatch.getCount());
        logger.debug("I'm done!");
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        tree = TreeBuilder.builder().setL(2).setKeyType(Types.integer())
                .setValueType(Types.integer()).build();
        random = new Random();
    }

    @Override
    protected void tearDown() throws Exception {
        tree = null;
        super.tearDown();
    }

    void doWorkNow() {
        final Integer integer = random.nextInt(100) + 1;
        final Integer operation = random.nextInt(100) + 1;
        if (operation % 2 == 0) {
            logger.debug("inserting :" + integer);
            tree.put(integer, integer);
        } else {
            logger.debug("removing :" + integer);
            tree.remove(integer);
        }
    }

}
