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

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import com.coroptis.jblinktree.Node;
import com.google.common.base.Preconditions;
import com.coroptis.jblinktree.JbNodeBuilder;

/**
 * Implementation of cache with eviction based on Last Recent Used (LRU)
 * algorithm.
 *
 * @author jajir
 *
 * @param <K>
 *            key type
 * @param <V>
 *            value type
 */
public final class CacheLru<K, V> implements Cache<K, V> {

    /**
     * Node builder factory.
     */
    private final JbNodeBuilder<K, V> nodeBuilder;

    /**
     * Cache itself.
     */
    private final Map<Integer, byte[]> cache = new HashMap<Integer, byte[]>();

    /**
     * Holds list of last accessed ids. Based on this list is selected last used
     * node id to remove.
     */
    private final LinkedList<Integer> lastRecentUsedIds =
            new LinkedList<Integer>();

    /**
     * How many nodes will be hold in cache.
     */
    private final int numberOfNodesCacheSize;

    /**
     * Cache listener.
     */
    private final CacheListener<K, V> cacheListener;

    /**
     *
     * @param jbNodeBuilder
     *            required node builder
     * @param maxNumberOfNodesInCache
     *            required maximum number of in memory cached nodes.
     * @param initCacheListerer
     *            required cache listener
     */
    public CacheLru(final JbNodeBuilder<K, V> jbNodeBuilder,
            final int maxNumberOfNodesInCache,
            final CacheListener<K, V> initCacheListerer) {
        this.nodeBuilder = Preconditions.checkNotNull(jbNodeBuilder);
        this.numberOfNodesCacheSize = maxNumberOfNodesInCache;
        this.cacheListener = Preconditions.checkNotNull(initCacheListerer);
    }

    @Override
    public void put(final Node<K, V> node) {
        setLastUsed(node.getId());
        cache.put(node.getId(), node.getFieldBytes());
        checkCacheSize();
    }

    /**
     * Verify that number of nodes in cache is under limit. When limit is
     * reached node is evicted.
     */
    private void checkCacheSize() {
        if (cache.size() > numberOfNodesCacheSize) {
            Integer nodeId = lastRecentUsedIds.removeLast();
            final byte[] field = cache.remove(nodeId);
            cacheListener
                    .onUnload((Node<K, V>) nodeBuilder.makeNode(nodeId, field));
        }
    }

    @Override
    public void remove(final Integer idNode) {
        lastRecentUsedIds.remove(idNode);
        Node<K, V> node = nodeBuilder.makeNode(idNode, cache.remove(idNode));
        cacheListener.onUnload(node);
    }

    @Override
    public Node<K, V> get(final Integer idNode) {
        if (cache.containsKey(idNode)) {
            setLastUsed(idNode);
            return nodeBuilder.makeNode(idNode, cache.get(idNode));
        } else {
            Node<K, V> node = cacheListener.onLoad(idNode);
            put(node);
            return node;
        }
    }

    /**
     * Mark some node id as last used. Helps to find less used node to remove.
     *
     * @param idNode
     *            required node id
     */
    private void setLastUsed(final Integer idNode) {
        lastRecentUsedIds.remove(idNode);
        lastRecentUsedIds.add(0, idNode);
    }

    @Override
    public void close() {
        Set<Integer> ids = new HashSet<Integer>();
        ids.addAll(cache.keySet());
        for (Integer idNode : ids) {
            remove(idNode);
        }
    }

}
