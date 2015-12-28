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
 * Allows to read &amp; write nodes. Main methods are thread safe. Get method
 * returns always new node instance with same data.
 * 
 * @author jajir
 * 
 */
public interface NodeStore<K> {

    /**
     * When new tree is created than first node have this id.
     */
    final static int FIRST_NODE_ID = 0;

    /**
     * Lock given node.
     * 
     * @param nodeId
     *            required node id
     */
    void lockNode(Integer nodeId);

    /**
     * Allows to unlock node.
     * 
     * @param nodeId
     *            required node id
     */
    void unlockNode(Integer nodeId);

    /**
     * Return defensive copy of node instance.
     * 
     * @param <S>
     *            node value type
     * @param nodeId
     *            required node id
     * @return copy of node from store
     */
    <S> Node<K, S> get(Integer nodeId);

    /**
     * Return defensive copy of node instance and lock it.
     * 
     * @param <S>
     *            node value type
     * @param nodeId
     *            required node id
     * @return copy of node from store
     */
    <S> Node<K, S> getAndLock(Integer nodeId);

    /**
     * Persist node into node store.
     * <p>
     * Method doens't work with locks.
     * </p>
     * 
     * @param <S>
     *            node value type
     * @param node
     *            required {@link Node}
     */
    <S> void writeNode(Node<K, S> node);

    /**
     * Allows to remove node from store.
     * 
     * @param idNode
     *            required node id
     */
    void deleteNode(Integer idNode);

    /**
     * Get number of nodes that are locked.
     * 
     * @return number of locked nodes.
     */
    int countLockedNodes();

    /**
     * Method provide new node id.
     * 
     * @return new node id
     */
    Integer getNextId();

    /**
     * Get maximal node id.
     * 
     * @return maximal node id
     */
    int getMaxNodeId();
}
