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

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Wrapper around java.util.concurrent lock providing additional logging.
 * 
 * @author jajir
 * 
 */
public class MyLoggingLock implements Lock {

    private final Logger logger = LoggerFactory.getLogger(MyLoggingLock.class);

    private final Lock lock;

    private final Integer nodeId;

    public MyLoggingLock(final Integer nodeId) {
	lock = new ReentrantLock(false);
	this.nodeId = nodeId;
    }

    @Override
    public void lock() {
	logger.trace("locking node {}", nodeId);
	lock.lock();
    }

    @Override
    public void lockInterruptibly() throws InterruptedException {
	lock.lockInterruptibly();
    }

    @Override
    public Condition newCondition() {
	return lock.newCondition();
    }

    @Override
    public boolean tryLock() {
	return lock.tryLock();
    }

    @Override
    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
	return lock.tryLock(time, unit);
    }

    @Override
    public void unlock() {
	logger.trace("unlocking node {}", nodeId);
	lock.unlock();
    }

    public ReentrantLock getLock() {
	return (ReentrantLock) lock;
    }

}
