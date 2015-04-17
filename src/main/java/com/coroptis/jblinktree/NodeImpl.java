package com.coroptis.jblinktree;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class NodeImpl implements Node {

	private Lock lock;

	NodeImpl(){
		lock = new ReentrantLock();
	}
	
	@Override
	public Lock getLock() {
		return lock;
	}

	@Override
	public void setLock(final Lock lock) {
		this.lock = lock;
	}
}
