package com.coroptis.jblinktree;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;

import com.google.common.base.Preconditions;

/**
 * Class holds list of locks.
 * <p>
 * When some lock is unlocked there is no sure that will be anymore needed so
 * could be dropped from memory.
 * </p>
 * <p>
 * because inserting procedure itself prevent from accessing new node with lock
 * lock surrounding nodes put into {@link Map} doesn't have to be thread safe.
 * </p>
 * 
 * @author jajir
 * 
 */
public class NodeLocks {

    /**
     * hash map contains map if node id and lock mapping.
     */
    private final Map<Integer, Lock> locks;

    public NodeLocks() {
	locks = new HashMap<Integer, Lock>();
    }

    public void lockNode(final Integer nodeId) {
	Preconditions.checkNotNull(nodeId);
	Lock lock = locks.get(nodeId);
	if (lock == null) {
	    lock = new MyLoggingLock(nodeId);
	    locks.put(nodeId, lock);
	}
	lock.lock();
    }

    public void unlockNode(final Integer nodeId) {
	Preconditions.checkNotNull(nodeId);
	Lock lock = locks.get(nodeId);
	if (lock != null) {
	    lock.unlock();
	}

    }

}
