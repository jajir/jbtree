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

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import com.coroptis.jblinktree.JblinktreeException;
import com.coroptis.jblinktree.Node;
import com.coroptis.jblinktree.NodeBuilder;
import com.coroptis.jblinktree.NodeLocks;
import com.coroptis.jblinktree.NodeStore;
import com.google.common.base.Preconditions;

/**
 * Implementation of {@link NodeStore}.
 *
 * @author jajir
 *
 * @param <K>
 *            key type
 * @param <V>
 *            value type
 */
public final class NodeStoreInMem<K, V> implements NodeStore<K> {

    private final Map<Integer, byte[]> nodes;

    private final NodeLocks nodeLocks;

    private final NodeBuilder<K, V> nodeBuilder;

    private final AtomicInteger nextId;

    public NodeStoreInMem(final NodeBuilder<K, V> nodeBuilder) {
        this.nextId = new AtomicInteger(FIRST_NODE_ID);
        this.nodeBuilder = Preconditions.checkNotNull(nodeBuilder);
        nodes = new ConcurrentHashMap<Integer, byte[]>();
        nodeLocks = new NodeLocks();
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
        byte[] field = nodes.get(Preconditions.checkNotNull(nodeId));
        if (field == null) {
            throw new JblinktreeException(
                    "There is no node with id '" + nodeId + "'");
        }
        return nodeBuilder.makeNode(nodeId, field);
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
        nodes.put(node.getId(), node.getFieldBytes());
    }

    @Override
    public void deleteNode(final Integer idNode) {
        nodes.remove(Preconditions.checkNotNull(idNode));
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
