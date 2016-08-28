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
    byte FLAG_LEAF_NODE = -77;

    /**
     * When this value in at flag position than it's non-leaf node.
     */
    byte FLAG_NON_LEAF_NODE = -3;

    /**
     * Value for Integer represents empty state.
     */
    Integer EMPTY_INT = -1;

    /**
     * Index in byte array where is located flag byte.
     */
    Integer FLAG_BYTE_POSITION = 0;

    /**
     * Get link value.
     *
     * @return link value, could be {@link Node#EMPTY_INT} but never null
     */
    Integer getLink();

    /**
     * Allows to set link value.
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

    /**
     * Insert key and value to some specific index position in field.
     *
     * @param key
     *            required key
     * @param value
     *            required value
     * @param targetIndex
     *            required target index in field
     */
    void insertAtPosition(K key, V value, int targetIndex);

    /**
     * Remove two bytes from node field at given position. Method doesn't care
     * about meaning of bites.
     *
     * @param position
     *            required position
     */
    void removeAtPosition(final int position);

    /**
     * Get flag byte.
     *
     * @return flag byte
     */
    byte getFlag();

    /**
     * Allow to set flag byte.
     *
     * @param b
     *            required flag byte
     */
    void setFlag(byte b);

}
