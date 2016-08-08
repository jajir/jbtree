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

import com.coroptis.jblinktree.type.TypeDescriptor;

public interface JbNodeDef<K, V> {

    /**
     * Number of bytes occupied by node flag.
     */
    static int FLAGS_LENGTH = 1;

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

    /**
     * What is maximum record length in bytes. In this case node contains L key
     * value pairs.
     * 
     * @return maximum length in bytes
     */
    int getRecordMaxLength();

    /**
     * What is actual record length in bytes. Number include:
     * <ul>
     * <li>node flags</li>
     * <li>key value pair length * numberOfKeys</li>
     * <li>next link</li>
     * </ul>
     * 
     * @param numberOfKeys
     *            required number of keys in node
     * @return maximum length in bytes
     */
    int getRecordActualLength(int numberOfKeys);

    /**
     * return size of key and value in bytes.
     * 
     * @return key and value size
     */
    int getKeyAndValueSize();

}