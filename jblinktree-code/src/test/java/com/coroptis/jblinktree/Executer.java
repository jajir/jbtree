package com.coroptis.jblinktree;

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

/**
 * Perform some work when come start signal. Each time when some work is done
 * doneSignal is notified.
 *
 * @author jajir
 *
 */
final public class Executer implements Runnable {

    /**
     *
     */
    private final Worker worker;
    private final CountDownLatch startSignal;
    private final CountDownLatch doneSignal;
    private final int cycleCount;

    public Executer(final Worker worker, final CountDownLatch startSignal,
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