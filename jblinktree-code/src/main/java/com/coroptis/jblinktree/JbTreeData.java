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
 * Holds tree meta data. Like id of first node and tree data definition.
 *
 * @author jajir
 *
 * @param <K>
 *            key type
 * @param <V>
 *            value type
 */
public interface JbTreeData<K, V> {

    /**
     * Holds root node id.
     *
     * @return root node id
     */
    Integer getRootNodeId();

    /**
     * Allows to set root node id.
     *
     * @param rootNodeId
     *            required root node id
     */
    void setRootNodeId(Integer rootNodeId);

    /**
     * @return the leafNodeDescriptor
     */
    JbNodeDef<K, V> getLeafNodeDescriptor();

    /**
     * @return the nonLeafNodeDescriptor
     */
    JbNodeDef<K, Integer> getNonLeafNodeDescriptor();

    /**
     * Get max number of key value pairs in tree node.
     *
     * @return L parameter
     */
    int getL();

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
    Integer getMaxNodeId();

    /**
     * Allows to set maximal node is.
     *
     * @param maxNodeId
     *            required new max node id
     */
    void setMaxNodeId(Integer maxNodeId);

}
