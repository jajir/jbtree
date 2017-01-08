package com.coroptis.jblinktree.store;

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

import java.util.concurrent.locks.ReentrantLock;

import com.coroptis.jblinktree.Node;
import com.google.common.base.Preconditions;

/**
 * Decorator for {@link NodeFileStorage} interface. Class ensure that enclosed
 * nodefile
 *
 * @author jajir
 *
 * @param <K>
 *            key type
 * @param <V>
 *            value type
 */
public final class NodeFileStorageLockDecorator<K, V>
        implements NodeFileStorage<K, V> {

    /**
     * Unified locking for file system operations.
     */
    private final ReentrantLock lock = new ReentrantLock(false);

    /**
     * Node storage to which will be operation delegated.
     */
    private final NodeFileStorage<K, V> next;

    /**
     * Simple constructor.
     *
     * @param nodeFileStorage
     *            required next file storage
     */
    public NodeFileStorageLockDecorator(
            final NodeFileStorage<K, V> nodeFileStorage) {
        this.next = Preconditions.checkNotNull(nodeFileStorage);
    }

    @Override
    public Node<K, V> load(final Integer nodeId) {
        lock.lock();
        try {
            return next.load(nodeId);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void store(final Node<K, V> node) {
        lock.lock();
        try {
            next.store(node);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void close() {
        lock.lock();
        try {
            next.close();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public boolean isNewlyCreated() {
        return next.isNewlyCreated();
    }

}
