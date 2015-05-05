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


import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import junit.framework.TestCase;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.coroptis.jblinktree.Executer;
import com.coroptis.jblinktree.Node;
import com.coroptis.jblinktree.NodeStore;
import com.coroptis.jblinktree.NodeStoreImpl;
import com.coroptis.jblinktree.Worker;

/**
 * Test verify that access to some node from multiple threads could be
 * controlled by locks.
 * <p>
 * If it's work correctly than in logs are messages node is locked/unlocked in
 * pairs. Please note that two messages in one milisecond could be switched.
 * </p>
 * 
 * @author jajir
 * 
 */
public class NodeStoreConcurrencyTest extends TestCase {

    private final Logger logger = LoggerFactory.getLogger(NodeStoreConcurrencyTest.class);

    private NodeStore nodeStore;

    @Test
    public void testForThreadClash() throws Exception {
	final int cycleCount = 10;
	final int threadCount = 10;
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
	doneLatch.await(10,TimeUnit.SECONDS);
	assertEquals("Some thread didn't finished",0, doneLatch.getCount());
	logger.debug("I'm done!");
    }

    @Override
    protected void setUp() throws Exception {
	super.setUp();
	nodeStore = new NodeStoreImpl();
	Node node = new Node(2, 1, true);
	nodeStore.writeNode(node);
    }

    @Override
    protected void tearDown() throws Exception {
	nodeStore = null;
	super.tearDown();
    }

    void doWorkNow() {
	int nodeId = 1;
	try {
	    nodeStore.lockNode(nodeId);
	    Thread.sleep(10);
	    nodeStore.unlockNode(nodeId);
	    Thread.sleep(100);
	} catch (InterruptedException e) {
	    logger.error(e.getMessage(), e);
	}
    }

}
