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

/**
 * Factory for nodes.
 * 
 * @author jajir
 * 
 * @param <K>
 *            key type
 * @param <V>
 *            value type
 */
public class NodeBuilderImpl<K, V> implements NodeBuilder<K, V> {

    private final int l;

    private final TypeDescriptor<K> keyTypeDescriptor;

    private final TypeDescriptor<V> valueTypeDescriptor;

    private final TypeDescriptor<Integer> linkTypeDescriptor;

    public NodeBuilderImpl(final JbTreeData<K, V> treeData) {
	this.l = treeData.getL();
	this.keyTypeDescriptor = treeData.getKeyTypeDescriptor();
	this.valueTypeDescriptor = treeData.getValueTypeDescriptor();
	this.linkTypeDescriptor = treeData.getLinkTypeDescriptor();
    }

    /**
     * Implementations is not
     */

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public <T> Node<K, T> makeNode(final int idNode, final byte field[]) {
	byte flag = field[0];
	if (flag == Node.M) {
	    // leaf node
	   final Field<K, T> f = new FieldImpl(field, keyTypeDescriptor, valueTypeDescriptor,
		    linkTypeDescriptor);
	    return (Node<K, T>) new NodeImpl(l, idNode, f, keyTypeDescriptor, valueTypeDescriptor,
		    linkTypeDescriptor);
	} else {
	    // non-leaf node
	    final Field<K, T> f = new FieldImpl(field, keyTypeDescriptor, linkTypeDescriptor,
		    linkTypeDescriptor);
	    return (Node<K, T>) new NodeImpl(l, idNode, f, keyTypeDescriptor, linkTypeDescriptor,
		    linkTypeDescriptor);
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
	return new NodeImpl<K, Integer>(l, idNode, false, keyTypeDescriptor, linkTypeDescriptor,
		linkTypeDescriptor);
    }

    @Override
    public Node<K, Integer> makeNonLeafNode(final int idNode, final Integer value1, final K key1,
	    final Integer value2, final K key2) {
	final byte b[] = new byte[1 + keyTypeDescriptor.getMaxLength() * 2
		+ linkTypeDescriptor.getMaxLength() * 2 + linkTypeDescriptor.getMaxLength()];
	b[0] = 0; // it's non-leaf node.
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

	final Field<K, Integer> f = new FieldImpl<K, Integer>(b, keyTypeDescriptor, linkTypeDescriptor,
		linkTypeDescriptor);
	return new NodeImpl<K, Integer>(l, idNode, f, keyTypeDescriptor, linkTypeDescriptor,
		linkTypeDescriptor);
    }
}
