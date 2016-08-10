package com.coroptis.jblinktree;

import com.coroptis.jblinktree.util.JbStack;

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
 *
 * @author jajir
 *
 * @param <K>
 *            key type
 * @param <V>
 *            value type
 */
public interface JbTreeHelper<K, V> {

    /**
     * Find leaf node where given key belongs. Method doesn't lock and nodes.
     *
     * @param key
     *            required key
     * @return appropriate leaf node
     */
    Node<K, V> findAppropriateLeafNode(K key);

    /**
     * Write given key value pair into leaf node.
     *
     * @param currentNode
     *            required leaf node
     * @param key
     *            required key
     * @param value
     *            required value
     * @param stack
     *            required stack useful for back tracing through tree
     * @return <code>null</code> when it's new key otherwise return old value
     */
    V insertToLeafNode(Node<K, V> currentNode, K key, V value, JbStack stack);

}
