package com.coroptis.jblinktree;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MyLoggingLock implements Lock {

    private final Logger logger = LoggerFactory.getLogger(MyLoggingLock.class);

    private final Lock lock;

    private final Integer nodeId;

    public MyLoggingLock(final Integer nodeId) {
	lock = new ReentrantLock();
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
    public boolean tryLock(long time, TimeUnit unit)
	    throws InterruptedException {
	return lock.tryLock(time, unit);
    }

    @Override
    public void unlock() {
	logger.trace("unlocking node {}", nodeId);
	lock.unlock();
    }

}
