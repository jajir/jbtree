package com.coroptis.jblinktree;

import com.coroptis.jblinktree.type.TypeDescriptor;

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
 */
public interface JbTreeTool<K, V> {

    /**
     * Move right in tree until suitable non-leaf node is found.
     * <p>
     * When there is move right than current node is unlocked and new one is
     * locked.
     * </p>
     * 
     * @param current
     *            required current node, this node should be locked
     * @param key
     *            required key
     * @return moved right node
     */
    Node<K, V> moveRightLeafNode(Node<K, V> current, K key);

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
     * Move right in tree until suitable leaf node is found.
     * <p>
     * When there is move right than current node is unlocked and new one is
     * locked.
     * </p>
     * 
     * @param current
     *            required current node, this node should be locked
     * @param key
     *            required key
     * @return moved right node
     */
    Node<K, Integer> moveRightNonLeafNode(Node<K, Integer> current, K key);

    /**
     * Split node into two nodes. It moved part of currentNode data into new one
     * which will be returned.
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
    <S> Node<K, S> split(Node<K, S> currentNode, K key, S value,
	    TypeDescriptor<S> valueTypeDescriptor);

    /**
     * It get already existing node, new node a create new root node pointing on
     * this two nodes.
     * <p>
     * Method doesn't work with locks.
     * </p>
     * 
     * @param currentRootNode
     *            required currently exiting root node
     * @param newNode
     *            new node that should be added to root node
     * @return id of newly created root node
     */
    <S> Integer splitRootNode(Node<K, S> currentRootNode, Node<K, S> newNode);

    /**
     * Update max value in parent node when child node contains bigger highes
     * key. It correct state when node is added to rightmost node and added node
     * contains bigger key than previously.
     * <p>
     * Method doesn't work with locks.
     * </p>
     * 
     * @param parentNode
     *            required parent node, this node should contains child node
     * @param childNode
     *            required child node
     */
    <S> void updateMaxIfNecessary(final Node<K, Integer> parentNode, final Node<K, S> childNode);

    /**
     * When new tree is created this method create new empty leaf root node.
     * 
     * @return new root node id.
     */
    Integer createRootNode();

}
