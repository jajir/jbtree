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
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import com.coroptis.jblinktree.JbNodeBuilder;
import com.coroptis.jblinktree.JbNodeLockProvider;
import com.coroptis.jblinktree.Node;
import com.coroptis.jblinktree.NodeStore;
import com.coroptis.jblinktree.util.JblinktreeException;

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

    /**
     * In memory stored nodes.
     */
    private final Map<Integer, byte[]> nodes;

    /**
     * Manage node locks.
     */
    private final JbNodeLockProvider nodeLocks;

    /**
     * Node builder factory.
     */
    private final JbNodeBuilder<K, V> nodeBuilder;

    /**
     *
     * @param jbNodeBuilder
     *            required node builder factory
     * @param jbNodeLockProvider
     *            required node lock provider
     */
    public NodeStoreInMem(final JbNodeBuilder<K, V> jbNodeBuilder,
            final JbNodeLockProvider jbNodeLockProvider) {
        this.nodeBuilder = Objects.requireNonNull(jbNodeBuilder);
        this.nodeLocks = Objects.requireNonNull(jbNodeLockProvider);
        nodes = new ConcurrentHashMap<Integer, byte[]>();
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
        byte[] field = nodes.get(Objects.requireNonNull(nodeId));
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
        Objects.requireNonNull(node.getId());
        Objects.requireNonNull(node);
        nodes.put(node.getId(), node.getFieldBytes());
    }

    @Override
    public void deleteNode(final Integer idNode) {
        nodes.remove(Objects.requireNonNull(idNode));
    }

    @Override
    public int countLockedNodes() {
        return nodeLocks.countLockedThreads();
    }

    @Override
    public void close() {
        nodes.clear();
    }

    @Override
    public boolean isNewlyCreated() {
        return true;
    }
}
