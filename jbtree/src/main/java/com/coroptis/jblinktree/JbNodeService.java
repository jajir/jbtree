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
 * Provide basic operations with node.
 *
 * @author jajir
 *
 * @param <K>
 *            key type
 * @param <V>
 *            value type
 */
public interface JbNodeService<K, V> {

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
     * @param node
     *            required node where will be searching
     * @param key
     *            required key
     * @return node id, in case of rightmost node it returns <code>null</code>
     *         because link is empty
     */
    Integer getCorrespondingNodeId(Node<K, Integer> node, K key);

    /**
     * Insert or override some value in node.
     *
     * @param node
     *            required node where will be key and value inserted
     * @param key
     *            required key
     * @param value
     *            required value
     * @return if inserts is about rewriting value than old value is returned
     *         otherwise <code>null</code> is returned
     * @throws NullPointerException
     *             when key or value is null
     * @param <S>
     *            node value type
     */
    <S> S insert(Node<K, S> node, K key, S value);

    /**
     * For non-leaf tree it update key of some tree. It's useful for update
     * sub-node max key.
     *
     * @param node
     *            required node where will be value updated
     * @param valueToUpdate
     *            required value which will be find
     * @param keyToSet
     *            required key that will be set to find value
     * @return return <code>true</code> when valueToUpdate was found and key was
     *         really updated otherwise return <code>false</code>
     */
    boolean updateKeyForValue(Node<K, Integer> node, Integer valueToUpdate,
            K keyToSet);

    /**
     * Get list of all node id stored in this node.
     *
     * @param node
     *            required node
     * @return list of id
     */
    List<Integer> getNodeIds(Node<K, Integer> node);

    /**
     * Write node content into {@link StringBuilder}.
     *
     * @param buff
     *            required {@link StringBuilder} instance
     * @param intendation
     *            how many white spaces should be added before each line.
     * @param node
     *            required node
     * @param <S>
     *            node value type
     */
    <S> void writeTo(Node<K, S> node, StringBuilder buff, String intendation);

    /**
     * Remove key and associated value from node.
     *
     * @param key
     *            required key to remove
     * @param node
     *            required node where will be key removed
     * @param <S>
     *            node value type
     * @return when key was found and removed it return <code>true</code>
     *         otherwise it return <code>false</code>
     * @throws NullPointerException
     *             when key or value is null
     */
    <S> S remove(final Node<K, S> node, final K key);

    /**
     * Find value for given key.
     * <p>
     * Method is not fast and should not be called in main search algorithm.
     * </p>
     *
     * @param key
     *            required key
     * @param node
     *            required node
     * @return found value if there is any, when value is <code>null</code> or
     *         there is no such key <code>null</code> is returned.
     */
    V getValueByKey(Node<K, V> node, K key);

}
