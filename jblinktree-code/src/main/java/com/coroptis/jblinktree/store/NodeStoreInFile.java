package com.coroptis.jblinktree.store;

import java.util.Objects;

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

import com.coroptis.jblinktree.JbNodeLockProvider;
import com.coroptis.jblinktree.Node;
import com.coroptis.jblinktree.NodeStore;

/**
 * Implementation of {@link NodeStore}. Stores nodes in file system with cache.
 *
 * @author jajir
 *
 * @param <K>
 *            key type
 * @param <V>
 *            value type
 */
public final class NodeStoreInFile<K, V> implements NodeStore<K> {

    /**
     * Node lock service.
     */
    private final JbNodeLockProvider nodeLocks;

    /**
     * Node in memory cache.
     */
    private final Cache<K, V> nodeCache;

    /**
     * Node file system storage.
     */
    private final NodeFileStorage<K, V> fileStorage;

    /**
     *
     * @param cache
     *            required cache implentation
     * @param nodeFileStorage
     *            node file storage
     * @param jbNodeLockProvider
     *            required node lock provider
     */
    public NodeStoreInFile(final Cache<K, V> cache,
            final NodeFileStorage<K, V> nodeFileStorage,
            final JbNodeLockProvider jbNodeLockProvider) {
        this.fileStorage = Objects.requireNonNull(nodeFileStorage);
        this.nodeCache = Objects.requireNonNull(cache);
        this.nodeLocks = Objects.requireNonNull(jbNodeLockProvider);
    }

    @Override
    public void lockNode(final Integer nodeId) {
        nodeLocks.lockNode(Objects.requireNonNull(nodeId));
    }

    @Override
    public void unlockNode(final Integer nodeId) {
        nodeLocks.unlockNode(Objects.requireNonNull(nodeId));
    }

    @Override
    public <S> Node<K, S> get(final Integer nodeId) {
        Node<K, S> node =
                (Node<K, S>) nodeCache.get(Objects.requireNonNull(nodeId));
        return node;
    }

    @Override
    public <S> Node<K, S> getAndLock(final Integer nodeId) {
        lockNode(nodeId);
        return get(nodeId);
    }

    @Override
    public <S> void writeNode(final Node<K, S> node) {
        Objects.requireNonNull(node.getId());
        Objects.requireNonNull(node);
        nodeCache.put((Node<K, V>) node);
    }

    @Override
    public void deleteNode(final Integer idNode) {
        nodeCache.remove(Objects.requireNonNull(idNode));
    }

    @Override
    public int countLockedNodes() {
        return nodeLocks.countLockedThreads();
    }

    @Override
    public void close() {
        nodeCache.close();
        fileStorage.close();
    }

    @Override
    public boolean isNewlyCreated() {
        return fileStorage.isNewlyCreated();
    }

}
