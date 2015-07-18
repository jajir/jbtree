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
 * @author jan
 * 
 */
public interface Node {

    /**
     * Get link value.
     * 
     * @return link value,could be <code>null</code>
     */
    public Integer getLink();

    /**
     * Allows to set link value
     * 
     * @param link
     *            link value, could be <code>null</code>
     */
    public void setLink(Integer link);

    /**
     * Return P0 value
     * 
     * @return link value,could be <code>null</code>
     */
    public Integer getP0();

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
     */
    public void insert(Integer key, Integer value);

    /**
     * Remove key and associated value from node.
     * 
     * @param key
     *            required key to remove
     * @return when key was found and removed it return <code>true</code>
     *         otherwise it return <code>false</code>
     */
    public boolean remove(Integer key);

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
    public boolean updateNodeValue(Integer nodeIdToUpdate, Integer nodeMaxValue);

    /**
     * About half of keys will be copied to <code>node</code>.
     * <p>
     * From this node will be created structure: thisNode ---&gt; node
     * </p>
     * 
     * @param node
     *            required empty node
     */
    public void moveTopHalfOfDataTo(Node node);

    /**
     * Return max key, that could be use for representing this nide.
     * 
     * @return
     */
    public Integer getMaxKey();

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
    public Integer getCorrespondingNodeId(Integer key);

    public Integer getPreviousCorrespondingNode(Integer key);

    /**
     * Find value for given key.
     * 
     * @param key
     *            required key
     * @return found value if there is any, when value is <code>null</code> or
     *         there is no such key <code>null</code> is returned.
     */
    public Integer getValue(Integer key);

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
    public List<Integer> getKeys();

    /**
     * Allows to set max key value.
     * 
     * @param maxKey
     *            max key value, could be <code>null</code>
     */
    public void setMaxKeyValue(Integer maxKey);

    /**
     * Get max value.
     * 
     * @return max value stored in node, could be <code>null</code>
     */
    public Integer getMaxValue();

    /**
     * Verify that node is consistent.
     * 
     * @return <code>true</code> when node is consistent otherwise return
     *         <code>false</code>
     */
    public boolean verify();

    /**
     * @return the l
     */
    public int getL();

    public void writeTo(StringBuilder buff, String intendation);

    /**
     * @return the field
     */
    @Deprecated
    public Integer[] getField();

    public byte[] getFieldBytes();

}