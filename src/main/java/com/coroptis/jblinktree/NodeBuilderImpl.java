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
import com.coroptis.jblinktree.type.TypeDescriptorInteger;

public class NodeBuilderImpl<K, V> implements NodeBuilder<K, V> {

    private final int l;

    private final TypeDescriptor<K> keyTypeDescriptor;

    private final TypeDescriptor<V> valueTypeDescriptor;

    private final TypeDescriptor<Integer> linkTypeDescriptor;

    public NodeBuilderImpl(final int l, final TypeDescriptor<K> keyTypeDescriptor,
	    final TypeDescriptor<V> valueTypeDescriptor) {
	this.l = l;
	this.keyTypeDescriptor = keyTypeDescriptor;
	this.valueTypeDescriptor = valueTypeDescriptor;
	this.linkTypeDescriptor = new TypeDescriptorInteger();
    }

    @Override
    public <T> Node<K, T> makeNode(final int idNode, final byte field[],
	    final TypeDescriptor<T> valueTypeDescriptor) {
	byte flag = field[0];
	if (flag == Node.M) {
	    // leaf node
	    return new NodeImpl<K, T>(l, idNode, field, keyTypeDescriptor, valueTypeDescriptor);
	} else {
	    // non-leaf node
	    return (Node<K, T>) new NodeImpl<K, Integer>(l, idNode, field, keyTypeDescriptor,
		    new TypeDescriptorInteger());
	}
    }

@Override
    public Node<K, Integer> makeNonLeafNode(final int idNode, final Integer value1, final K key1,
	    final Integer value2, final K key2) {
	byte b[] = new byte[1 + keyTypeDescriptor.getMaxLength() * 2
		+ valueTypeDescriptor.getMaxLength() * 2 + linkTypeDescriptor.getMaxLength()];
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

	return new NodeImpl<K, Integer>(l, idNode, b, keyTypeDescriptor, linkTypeDescriptor);
    }
}
