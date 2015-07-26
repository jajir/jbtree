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

/**
 * Helps create modes.
 * 
 * @author jan
 * 
 * @param <K>
 * @param <V>
 */
public interface NodeBuilder<K, V> {

    <T> Node<K, T> makeNode(final int idNode, final byte field[],
	    final TypeDescriptor<T> valueTypeDescriptor);

    Node<K, Integer> makeNonLeafNode(final int idNode, final Integer value1, final K key1,
	    final Integer value2, final K key2);

    Node<K, V> makeLeafNode(final int idNode, final V value1, final K key1, final V value2,
	    final K key2);

}
