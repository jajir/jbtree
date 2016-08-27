package com.coroptis.jblinktree.store;

import com.coroptis.jblinktree.JbNodeBuilder;
import com.coroptis.jblinktree.JbTreeData;
import com.coroptis.jblinktree.Node;
import com.google.common.base.Preconditions;

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
        this.treeData = Preconditions.checkNotNull(jbTreeData);
        this.nodeBuilder = Preconditions.checkNotNull(jbNodeBuilder);
    }

    @Override
    public Node<K, Integer> convertToKeyInt(final Node<K, V> node) {
        byte[] b = new byte[treeData.getNonLeafNodeDescriptor()
                .getFieldActualLength(node.getKeyCount())];
        b[0] = Node.M;
        Node<K, Integer> out = nodeBuilder.makeNode(node.getId(), b,
                treeData.getNonLeafNodeDescriptor());
        Preconditions.checkState(out.isLeafNode());
        for (int i = 0; i < node.getKeyCount(); i++) {
            out.setKey(i, node.getKey(i));
        }
        out.setLink(node.getLink());
        return out;
    }

    @Override
    public Node<K, V> convertToKeyValue(final Node<K, Integer> node) {
        byte[] b = new byte[treeData.getLeafNodeDescriptor()
                .getFieldActualLength(node.getKeyCount())];
        b[0] = Node.M;
        Node<K, V> out = nodeBuilder.makeNode(node.getId(), b);
        Preconditions.checkState(out.isLeafNode());
        for (int i = 0; i < node.getKeyCount(); i++) {
            out.setKey(i, node.getKey(i));
        }
        out.setLink(node.getLink());
        return out;
    }

}
