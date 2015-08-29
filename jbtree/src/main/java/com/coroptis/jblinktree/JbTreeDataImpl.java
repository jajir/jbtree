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

import java.util.concurrent.locks.ReentrantLock;

import com.google.common.base.Preconditions;

/**
 * 
 * @author jajir
 * 
 * @param <K>
 *            key type
 * @param <V>
 *            value type
 */
public class JbTreeDataImpl<K, V> implements JbTreeData<K> {

    private final NodeStore<K> nodeStore;

    private final JbTreeTool<K, V> treeTool;

    private Integer rootNodeId;

    JbTreeDataImpl(final NodeStore<K> nodeStore, final JbTreeTool<K, V> treeTool) {
	this.nodeStore = Preconditions.checkNotNull(nodeStore);
	this.treeTool = Preconditions.checkNotNull(treeTool);
	rootNodeId = treeTool.createRootNode();
    }

    @Override
    public <S> Integer splitRootNode(final Node<K, S> currentNode, final Node<K, S> newNode) {
	ReentrantLock lock = new ReentrantLock(false);
	lock.lock();
	try {
	    if (rootNodeId.equals(currentNode.getId())) {
		Preconditions.checkArgument(rootNodeId.equals(currentNode.getId()));
		rootNodeId = treeTool.splitRootNode(currentNode, newNode);
		nodeStore.unlockNode(currentNode.getId());
	    } else {
		nodeStore.unlockNode(currentNode.getId());
	    }
	} finally {
	    lock.unlock();
	}
	return rootNodeId;
    }

    @Override
    public Integer getRootNodeId() {
	return rootNodeId;
    }

}
