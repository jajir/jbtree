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

import com.coroptis.jblinktree.JbNodeBuilder;
import com.coroptis.jblinktree.Node;
import com.coroptis.jblinktree.NodeLocks;
import com.coroptis.jblinktree.NodeStore;
import com.google.common.base.Preconditions;

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
    private final NodeLocks nodeLocks;

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
     * @param nodeBuilder
     *            required node builder factory
     * @param numberOfNodesCacheSize
     *            required maximum number of keys in memory
     * @param nodeFileStorage
     *            node file storage
     */
    public NodeStoreInFile(final JbNodeBuilder<K, V> nodeBuilder,
            final int numberOfNodesCacheSize,
            final NodeFileStorage<K, V> nodeFileStorage) {
        this.fileStorage = Preconditions.checkNotNull(nodeFileStorage);
        nodeLocks = new NodeLocks();
        nodeCache = new CacheLru<K, V>(nodeBuilder, numberOfNodesCacheSize,
                new CacheListener<K, V>() {

                    @Override
                    public void onUnload(final Node<K, V> node,
                            final boolean wasChanged) {
                        if (wasChanged) {
                            fileStorage.store(node);
                        }
                    }

                    @Override
                    public Node<K, V> onLoad(final Integer nodeId) {
                        return fileStorage.load(nodeId);
                    }
                });
    }

    @Override
    public void lockNode(final Integer nodeId) {
        nodeLocks.lockNode(Preconditions.checkNotNull(nodeId));
    }

    @Override
    public void unlockNode(final Integer nodeId) {
        nodeLocks.unlockNode(Preconditions.checkNotNull(nodeId));
    }

    @Override
    public <S> Node<K, S> get(final Integer nodeId) {
        Node<K, S> node =
                (Node<K, S>) nodeCache.get(Preconditions.checkNotNull(nodeId));
        return node;
    }

    @Override
    public <S> Node<K, S> getAndLock(final Integer nodeId) {
        lockNode(nodeId);
        return get(nodeId);
    }

    @Override
    public <S> void writeNode(final Node<K, S> node) {
        Preconditions.checkNotNull(node.getId());
        Preconditions.checkNotNull(node);
        nodeCache.put((Node<K, V>) node);
    }

    @Override
    public void deleteNode(final Integer idNode) {
        nodeCache.remove(Preconditions.checkNotNull(idNode));
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
