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
	    /**
	     * Following construction prevent store from creating more lock
	     * instances for same nodeId.
	     */
	    synchronized (this) {
		lock = locks.get(nodeId);
		if (lock == null) {
		    lock = new MyLoggingLock(nodeId);
		    locks.put(nodeId, lock);
		}
	    }
	}
	lock.lock();
    }

    public void unlockNode(final Integer nodeId) {
	Preconditions.checkNotNull(nodeId);
	Lock lock = locks.get(nodeId);
	if (lock == null) {
	    throw new JblinktreeException("Attempt to unlock not locked node '" + nodeId + "'");
	} else {
	    lock.unlock();
	}
    }

    public int countLockedThreads() {
	int out = 0;
	for (final Lock lock : locks.values()) {
	    final MyLoggingLock l = (MyLoggingLock) lock;
	    if (l.getLock().isLocked()) {
		out++;
	    }
	}
	return out;
    }
}
