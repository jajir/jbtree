package com.coroptis.jblinktree;

/**
 * Factory instance for short nodes.
 *
 * @author jajir
 *
 * @param <K>
 *            key type
 * @param <V>
 *            value type
 */
public final class JbNodeBuilderShort<K, V>
        extends AbstractJbNodeBuilder<K, V> {

    /**
     * Simple constructor.
     *
     * @param jbTreeData
     *            required tree data
     */
    public JbNodeBuilderShort(final JbTreeData<K, V> jbTreeData) {
        super(jbTreeData);
    }

    @Override
    public <T> Node<K, T> makeNode(final Integer idNode, final byte[] field,
            final JbNodeDef<K, T> jbNodeDef) {
        return new NodeShort<K, T>(idNode, field, jbNodeDef);
    }

    @Override
    public <T> Node<K, T> makeNode(final Integer nodeId,
            final boolean isLeafNode, final JbNodeDef<K, T> jbNodeDef) {
        return new NodeShort<K, T>(nodeId, isLeafNode, jbNodeDef);
    }

}
