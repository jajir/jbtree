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

    //TODO javadoc
    Node<K, V> makeEmptyLeafNode(int idNode);

    //TODO javadoc
    Node<K, Integer> makeEmptyNonLeafNode(int idNode);

    //TODO javadoc
    <T> Node<K, T> makeNode(int idNode, byte field[]);

    //TODO javadoc
    Node<K, Integer> makeNonLeafNode(int idNode, Integer value1, K key1, Integer value2, K key2);

    //TODO javadoc
    Node<K, V> makeLeafNode(int idNode, V value1, K key1, V value2, K key2);

}
