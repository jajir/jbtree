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

import com.coroptis.jblinktree.Node;

/**
 * Cache for nodes for B*link tree.
 *
 * @author jajir
 *
 * @param <K>
 *            key type
 * @param <V>
 *            value type
 */
public interface Cache<K, V> {

    /**
     * Put another node into cache. If some node should be evicted from cache
     * than {@link CacheListener#onUnload(Node)} is called.
     * 
     * @param node
     *            required node
     */
    void put(Node<K, V> node);

    /**
     * Remove node from cache. Call {@link CacheListener#onUnload(Node)}
     *
     * @param idNode
     *            required id node
     */
    void remove(Integer idNode);

    /**
     * Get node from cache. If requested node is not in cache than
     * {@link CacheListener#onLoad(Integer)} is called.
     *
     * @param idNode
     *            required id node
     * @return cached node
     */
    Node<K, V> get(Integer idNode);

    /**
     * Evict all cached data.
     */
    void close();

}