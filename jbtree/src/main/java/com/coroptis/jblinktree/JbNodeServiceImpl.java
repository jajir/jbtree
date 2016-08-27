package com.coroptis.jblinktree;

public class JbNodeServiceImpl<K, V> implements JbNodeService<K, V> {

    @Override
    public Integer getCorrespondingNodeId(final Node<K, Integer> node,
            final K key) {
        if (node.isLeafNode()) {
            throw new JblinktreeException("Leaf node '" + node.getId()
                    + "' doesn't have any child nodes.");
        }
        if (node.isEmpty()) {
            return node.getLink();
        }
        for (int i = 0; i < node.getKeyCount(); i++) {
            if (node.getNodeDef().getKeyTypeDescriptor().compareValues(key,
                    node.getKey(i)) <= 0) {
                return node.getValue(i);
            }
        }
        return node.getLink();
    }

}
