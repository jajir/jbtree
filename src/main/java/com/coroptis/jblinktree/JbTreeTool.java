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

/**
 * Provide simple operations with tree.
 * 
 * @author jajir
 * 
 */
public interface JbTreeTool {

    /**
     * For given key find corresponding node in which key should belongs.
     * <p>
     * Method doesn't lock any node.
     * </p>
     * 
     * @param node
     *            required node object
     * @param key
     *            required key
     * @return found node if there is such, when there is no corresponding key
     *         <code>null</code> is returned.
     */
    Node findCorrespondingNode(Node node, Integer key);

    /**
     * Move right in tree until suitable non-leaf node is found.
     * <p>
     * When there is move right than current node is unlocked and new one is
     * locked.
     * </p>
     * 
     * @param current
     *            required current node, this node should be locked
     * @param key
     *            required key
     * @return moved right node
     */
    Node moveRightLeafNode(Node current, Integer key);

    /**
     * Move right in tree until suitable leaf node is found.
     * <p>
     * When there is move right than current node is unlocked and new one is
     * locked.
     * </p>
     * 
     * @param current
     *            required current node, this node should be locked
     * @param key
     *            required key
     * @return moved right node
     */
    Node moveRightNonLeafNode(Node current, Integer key);

    /**
     * Split node into two nodes. It moved path of currentNode data int new one
     * which will be returned.
     * 
     * @param currentNode
     *            required node which will be split
     * @param key
     *            required key
     * @param tmpValue
     *            required value
     * @return
     */
    Node split(Node currentNode, Integer key, Integer tmpValue);

}
