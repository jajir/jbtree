package com.coroptis.jblinktree.store;
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

import com.coroptis.jblinktree.Node;

/**
 * Allows to convert Node&lt;K,V&gt; to Node&lt;K,Interer&gt; and convert it
 * back.
 *
 * @author jajir
 *
 * @param <K>
 *            key type
 * @param <V>
 *            value type
 */
public interface NodeConverter<K, V> {

    /**
     * Convert Node&lt;K,V&gt; to Node&lt;K,Integer&gt;. Integers will be empty.
     *
     * @param node
     *            required node
     * @return converted node
     */
    Node<K, Integer> convertToKeyInt(Node<K, V> node);

    /**
     * Convert Node&lt;K,Integer&gt; to Node&lt;K,V&gt;. Values will be empty.
     *
     * @param node
     *            required node
     * @return converted node
     */
    Node<K, V> convertToKeyValue(Node<K, Integer> node);

}
