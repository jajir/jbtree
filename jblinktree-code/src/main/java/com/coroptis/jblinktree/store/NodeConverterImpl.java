package com.coroptis.jblinktree.store;

import java.util.Objects;

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

import com.coroptis.jblinktree.JbNodeBuilder;
import com.coroptis.jblinktree.JbTreeData;
import com.coroptis.jblinktree.Node;
import com.coroptis.jblinktree.type.Wrapper;
import com.coroptis.jblinktree.util.Validation;

/**
 * Immutable implementation of {@link NodeConverter}.
 *
 * @author jajir
 *
 * @param <K>
 *            key type
 * @param <V>
 *            value type
 */
public final class NodeConverterImpl<K, V> implements NodeConverter<K, V> {

    /**
     * Tree meta information.
     */
    private final JbTreeData<K, V> treeData;

    /**
     * Node builder factory.
     */
    private final JbNodeBuilder<K, V> nodeBuilder;

    /**
     *
     * @param jbTreeData
     *            required tree meta information
     * @param jbNodeBuilder
     *            required node builder factory
     */
    public NodeConverterImpl(final JbTreeData<K, V> jbTreeData,
            final JbNodeBuilder<K, V> jbNodeBuilder) {
        this.treeData = Objects.requireNonNull(jbTreeData);
        this.nodeBuilder = Objects.requireNonNull(jbNodeBuilder);
    }

    @Override
    public Node<K, Integer> convertToKeyInt(final Node<K, V> node) {
        byte[] b = new byte[treeData.getNonLeafNodeDescriptor()
                .getFieldActualLength(node.getKeyCount())];
        b[0] = Node.FLAG_LEAF_NODE;
        Node<K, Integer> out = nodeBuilder.makeNode(node.getId(), b,
                treeData.getNonLeafNodeDescriptor());
        Validation.checkState(out.isLeafNode());
        for (int i = 0; i < node.getKeyCount(); i++) {
            out.setKey(i, Wrapper.make(node.getKey(i),
                    node.getNodeDef().getKeyTypeDescriptor()));
        }
        out.setLink(node.getLink());
        return out;
    }

    @Override
    public Node<K, V> convertToKeyValue(final Node<K, Integer> node) {
        byte[] b = new byte[treeData.getLeafNodeDescriptor()
                .getFieldActualLength(node.getKeyCount())];
        b[0] = Node.FLAG_LEAF_NODE;
        Node<K, V> out = nodeBuilder.makeNode(node.getId(), b);
        Validation.checkState(out.isLeafNode());
        for (int i = 0; i < node.getKeyCount(); i++) {
            out.setKey(i, Wrapper.make(node.getKey(i),
                    node.getNodeDef().getKeyTypeDescriptor()));
        }
        out.setLink(node.getLink());
        return out;
    }

}
