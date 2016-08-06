package com.coroptis.jblinktree;

import com.coroptis.jblinktree.type.TypeDescriptor;
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
 * 
 * @author jajir
 * 
 * @param <K>
 *            key type
 * @param <V>
 *            value type
 */
public class JbTreeDataImpl<K, V> implements JbTreeData<K, V> {

    private Integer rootNodeId;

    private final int l;

    private final JbNodeDef<K, V> leafNodeDescriptor;

    private final JbNodeDef<K, Integer> nonLeafNodeDescriptor;

    public JbTreeDataImpl(final Integer startNodeId, final int l,
	    final TypeDescriptor<K> keyTypeDescriptor, final TypeDescriptor<V> valueTypeDescriptor,
	    final TypeDescriptor<Integer> linkTypeDescriptor) {
	this.rootNodeId = startNodeId;
	this.l = l;
	Preconditions.checkNotNull(keyTypeDescriptor);
	Preconditions.checkNotNull(valueTypeDescriptor);
	Preconditions.checkNotNull(linkTypeDescriptor);
	leafNodeDescriptor = new JbNodeDefImpl<K, V>(l, keyTypeDescriptor, valueTypeDescriptor,
		linkTypeDescriptor);
	nonLeafNodeDescriptor = new JbNodeDefImpl<K, Integer>(l, keyTypeDescriptor,
		linkTypeDescriptor, linkTypeDescriptor);
    }

    @Override
    public Integer getRootNodeId() {
	return rootNodeId;
    }

    /**
     * @param rootNodeId
     *            the rootNodeId to set
     */
    @Override
    public void setRootNodeId(Integer rootNodeId) {
	this.rootNodeId = rootNodeId;
    }

    /**
     * @return the leafNodeDescriptor
     */
    @Override
    public JbNodeDef<K, V> getLeafNodeDescriptor() {
        return leafNodeDescriptor;
    }

    /**
     * @return the nonLeafNodeDescriptor
     */
    @Override
    public JbNodeDef<K, Integer> getNonLeafNodeDescriptor() {
        return nonLeafNodeDescriptor;
    }

    /**
     * @return the l
     */
    @Override
    public int getL() {
        return l;
    }

}
