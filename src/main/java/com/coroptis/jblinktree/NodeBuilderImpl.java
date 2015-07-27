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
import com.google.common.base.Preconditions;

public class NodeBuilderImpl<K, V> implements NodeBuilder<K, V> {

    private final int l;

    private final TypeDescriptor<K> keyTypeDescriptor;

    private final TypeDescriptor<V> valueTypeDescriptor;

    private final TypeDescriptor<Integer> linkTypeDescriptor;

    public NodeBuilderImpl(final int l, final TypeDescriptor<K> keyTypeDescriptor,
	    final TypeDescriptor<V> valueTypeDescriptor,
	    final TypeDescriptor<Integer> linkTypeDescriptor) {
	this.l = l;
	this.keyTypeDescriptor = Preconditions.checkNotNull(keyTypeDescriptor);
	this.valueTypeDescriptor = Preconditions.checkNotNull(valueTypeDescriptor);
	this.linkTypeDescriptor = Preconditions.checkNotNull(linkTypeDescriptor);
    }

    /**
     * Implementations is not
     */
    @SuppressWarnings("unchecked")
    @Override
    public <T> Node<K, T> makeNode(final int idNode, final byte field[]) {
	byte flag = field[0];
	if (flag == Node.M) {
	    // leaf node
	    return (Node<K, T>) new NodeImpl<K, V>(l, idNode, field, keyTypeDescriptor,
		    valueTypeDescriptor, linkTypeDescriptor);
	} else {
	    // non-leaf node
	    return (Node<K, T>) new NodeImpl<K, Integer>(l, idNode, field, keyTypeDescriptor,
		    linkTypeDescriptor, linkTypeDescriptor);
	}
    }

    @Override
    public Node<K, V> makeEmptyLeafNode(final int idNode) {
	Preconditions.checkNotNull(idNode);
	return new NodeImpl<K, V>(l, idNode, true, keyTypeDescriptor, valueTypeDescriptor,
		linkTypeDescriptor);
    }

    @Override
    public Node<K, Integer> makeEmptyNonLeafNode(final int idNode) {
	Preconditions.checkNotNull(idNode);
	return new NodeImpl<K, Integer>(l, idNode, true, keyTypeDescriptor, linkTypeDescriptor,
		linkTypeDescriptor);
    }

    @Override
    public Node<K, Integer> makeNonLeafNode(final int idNode, final Integer value1, final K key1,
	    final Integer value2, final K key2) {
	byte b[] = new byte[1 + keyTypeDescriptor.getMaxLength() * 2
		+ linkTypeDescriptor.getMaxLength() * 2 + linkTypeDescriptor.getMaxLength()];
	b[0] = 0; // it's non-lef node.
	int position = 1;
	// pair 1
	linkTypeDescriptor.save(b, position, value1);
	position += linkTypeDescriptor.getMaxLength();
	keyTypeDescriptor.save(b, position, key1);
	position += keyTypeDescriptor.getMaxLength();

	// pair 2
	linkTypeDescriptor.save(b, position, value2);
	position += linkTypeDescriptor.getMaxLength();
	keyTypeDescriptor.save(b, position, key2);
	position += keyTypeDescriptor.getMaxLength();

	linkTypeDescriptor.save(b, position, NodeImpl.EMPTY_INT);

	return new NodeImpl<K, Integer>(l, idNode, b, keyTypeDescriptor, linkTypeDescriptor,
		linkTypeDescriptor);
    }

    @Override
    public Node<K, V> makeLeafNode(final int idNode, final V value1, final K key1, final V value2,
	    final K key2) {
	byte b[] = new byte[1 + keyTypeDescriptor.getMaxLength() * 2
		+ valueTypeDescriptor.getMaxLength() * 2 + linkTypeDescriptor.getMaxLength()];
	b[0] = Node.M; // it's non-lef node.
	int position = 1;
	// pair 1
	valueTypeDescriptor.save(b, position, value1);
	position += valueTypeDescriptor.getMaxLength();
	keyTypeDescriptor.save(b, position, key1);
	position += keyTypeDescriptor.getMaxLength();

	// pair 2
	valueTypeDescriptor.save(b, position, value2);
	position += valueTypeDescriptor.getMaxLength();
	keyTypeDescriptor.save(b, position, key2);
	position += keyTypeDescriptor.getMaxLength();

	linkTypeDescriptor.save(b, position, NodeImpl.EMPTY_INT);

	return new NodeImpl<K, V>(l, idNode, b, keyTypeDescriptor, valueTypeDescriptor,
		linkTypeDescriptor);
    }
}
