package com.coroptis.jblinktree;

import com.google.common.base.Preconditions;

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
 * Implementation of {@link JbTreeService}.
 * 
 * @author jajir
 * 
 */
public class JbTreeServiceImpl<K, V> implements JbTreeService<K, V> {

    private final NodeStore<K> nodeStore;

    private final JbTreeTraversingService<K, V> treeTraversingService;

    public JbTreeServiceImpl(final NodeStore<K> nodeStore,
	    final JbTreeTraversingService<K, V> treeTraversingService) {
	this.nodeStore = Preconditions.checkNotNull(nodeStore);
	this.treeTraversingService = Preconditions.checkNotNull(treeTraversingService);
    }

    @Override
    public <S> Node<K, Integer> loadParentNode(final Node<K, S> currentNode, final K tmpKey,
	    final Integer nextNodeId) {
	Node<K, Integer> parentNode = nodeStore.getAndLock(nextNodeId);
	// TODO link to current node which key should be updated can be in
	// different node than tmpKey
	parentNode = treeTraversingService.moveRightNonLeafNode(parentNode, tmpKey);
	if (parentNode.updateNodeValue(currentNode.getId(), currentNode.getMaxKey())) {
	    nodeStore.writeNode(parentNode);
	}
	return parentNode;
    }

    @Override
    public void storeValueIntoLeafNode(final Node<K, V> currentNode, final K key, final V value) {
	currentNode.insert(key, value);
	nodeStore.writeNode(currentNode);
	nodeStore.unlockNode(currentNode.getId());
    }

    @Override
    public void storeValueIntoNonLeafNode(final Node<K, Integer> currentNode, final K key,
	    final Integer value) {
	currentNode.insert(key, value);
	nodeStore.writeNode(currentNode);
	nodeStore.unlockNode(currentNode.getId());
    }
}
