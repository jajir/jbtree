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

import java.io.File;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import junit.framework.TestCase;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.coroptis.jblinktree.Executer;
import com.coroptis.jblinktree.JblinktreeException;
import com.coroptis.jblinktree.TreeBuilder;
import com.coroptis.jblinktree.TreeMap;
import com.coroptis.jblinktree.TreeUtil;
import com.coroptis.jblinktree.Worker;
import com.coroptis.jblinktree.type.Types;

/**
 * Test that tree could work in multiple threads environment.
 * 
 * @author jajir
 * 
 */
public class TreeConcurrencyTest extends TestCase {

    private final Logger logger = LoggerFactory.getLogger(TreeConcurrencyTest.class);

    private TreeMap<Integer, Integer> tree;

    private TreeUtil treeUtil;

    private Random random;

    @Test
    public void testForThreadClash() throws Exception {
	final int cycleCount = 100;
	final int threadCount = 500;
	final CountDownLatch doneLatch = new CountDownLatch(cycleCount * threadCount);
	final CountDownLatch startLatch = new CountDownLatch(1);

	for (int i = 0; i < threadCount; ++i) {
	    Runnable runner = new Executer(new Worker() {

		public void doWork() {
		    doWorkNow();
		}
	    }, startLatch, doneLatch, cycleCount);
	    new Thread(runner, "TestThread" + i).start();
	}

	startLatch.countDown();
	doneLatch.await(20, TimeUnit.SECONDS);
	assertEquals("Some thread didn't finished work", 0, doneLatch.getCount());
	assertEquals("Some locks wasn't unlocked", 0, tree.countLockedNodes());
	tree.verify();
	logger.debug("I'm done!");
	treeUtil.toDotFile(new File("pok.dot"));
    }

    @Override
    protected void setUp() throws Exception {
	super.setUp();
	tree = TreeBuilder.builder().setL(2).setKeyType(Types.integer())
		.setValueType(Types.integer()).build();
	treeUtil = new TreeUtil(tree);
	random = new Random();
    }

    @Override
    protected void tearDown() throws Exception {
	tree = null;
	treeUtil = null;
	super.tearDown();
    }

    void doWorkNow() {
	Integer integer = random.nextInt(100) + 1;
	try {
	    tree.put(integer, integer);
	} catch (JblinktreeException e) {
	    synchronized (e) {
		// tree.toDotFile(new File("dot.dot"));
	    }
	    throw e;
	}
    }

}
