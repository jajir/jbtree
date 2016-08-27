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

import java.util.List;

/**
 * Tree node providing basic operations.
 *
 * @author jajir
 *
 *
 * @param <K>
 *            key type
 * @param <V>
 *            value type
 */
public interface Node<K, V> {

    /**
     * When this value in at flag position than it's leaf node.
     */
    byte M = -77;

    /**
     * Value for Integer represents empty state.
     */
    Integer EMPTY_INT = -1;

    /**
     * Get link value. Delegate to {@link Field#getLink()}
     *
     * @return link value, could be {@link Node#EMPTY_INT} but never null
     */
    Integer getLink();

    /**
     * Allows to set link value. Delegate to {@link Field#setLink(Integer)}
     *
     * @param link
     *            link value, could be {@link Node#EMPTY_INT}
     * @throws NullPointerException
     *             when link is <code>null</code>
     */
    void setLink(Integer link);

    /**
     * Return true when node is empty. Empty means there are no keys in node. In
     * case of non-leaf node there still can be P0 link and max value.
     *
     * @return return <code>true</code> when node is empty otherwise return
     *         <code>false</code>
     */
    boolean isEmpty();

    /**
     * Get number of keys stored in node.
     *
     * @return number of stored keys.
     */
    int getKeyCount();

    /**
     * Insert or override some value in node.
     *
     * @param key
     *            required key
     * @param value
     *            required value
     * @throws NullPointerException
     *             when key or value is null
     */
    void insert(K key, V value);

    /**
     * Remove key and associated value from node.
     *
     * @param key
     *            required key to remove
     * @return when key was found and removed it return <code>true</code>
     *         otherwise it return <code>false</code>
     * @throws NullPointerException
     *             when key or value is null
     */
    V remove(K key);

    /**
     * For non-leaf tree it update key of some tree. It's useful for update
     * sub-node max key.
     *
     * @param valueToUpdate
     *            required value which will be find
     * @param keyToSet
     *            required key that will be set to find value
     * @return return <code>true</code> when valueToUpdate was found and key was
     *         really updated otherwise return <code>false</code>
     */
    boolean updateKeyForValue(Integer valueToUpdate, K keyToSet);

    /**
     * About half of keys will be copied to <code>node</code>.
     * <p>
     * From this node will be created structure: thisNode ---&gt; node
     * </p>
     *
     * @param node
     *            required empty node
     */
    void moveTopHalfOfDataTo(Node<K, V> node);

    /**
     * Return max key, that could be use for representing this node.
     *
     * @return max key
     */
    K getMaxKey();

    /**
     * Return node id.
     *
     * @return the id
     */
    Integer getId();

    /**
     * Inform id it's leaf node or non-leaf node.
     *
     * @return return <code>true</code> when it's leaf node otherwise return
     *         <code>false</code>
     */
    boolean isLeafNode();

    /**
     * Find value for given key.
     * <p>
     * Method is not fast and should not be called in main search algorithm.
     * </p>
     *
     * @param key
     *            required key
     * @return found value if there is any, when value is <code>null</code> or
     *         there is no such key <code>null</code> is returned.
     */
    V getValueByKey(K key);

    /**
     * Get list of all node id stored in this node.
     *
     * @return list of id
     */
    List<Integer> getNodeIds();

    /**
     * Get list of keys stored in node.
     *
     * @return list of keys.
     */
    List<K> getKeys();

    /**
     * Verify that node is consistent.
     *
     * @return <code>true</code> when node is consistent otherwise return
     *         <code>false</code>
     */
    boolean verify();

    /**
     * Write node content into {@link StringBuilder}.
     *
     * @param buff
     *            required {@link StringBuilder} instance
     * @param intendation
     *            how many white spaces should be added before each line.
     */
    void writeTo(StringBuilder buff, String intendation);

    /**
     * Get node content as byte array.
     *
     * @return byte array
     */
    byte[] getFieldBytes();

    /**
     * Get key from specific position.
     *
     * @param position
     *            required key position
     * @return key
     */
    K getKey(int position);

    /**
     * Get value from specific position.
     *
     * @param position
     *            required value position
     * @return value
     */
    V getValue(int position);

    /**
     * Allows to set key at specific position.
     *
     * @param position
     *            required position
     * @param value
     *            required key
     */
    void setKey(int position, K value);

    /**
     * Allows to set value at specific position.
     *
     * @param position
     *            required position
     * @param value
     *            required value
     */
    void setValue(int position, V value);

    /**
     * Return node data definition.
     *
     * @return node data definition
     */
    JbNodeDef<K, V> getNodeDef();
}
