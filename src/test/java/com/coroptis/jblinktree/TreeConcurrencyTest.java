package com.coroptis.jblinktree;

import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import junit.framework.TestCase;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Test that tree could work in multiple threads environment.
 * 
 * @author jajir
 * 
 */
public class TreeConcurrencyTest extends TestCase {

    private final Logger logger = LoggerFactory.getLogger(TreeConcurrencyTest.class);

    private Tree tree;

    private Random random;

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
	doneLatch.await(10, TimeUnit.SECONDS);
	assertEquals("Some thread didn't finished work", 0, doneLatch.getCount());
	tree.verify();
	logger.debug("I'm done!");
    }

    @Override
    protected void setUp() throws Exception {
	super.setUp();
	NodeStore nodeStore = new NodeStoreImpl();
	tree = new Tree(2, nodeStore);
	random = new Random();
    }

    @Override
    protected void tearDown() throws Exception {
	tree = null;
	super.tearDown();
    }

    void doWorkNow() {
	Integer integer = random.nextInt(100) + 1;
	logger.debug("inserting :" + integer);
	tree.insert(integer, integer);
    }

}
