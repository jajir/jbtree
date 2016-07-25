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
    final static byte M = -77;

    /**
     * Value for Integer represents empty state.
     */
    final static Integer EMPTY_INT = -1;

    /**
     * Get link value. Delegate to {@link Field#getLink()}
     * 
     * @return link value, could be {@link Node#EMPTY_INT}
     */
    public Integer getLink();

    /**
     * Allows to set link value. Delegate to {@link Field#setLink(Integer)}
     * 
     * @param link
     *            link value, could be {@link Node#EMPTY_INT}
     * @throws NullPointerException
     *             when link is <code>null</code>
     */
    public void setLink(Integer link);

    /**
     * Return true when node is empty. Empty means there are no keys in node. In
     * case of non-leaf node there still can be P0 link and max value.
     * 
     * @return return <code>true</code> when node is empty otherwise return
     *         <code>false</code>
     */
    public boolean isEmpty();

    /**
     * Get number of keys stored in node.
     * 
     * @return number of stored keys.
     */
    public int getKeysCount();

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
    public void insert(K key, V value);

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
    public V remove(K key);

    /**
     * For non-leaf tree it update value of some tree.
     * 
     * @param nodeIdToUpdate
     *            required node is to update
     * @param nodeMaxValue
     *            required value, this value will be set for previous node id
     * @return return <code>true</code> when node max value was really updated
     *         otherwise return <code>false</code>
     */
    public boolean updateNodeValue(Integer nodeIdToUpdate, K nodeMaxValue);

    /**
     * About half of keys will be copied to <code>node</code>.
     * <p>
     * From this node will be created structure: thisNode ---&gt; node
     * </p>
     * 
     * @param node
     *            required empty node
     */
    public void moveTopHalfOfDataTo(Node<K, V> node);

    /**
     * Return max key, that could be use for representing this node.
     * 
     * @return max key 
     */
    public K getMaxKey();

    /**
     * Return node id.
     * 
     * @return the id
     */
    public Integer getId();

    /**
     * Inform id it's leaf node or non-leaf node.
     * 
     * @return return <code>true</code> when it's leaf node otherwise return
     *         <code>false</code>
     */
    public boolean isLeafNode();

    /**
     * When it's non-leaf node it return pointer to next node where should be
     * given key stored.
     * <p>
     * When key is bigger that all keys in node than link is returned. In case
     * of rightmost node next link is <code>null</code>
     * </p>
     * <p>
     * Correct working of method depends on correct setting of max keys.
     * </p>
     * <p>
     * There is possible performance improvement, when search not insert
     * procedure called this method than when key is bigger than max key than
     * null can be returned.
     * </p>
     * 
     * @param key
     *            required key
     * @return node id, in case of rightmost node it returns <code>null</code>
     *         because link is empty
     */
    public Integer getCorrespondingNodeId(K key);

    /**
     * Find value for given key.
     * 
     * @param key
     *            required key
     * @return found value if there is any, when value is <code>null</code> or
     *         there is no such key <code>null</code> is returned.
     */
    public V getValue(K key);

    /**
     * Get list of all node id stored in this node.
     * 
     * @return list of id
     */
    public List<Integer> getNodeIds();

    /**
     * Get list of keys stored in node.
     * 
     * @return list of keys.
     */
    public List<K> getKeys();

    /**
     * Allows to set max key value.
     * 
     * @param maxKey
     *            max key value, could be <code>null</code>
     */
    public void setMaxKey(K maxKey);

    /**
     * Verify that node is consistent.
     * 
     * @return <code>true</code> when node is consistent otherwise return
     *         <code>false</code>
     */
    public boolean verify();

    /**
     * TODO describe what is it.
     * @return the l
     */
    public int getL();

    /**
     * Write node content into {@link StringBuilder}.
     * 
     * @param buff
     *            required {@link StringBuilder} instance
     * @param intendation
     *            how many white spaces should be added before each line.
     */
    public void writeTo(StringBuilder buff, String intendation);

    /**
     * Get node content as byte array.
     * 
     * @return byte array
     */
    public byte[] getFieldBytes();

}