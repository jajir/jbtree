package com.coroptis.jblinktree;

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

/**
 * Provide tree operations.
 * 
 * @author jajir
 * 
 */
public interface JbTreeService<K, V> {

    /**
     * Load parent node of given node containing given key. When it's necessary
     * to move to next node than method move to next node. Method lock nodex in
     * correct order.
     * 
     * @param <S>
     *            node value type
     * @param currentNode
     *            required current node
     * @param key
     *            required key
     * @param nextNodeId
     *            required next node id
     * @return locked parent node instance
     */
    <S> Node<K, Integer> loadParentNode(Node<K, S> currentNode, K key,
	    Integer nextNodeId);

    /**
     * Store value in leaf node under the key.
     * 
     * @param node
     *            required leaf node
     * @param key
     *            required key
     * @param value
     *            required value
     */
    void storeValueIntoLeafNode(Node<K, V> node, K key, V value);

    /**
     * Store value in non-leaf node under the key.
     * 
     * @param currentNode
     *            required non-leaf node
     * @param key
     *            required key
     * @param value
     *            required value
     */
    void storeValueIntoNonLeafNode(Node<K, Integer> currentNode, K key,
	    Integer value);
}
