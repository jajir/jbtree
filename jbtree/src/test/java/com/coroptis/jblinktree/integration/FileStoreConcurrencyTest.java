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

import static org.junit.Assert.assertEquals;

import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.coroptis.jblinktree.Executer;
import com.coroptis.jblinktree.FileStorageRule;
import com.coroptis.jblinktree.JblinktreeException;
import com.coroptis.jblinktree.Node;
import com.coroptis.jblinktree.NodeImpl;
import com.coroptis.jblinktree.Worker;

/**
 * Writes and load multiple nodes in highly concurrent environment. Test verify
 * that nodeStore serialize all requests.
 * 
 * @author jan
 * 
 */
public class FileStoreConcurrencyTest {

    private final Logger logger = LoggerFactory.getLogger(FileStoreConcurrencyTest.class);

    private final Integer L = 5;

    private Random random;

    @Rule
    public FileStorageRule fsRule = new FileStorageRule();

    @Test
    public void testForThreadClash() throws Exception {
	final int cycleCount = 1000 * 10;
	final int threadCount = 100;
	final CountDownLatch doneLatch = new CountDownLatch(cycleCount * threadCount);
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
	doneLatch.await(20, TimeUnit.SECONDS);
	assertEquals("Some thread didn't finished work", 0, doneLatch.getCount());
	logger.debug("I'm done!");
    }

    @Before
    public void setUp() throws Exception {
	random = new Random();
	for (int i = 0; i < 100; i++) {
	    fsRule.getFileStorage().store(getNode(i));
	}
    }

    @After
    public void tearDown() throws Exception {
	random = null;
    }

    void doWorkNow() {
	Integer integer = random.nextInt(100);
	boolean read = random.nextBoolean();
	try {
	    if (read) {
		Node<Integer, Integer> node = fsRule.getFileStorage().load(integer);
		assertEquals(1, node.getKeysCount());
	    } else {
		fsRule.getFileStorage().store(getNode(integer));
	    }
	} catch (JblinktreeException e) {
	    synchronized (e) {
		// tree.toDotFile(new File("dot.dot"));
	    }
	    throw e;
	}
    }

    private Node<Integer, Integer> getNode(final Integer nodeId) {
	final Node<Integer, Integer> node = new NodeImpl<Integer, Integer>(L, nodeId, false,
		fsRule.getIntDescriptor(), fsRule.getIntDescriptor(), fsRule.getIntDescriptor());
	node.insert(nodeId, nodeId);
	return node;
    }

}
