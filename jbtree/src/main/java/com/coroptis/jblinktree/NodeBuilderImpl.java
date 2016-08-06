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

    private final JbTreeData<K, V> treeData;

    public NodeBuilderImpl(final JbTreeData<K, V> treeData) {
	this.treeData = treeData;
    }

    /**
     * Implementations is not
     */

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public <T> Node<K, T> makeNode(final Integer idNode, final byte field[]) {
	byte flag = field[0];
	if (flag == Node.M) {
	    // leaf node
	    final Field<K, T> f = new FieldImpl(field, treeData.getLeafNodeDescriptor());
	    return (Node<K, T>) new NodeImpl(idNode, f);
	} else {
	    // non-leaf node
	    final Field<K, T> f = new FieldImpl(field, treeData.getNonLeafNodeDescriptor());
	    return (Node<K, T>) new NodeImpl(idNode, f);
	}
    }

    @Override
    public Node<K, V> makeEmptyLeafNode(final Integer idNode) {
	Preconditions.checkNotNull(idNode);
	return new NodeImpl<K, V>(idNode, true, treeData);
    }

    @Override
    public Node<K, Integer> makeEmptyNonLeafNode(final Integer idNode) {
	Preconditions.checkNotNull(idNode);
	return new NodeImpl<K, Integer>(idNode, false, (JbTreeData<K, Integer>) treeData);
    }

    @Override
    public Node<K, Integer> makeNonLeafNode(final Integer idNode, final Integer value1,
	    final K key1, final Integer value2, final K key2) {
	final byte b[] = new byte[1 + treeData.getKeyTypeDescriptor().getMaxLength() * 2
		+ treeData.getLinkTypeDescriptor().getMaxLength() * 2
		+ treeData.getLinkTypeDescriptor().getMaxLength()];
	b[0] = 0; // it's non-leaf node.
	int position = 1;
	// pair 1
	treeData.getLinkTypeDescriptor().save(b, position, value1);
	position += treeData.getLinkTypeDescriptor().getMaxLength();
	treeData.getKeyTypeDescriptor().save(b, position, key1);
	position += treeData.getKeyTypeDescriptor().getMaxLength();

	// pair 2
	treeData.getLinkTypeDescriptor().save(b, position, value2);
	position += treeData.getLinkTypeDescriptor().getMaxLength();
	treeData.getKeyTypeDescriptor().save(b, position, key2);
	position += treeData.getKeyTypeDescriptor().getMaxLength();

	treeData.getLinkTypeDescriptor().save(b, position, NodeImpl.EMPTY_INT);

	final Field<K, Integer> f = new FieldImpl<K, Integer>(b, (JbTreeData<K, Integer>) treeData);
	return new NodeImpl<K, Integer>(idNode, f);
    }
}
