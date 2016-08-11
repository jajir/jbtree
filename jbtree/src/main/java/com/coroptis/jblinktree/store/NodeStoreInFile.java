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

import java.util.concurrent.atomic.AtomicInteger;

import com.coroptis.jblinktree.JbNodeDef;
import com.coroptis.jblinktree.JbTreeData;
import com.coroptis.jblinktree.Node;
import com.coroptis.jblinktree.NodeBuilder;
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

    private final NodeLocks nodeLocks;

    private final AtomicInteger nextId;

    private final LruCache<K, V> nodeCache;

    private final KeyFileStorage<K, V> fileStorage;

    public NodeStoreInFile(final JbTreeData<K, V> treeData,
            final NodeBuilder<K, V> nodeBuilder, String fileName,
            int numberOfNodesCacheSize) {
        this.nextId = new AtomicInteger(FIRST_NODE_ID);
        // FIXME - casting should be removed.
        fileStorage = new KeyFileStorageImpl<K, V>(
                (JbNodeDef<K, V>) treeData.getNonLeafNodeDescriptor(),
                nodeBuilder, fileName);
        nodeLocks = new NodeLocks();
        nodeCache = new LruCache<K, V>(nodeBuilder, numberOfNodesCacheSize,
                new OnEvict<K, V>() {

                    @Override
                    public void evict(Node<K, V> node) {
                        fileStorage.store(node);
                    }

                    @Override
                    public Node<K, V> load(Integer nodeId) {
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
        Node<K, S> node = (Node<K, S>) nodeCache
                .get(Preconditions.checkNotNull(nodeId));
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
        node.verify();
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
    public Integer getNextId() {
        return nextId.getAndIncrement();
    }

    @Override
    public int getMaxNodeId() {
        return nextId.get();
    }

}
