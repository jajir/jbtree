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

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Set;

import com.coroptis.jblinktree.JbNodeBuilder;
import com.coroptis.jblinktree.Node;
import com.google.common.base.Preconditions;

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
     * Simple implementation of Last Recent Used cache.
     *
     * @author jajir
     *
     */
    private final class CacheMap extends LinkedHashMap<Integer, CacheItem> {

        /**
         *
         */
        private static final long serialVersionUID = 1L;

        /**
         * How many nodes will be hold in cache.
         */
        private final int numberOfNodesCacheSize;

        /**
         * Constructor.
         *
         * @param maxNumberOfNodesInCache
         *            required maximal number of cached items
         */
        CacheMap(final int maxNumberOfNodesInCache) {
            this.numberOfNodesCacheSize = maxNumberOfNodesInCache;
        }

        @Override
        protected boolean removeEldestEntry(
                final java.util.Map.Entry<Integer, CacheItem> eldestEntry) {
            if (size() > numberOfNodesCacheSize) {
                removeCacheItem(eldestEntry.getKey(), eldestEntry.getValue());
                return true;
            } else {
                return false;
            }
        }

    }

    /**
     * Node builder factory.
     */
    private final JbNodeBuilder<K, V> nodeBuilder;

    /**
     * Cache itself.
     */
    private final CacheMap cache;

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
        this.cacheListener = Preconditions.checkNotNull(initCacheListerer);
        cache = new CacheMap(maxNumberOfNodesInCache);
    }

    @Override
    public void put(final Node<K, V> node) {
        cache.put(node.getId(), CacheItem.make(node.getFieldBytes(), true));
    }

    @Override
    public void remove(final Integer idNode) {
        final CacheItem cacheItem = cache.remove(idNode);
        if (cacheItem != null) {
            final Node<K, V> node =
                    nodeBuilder.makeNode(idNode, cacheItem.getNodeData());
            cacheListener.onUnload(node, cacheItem.isChanged());
        }
    }

    /**
     * Remove given node from cache.
     *
     * @param idNode
     *            required node id
     * @param cacheItem
     *            required {@link CacheItem} object
     */
    private void removeCacheItem(final Integer idNode,
            final CacheItem cacheItem) {
        final Node<K, V> node =
                nodeBuilder.makeNode(idNode, cacheItem.getNodeData());
        cacheListener.onUnload(node, cacheItem.isChanged());
    }

    @Override
    public Node<K, V> get(final Integer idNode) {
        if (cache.containsKey(idNode)) {
            final CacheItem cacheItem = cache.get(idNode);
            return nodeBuilder.makeNode(idNode, cacheItem.getNodeData());
        } else {
            Node<K, V> node = cacheListener.onLoad(idNode);
            cache.put(idNode, CacheItem.make(node.getFieldBytes()));
            return node;
        }
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
