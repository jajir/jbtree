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
import java.util.LinkedList;
import java.util.Map;

import com.coroptis.jblinktree.Node;
import com.coroptis.jblinktree.NodeBuilder;


/**
 * Implementation of Last Recent Used (LRU) cache.
 * 
 * @author jan
 *
 * @param <K>
 *            key type
 * @param <V>
 *            value type
 */
public class LruCache<K, V> {

    final NodeBuilder<K, V> nodeBuilder;

    private final Map<Integer, byte[]> cache = new HashMap<Integer, byte[]>();

    private final LinkedList<Integer> lastRecentUsedIds = new LinkedList<Integer>();

    private final int numberOfNodesCacheSize;

    private final OnEvict<K, V> onEvict;

    public LruCache(final NodeBuilder<K, V> nodeBuilder, final int numberOfNodesCacheSize,
	    final OnEvict<K, V> onEvict) {
	this.nodeBuilder = nodeBuilder;
	this.numberOfNodesCacheSize = numberOfNodesCacheSize;
	this.onEvict = onEvict;
    }

    void put(final Node<K, V> node) {
	setLastUsed(node.getId());
	cache.put(node.getId(), node.getFieldBytes());
	checkCacheSize();
    }

    private void checkCacheSize() {
	if (cache.size() > numberOfNodesCacheSize) {
	    Integer nodeId = lastRecentUsedIds.removeLast();
	    final byte[] field = cache.remove(nodeId);
	    onEvict.evict((Node<K, V>) nodeBuilder.makeNode(nodeId, field));
	}
    }

    void remove(final Integer idNode) {
	lastRecentUsedIds.remove(idNode);
	Node<K, V> node = nodeBuilder.makeNode(idNode, cache.remove(idNode));
	onEvict.evict(node);
    }

    Node<K, V> get(final Integer idNode) {
	if (cache.containsKey(idNode)) {
	    setLastUsed(idNode);
	    return nodeBuilder.makeNode(idNode, cache.get(idNode));
	} else {
	    Node<K, V> node = onEvict.load(idNode);
	    put(node);
	    return node;
	}
    }

    private void setLastUsed(final Integer idNode) {
	lastRecentUsedIds.remove(idNode);
	lastRecentUsedIds.add(0, idNode);
    }

}
