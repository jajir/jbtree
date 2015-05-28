package com.coroptis.jblinktree;

import java.util.Stack;

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

/**
 * Provide tree operations.
 * 
 * @author jajir
 * 
 */
public interface JbTreeService {

    /**
     * Non locking method that find leaf node id where should be given key
     * placed. In Stock are stored passed nodes. Right moved in tree are not
     * stored.
     * <p>
     * When it's necessary to move right in stack are stored just rightmost
     * nodes id.
     * </p>
     * <p>
     * Method doesn't lock any nodes.
     * </p>
     * 
     * @param key
     *            required key
     * @param stack
     *            required stack
     * @param rootNodeId
     *            required nodeId
     * @return leaf node id where should be key found or stored, it's never
     *         <code>null</code>
     */
    Integer findLeafNodeId(Integer key, Stack<Integer> stack, Integer rootNodeId);

    Node loadParentNode(Node currentNode, Integer tmpKey, Integer nextNodeId);

    void fillPathToNode(final Integer key, final Integer nodeId, final Stack<Integer> stack,
	    final Integer rootNodeId);
}
