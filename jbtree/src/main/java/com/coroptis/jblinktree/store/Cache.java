package com.coroptis.jblinktree.store;

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