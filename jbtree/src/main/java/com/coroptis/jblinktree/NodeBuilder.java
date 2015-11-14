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
 * Helps create nodes.
 * 
 * @author jajir
 * 
 * @param <K>
 *            key type
 * @param <V>
 *            value type
 */
public interface NodeBuilder<K, V> {

    /**
     * Create empty node.
     * 
     * @param idNode
     *            required node id
     * @return created empty node
     */
    Node<K, V> makeEmptyLeafNode(int idNode);

    /**
     * Create empty non-leaf node.
     * 
     * @param idNode
     *            required node id
     * @return created empty node
     */
    Node<K, Integer> makeEmptyNonLeafNode(final int idNode);

    /**
     * Create new node by copying array to new node.
     * 
     * @param idNode
     *            required node id
     * @param field
     *            required byte array
     * @return newly created node
     */
    <T> Node<K, T> makeNode(int idNode, byte field[]);

    /**
     * Create non-leaf node. Method allows insert two key value pairs
     * 
     * @param idNode
     *            required node id
     * @param value1
     *            required value 1
     * @param key1
     *            required key 1
     * @param value2
     *            required value 2
     * @param key2
     *            required key 2
     * @return newly created non-leaf node
     */
    Node<K, Integer> makeNonLeafNode(int idNode, Integer value1, K key1, Integer value2, K key2);

}
