package com.coroptis.jblinktree;

import java.util.concurrent.CountDownLatch;

/**
 * Perform some work when come start signal. Each time when some work is done
 * doneSignal is notified.
 * 
 * @author jajir
 * 
 */
final class Executer implements Runnable {

    /**
     * 
     */
    private final Worker worker;
    private final CountDownLatch startSignal;
    private final CountDownLatch doneSignal;
    private final int cycleCount;

    Executer(final Worker worker, final CountDownLatch startSignal,
	    final CountDownLatch doneSignal, final int cycleCount) {
	this.worker = worker;
	this.startSignal = startSignal;
	this.doneSignal = doneSignal;
	this.cycleCount = cycleCount;
    }

    @Override
    public final void run() {
	try {
	    startSignal.await();
	    for (int j = 0; j < cycleCount; j++) {
		worker.doWork();
		doneSignal.countDown();
	    }
	} catch (InterruptedException ex) {
	} // return;
    }
}