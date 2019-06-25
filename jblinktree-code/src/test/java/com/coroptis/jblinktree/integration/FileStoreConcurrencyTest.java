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
import com.coroptis.jblinktree.JbNodeDef;
import com.coroptis.jblinktree.JbNodeDefImpl;
import com.coroptis.jblinktree.JbTreeDataImpl;
import com.coroptis.jblinktree.Node;
import com.coroptis.jblinktree.NodeShort;
import com.coroptis.jblinktree.Worker;
import com.coroptis.jblinktree.util.JblinktreeException;

/**
 * Writes and load multiple nodes in highly concurrent environment. Test verify
 * that nodeStore serialize all requests.
 *
 * @author jajir
 *
 */
public class FileStoreConcurrencyTest {

	private final Logger logger = LoggerFactory.getLogger(FileStoreConcurrencyTest.class);

	private final Integer L = 5;

	private Random random;

	@Rule
	public FileStorageRule fsRule = new FileStorageRule();
	

	@Test
	public void mock() throws Exception {
		//FIXME
	}

	//FIXME add test here
	public void testForThreadClash() throws Exception {
		final int cycleCount = 1000 * 1;
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
				assertEquals(String.format("Node id %s should have 0 key but there are %s keys.", integer,
						node.getKeyCount()), 0, node.getKeyCount());
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

		final JbNodeDefImpl.Initializator<Integer, Integer> init = new JbNodeDefImpl.InitializatorShort<Integer, Integer>();
		final JbNodeDef<Integer, Integer> leafNodeDescriptor = new JbNodeDefImpl<Integer, Integer>(5,
				fsRule.getIntDescriptor(), fsRule.getIntDescriptor(), fsRule.getIntDescriptor(), init);
		final JbNodeDef<Integer, Integer> nonLeafNodeDescriptor = new JbNodeDefImpl<Integer, Integer>(5,
				fsRule.getIntDescriptor(), fsRule.getIntDescriptor(), fsRule.getIntDescriptor(), init);

		JbTreeDataImpl<Integer, Integer> treeData = new JbTreeDataImpl<Integer, Integer>(0, L, leafNodeDescriptor,
				nonLeafNodeDescriptor);

		final Node<Integer, Integer> node = new NodeShort<Integer, Integer>(nodeId, false,
				treeData.getLeafNodeDescriptor());

//		TypeDescriptorInteger intDescriptor = new TypeDescriptorInteger();
//		node.insertAtPosition(Wrapper.make(12, intDescriptor), 1, 1);
		assertEquals(0, node.getKeyCount());
		return node;
	}

}
