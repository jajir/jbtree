package com.coroptis.jblinktree;

/**
 * Factory instance for fixed length nodes.
 *
 * @author jajir
 *
 * @param <K>
 *            key type
 * @param <V>
 *            value type
 */
public final class JbNodeBuilderFixedLength<K, V>
        extends AbstractJbNodeBuilder<K, V> {

    /**
     * Simple constructor.
     *
     * @param jbTreeData
     *            required tree data
     */
    public JbNodeBuilderFixedLength(final JbTreeData<K, V> jbTreeData) {
        super(jbTreeData);
    }

    @Override
    public <T> Node<K, T> makeNode(final Integer idNode, final byte[] field,
            final JbNodeDef<K, T> jbNodeDef) {
        return new NodeFixedLength<K, T>(idNode, field, jbNodeDef);
    }

    @Override
    public <T> Node<K, T> makeNode(final Integer nodeId,
            final boolean isLeafNode, final JbNodeDef<K, T> jbNodeDef) {
        return new NodeFixedLength<K, T>(nodeId, isLeafNode, jbNodeDef);
    }
}
