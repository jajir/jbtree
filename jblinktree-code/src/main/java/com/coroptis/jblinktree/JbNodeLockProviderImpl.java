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

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.coroptis.jblinktree.util.JblinktreeException;
import com.google.common.base.Preconditions;

/**
 * Implementation of {@link JbNodeLockProvider}. Locks for nodes are stored in
 * {@link java.util.HashMap}.
 *
 * @author jajir
 *
 */
public final class JbNodeLockProviderImpl implements JbNodeLockProvider {

    /**
     * hash map contains map if node id and lock mapping.
     */
    private final Map<Integer, Lock> locks;

    /**
     * Create new node lock instance.
     */
    public JbNodeLockProviderImpl() {
        locks = new ConcurrentHashMap<Integer, Lock>();
    }

    @Override
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
                    lock = new ReentrantLock(false);
                    locks.put(nodeId, lock);
                }
            }
        }
        lock.lock();
    }

    @Override
    public void unlockNode(final Integer nodeId) {
        Preconditions.checkNotNull(nodeId);
        Lock lock = locks.get(nodeId);
        if (lock == null) {
            throw new JblinktreeException(
                    "Attempt to unlock not locked node '" + nodeId + "'");
        } else {
            lock.unlock();
        }
    }

    @Override
    public int countLockedThreads() {
        int out = 0;
        for (final Lock lock : locks.values()) {
            final ReentrantLock l = (ReentrantLock) lock;
            if (l.isLocked()) {
                out++;
            }
        }
        return out;
    }

    @Override
    public void removeLock(final Integer nodeId) {
        final Lock lock = locks.get(nodeId);
        if (lock != null) {
            final ReentrantLock l = (ReentrantLock) lock;
            /**
             * Attempt to remove node lock from memory. If node is locked than
             * I'm not sure, if it's necessary bad state. Node will be later
             * loaded and unloaded it allows to remove lock next time.
             */
            if (!l.isLocked()) {
                locks.remove(nodeId);
            }
        }
    }
}
