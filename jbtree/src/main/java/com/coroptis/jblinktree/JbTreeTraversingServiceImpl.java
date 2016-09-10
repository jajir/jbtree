package com.coroptis.jblinktree;

import com.coroptis.jblinktree.type.Wrapper;

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
        this.treeTool = Preconditions.checkNotNull(initTreeTool);
        this.nodeService = Preconditions.checkNotNull(jbNodeService);
    }

    @Override
    public Node<K, Integer> moveRightNonLeafNode(final Node<K, Integer> node,
            final Wrapper<K> key) {
        Preconditions.checkNotNull(key);
        Preconditions.checkNotNull(node);
        Node<K, Integer> current = node;
        if (current.isLeafNode()) {
            throw new JblinktreeException(
                    "method is for non-leaf nodes, but given node is leaf: "
                            + current.toString());
        }
        Integer nextNodeId = nodeService.getCorrespondingNodeId(current, key);
        while (!NodeImpl.EMPTY_INT.equals(nextNodeId)
                && nextNodeId.equals(current.getLink())) {
            current = treeTool.moveToNextNode(current, nextNodeId);
            nextNodeId = nodeService.getCorrespondingNodeId(current, key);
        }
        return current;
    }

    @Override
    public Node<K, V> moveRightLeafNode(final Node<K, V> node,
            final Wrapper<K> key) {
        Preconditions.checkNotNull(key);
        Preconditions.checkNotNull(node);
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
