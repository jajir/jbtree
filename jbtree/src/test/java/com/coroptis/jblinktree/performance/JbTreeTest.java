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

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import com.coroptis.jblinktree.Executer;
import com.coroptis.jblinktree.JblinktreeException;
import com.coroptis.jblinktree.Worker;

/**
 * Test that tree could work in multiple threads environment.
 * 
 * @author jajir
 * 
 */
public class JbTreeTest {

    private final TestedTreeFunctionality functionality;

    private final int cycleCount;

    private final int threadCount;

    public JbTreeTest(final TestedTreeFunctionality functionality, final int cycleCount,
	    final int threadCount) {
	this.functionality = functionality;
	this.cycleCount = cycleCount;
	this.threadCount = threadCount;
    }

    public Object testForThreadClash() throws Exception {
	final CountDownLatch doneLatch = new CountDownLatch(cycleCount * threadCount);
	final CountDownLatch startLatch = new CountDownLatch(1);

	functionality.setUp();

	for (int i = 0; i < threadCount; ++i) {
	    Runnable runner = new Executer(new Worker() {

		@Override
		public void doWork() {
		    functionality.doWork();
		}
	    }, startLatch, doneLatch, cycleCount);
	    new Thread(runner, "TestThread" + i).start();
	}

	startLatch.countDown();
	doneLatch.await(100, TimeUnit.MINUTES);
	if (0 != doneLatch.getCount()) {
	    throw new JblinktreeException("Some thread didn't finished work");
	}
	return functionality.tearDown();
    }

}
