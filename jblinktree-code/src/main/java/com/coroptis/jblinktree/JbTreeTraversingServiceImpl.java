package com.coroptis.jblinktree;

import java.util.Objects;

import com.coroptis.jblinktree.type.Wrapper;
import com.coroptis.jblinktree.util.JblinktreeException;

/**
 * Implementation of {@link JbTreeTraversingService}.
 *
 * @author jajir
 *
 * @param <K>
 *            key type
 * @param <V>
 *            value type
 */
public final class JbTreeTraversingServiceImpl<K, V>
        implements JbTreeTraversingService<K, V> {

    /**
     * Tree tool.
     */
    private final JbTreeTool<K, V> treeTool;

    /**
     * Node service.
     */
    private final JbNodeService<K, V> nodeService;

    /**
     * Simple constructor.
     *
     * @param initTreeTool
     *            required tree tool
     * @param jbNodeService
     *            node service
     */
    public JbTreeTraversingServiceImpl(final JbTreeTool<K, V> initTreeTool,
            final JbNodeService<K, V> jbNodeService) {
        this.treeTool = Objects.requireNonNull(initTreeTool);
        this.nodeService = Objects.requireNonNull(jbNodeService);
    }

    @Override
    public Node<K, Integer> moveRightNonLeafNode(final Node<K, Integer> node,
            final Wrapper<K> key) {
        Objects.requireNonNull(key);
        Objects.requireNonNull(node);
        Node<K, Integer> current = node;
        if (current.isLeafNode()) {
            throw new JblinktreeException(
                    "method is for non-leaf nodes, but given node is leaf: "
                            + current.toString());
        }
        Integer nextNodeId = nodeService.getCorrespondingNodeId(current, key);
        while (!NodeShort.EMPTY_INT.equals(nextNodeId)
                && nextNodeId.equals(current.getLink())) {
            current = treeTool.moveToNextNode(current, nextNodeId);
            nextNodeId = nodeService.getCorrespondingNodeId(current, key);
        }
        return current;
    }

    @Override
    public Node<K, V> moveRightLeafNode(final Node<K, V> node,
            final Wrapper<K> key) {
        Objects.requireNonNull(key);
        Objects.requireNonNull(node);
        Node<K, V> current = node;
        if (!current.isLeafNode()) {
            throw new JblinktreeException(
                    "method is for leaf nodes, but given node is non-leaf");
        }
        while (treeTool.canMoveToNextNode(current, key)) {
            current = treeTool.moveToNextNode(current, current.getLink());
        }
        return current;
    }

}
