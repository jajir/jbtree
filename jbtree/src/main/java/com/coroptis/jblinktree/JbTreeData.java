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
 * Holds data specific for tree.
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
     * Get max number of key value pairs in tree node.
     * 
     * @return L parameter
     */
    int getL();

    /**
     * Return key type descriptor.
     * 
     * @return key type descriptor
     */
    TypeDescriptor<K> getKeyTypeDescriptor();

    /**
     * Return value type descriptor.
     * 
     * @return value type descriptor
     */
    TypeDescriptor<V> getValueTypeDescriptor();

    /**
     * Return next link type descriptor.
     * 
     * @return next link type descriptor
     */
    TypeDescriptor<Integer> getLinkTypeDescriptor();

}
