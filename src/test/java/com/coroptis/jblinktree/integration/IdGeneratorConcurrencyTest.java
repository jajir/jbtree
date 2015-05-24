package com.coroptis.jblinktree.integration;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import junit.framework.TestCase;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.coroptis.jblinktree.Executer;
import com.coroptis.jblinktree.IdGenerator;
import com.coroptis.jblinktree.IdGeneratorImpl;
import com.coroptis.jblinktree.JblinktreeException;
import com.coroptis.jblinktree.Worker;

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

/**
 * Class try to verify that {@link IdGenerator} is thread safe.
 * 
 * @author jajir
 * 
 */
public class IdGeneratorConcurrencyTest extends TestCase {

    private final Logger logger = LoggerFactory.getLogger(IdGeneratorConcurrencyTest.class);

    private IdGenerator idGenerator;

    private Set<Integer> s;

    @Test
    public void testForThreadClash() throws Exception {
	final int cycleCount = 100;
	final int threadCount = 300;
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
	doneLatch.await(100, TimeUnit.SECONDS);
	assertEquals("Some thread didn't finished work", 0, doneLatch.getCount());
	logger.debug("I'm done!");
    }

    @Override
    protected void setUp() throws Exception {
	super.setUp();
	s = Collections.synchronizedSet(new HashSet<Integer>());
	idGenerator = new IdGeneratorImpl();
    }

    @Override
    protected void tearDown() throws Exception {
	s = null;
	idGenerator = null;
	super.tearDown();
    }

    void doWorkNow() {
	Integer i = idGenerator.getNextId();
	if (!s.add(i)) {
	    throw new JblinktreeException("IdGenerator is not thread safe,"
		    + " some node id was provdes at lest two times.");
	}
    }
}
