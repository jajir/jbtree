package com.coroptis.jblinktree;

import com.coroptis.jblinktree.util.JbStack;

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
 * Provide simple operations with tree.
 *
 * @author jajir
 *
 * @param <K>
 *            key type
 * @param <V>
 *            value type
 *
 */
public interface JbTreeTool<K, V> {

    /**
     * Move right in tree until suitable non-leaf node is found.
     * <p>
     * Method doesn't work with locks.
     * </p>
     *
     * @param current
     *            required current node, this node should be locked
     * @param key
     *            required key
     * @return moved right node
     */
    Node<K, V> moveRightLeafNodeWithoutLocking(Node<K, V> current, K key);

    /**
     * Split leaf node into two nodes. It moved part of currentNode data into
     * new one which will be returned.
     * <p>
     * Method doesn't work with locks. New node is not locked.
     * </p>
     *
     * @param currentNode
     *            required node which will be split
     * @param key
     *            required inserted key
     * @param value
     *            required inserted value
     * @return newly created node, this node contains higher part of keys.
     */
    Node<K, V> splitLeafNode(Node<K, V> currentNode, K key, V value);

    /**
     * Split non-leaf node into two nodes. It moved part of currentNode data
     * into new one which will be returned.
     * <p>
     * Method doesn't work with locks. New node is not locked.
     * </p>
     *
     * @param currentNode
     *            required node which will be split
     * @param key
     *            required inserted key
     * @param value
     *            required inserted value
     * @return newly created node, this node contains higher part of keys.
     */
    Node<K, Integer> splitNonLeafNode(final Node<K, Integer> currentNode,
            final K key, final Integer value);

    /**
     * It get already existing node, new node a create new root node pointing on
     * this two nodes.
     * <p>
     * Method doesn't work with locks.
     * </p>
     *
     * @param <S>
     *            node value type
     * @param currentRootNode
     *            required currently exiting root node
     * @param newNode
     *            new node that should be added to root node
     * @return id of newly created root node
     */
    <S> Integer splitRootNode(Node<K, S> currentRootNode, Node<K, S> newNode);

    /**
     * When new tree is created this method create new empty leaf root node.
     *
     * @return new root node id.
     */
    Integer createRootNode();

    /**
     * Non locking method that find leaf node id where should be given key
     * placed. In Stock are stored passed nodes. Right moved in tree are not
     * stored.
     * <p>
     * When it's necessary to move right in stack are stored just rightmost
     * nodes id.
     * </p>
     * <p>
     * Method doesn't lock any nodes.
     * </p>
     *
     * @param key
     *            required key
     * @param stack
     *            required stack
     * @param rootNodeId
     *            required nodeId
     * @return leaf node id where should be key found or stored, it's never
     *         <code>null</code>
     */
    Integer findLeafNodeId(K key, JbStack stack, Integer rootNodeId);

    /**
     * During searching for proper place where to put key it's important to know
     * if is possible to move to next node. Next node is find by link.
     *
     * @param node
     *            required node
     * @param key
     *            required key
     * @return <code>true</code> when it's possible to move to next node
     *         otherwise return <code>false</code>
     */
    boolean canMoveToNextNode(final Node<K, ?> node, final K key);

    /**
     * Move to next node. Lock next node than unlock current node.
     *
     * @param <S>
     *            node value type
     * @param currentNode
     *            required node
     * @param nextNodeId
     *            required next node id
     * @return locked new node instance
     */

    <S> Node<K, S> moveToNextNode(final Node<K, ?> currentNode,
            final Integer nextNodeId);

}
