package com.coroptis.jblinktree;

import java.util.Stack;

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
public interface JbTreeTool {

    /**
     * For given key find corresponding node in which key should belongs.
     * <p>
     * Method doesn't lock any node.
     * </p>
     * 
     * @param node
     *            required node object
     * @param key
     *            required key
     * @return found node if there is such, when there is no corresponding key
     *         <code>null</code> is returned.
     */
    Node findCorrespondingNode(Node node, Integer key);

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
    NodeImpl moveRightLeafNode(NodeImpl current, Integer key);

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
    Node moveRightLeafNodeWithoutLocking(Node current, Integer key);

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
    NodeImpl moveRightNonLeafNode(NodeImpl current, Integer key);

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
    NodeImpl split(Node currentNode, Integer key, Integer value);

    void updateMaxValueWhenNecessary(Node currentNode, Integer insertedKey, Stack<Integer> stack);

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
    Integer splitRootNode(Node currentRootNode, Node newNode);

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
    void updateMaxIfNecessary(final NodeImpl parentNode, final Node childNode);

}
