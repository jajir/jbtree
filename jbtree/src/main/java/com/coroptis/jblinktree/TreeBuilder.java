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

import com.coroptis.jblinktree.type.TypeDescriptor;
import com.coroptis.jblinktree.type.TypeDescriptorInteger;
import com.google.common.base.Preconditions;

/**
 * Provide fluent API for creating tree.
 * 
 * @author jajir
 * 
 */
public final class TreeBuilder {

    private Integer l;
    private TypeDescriptor<?> keyTypeDescriptor;
    private TypeDescriptor<?> valueTypeDescriptor;

    //TODO documentation
    public static TreeBuilder builder() {
	return new TreeBuilder(5);
    }

    //TODO documentation
    private TreeBuilder(final Integer default_l) {
	this.l = default_l;
    }

    //TODO documentation
    public TreeBuilder setL(final Integer l) {
	this.l = l;
	return this;
    }

    //TODO documentation
    public TreeBuilder setKeyType(final TypeDescriptor<?> keyTypeDescriptor) {
	this.keyTypeDescriptor = keyTypeDescriptor;
	return this;
    }

    //TODO documentation
    public TreeBuilder setValueType(final TypeDescriptor<?> valueTypeDescriptor) {
	this.valueTypeDescriptor = valueTypeDescriptor;
	return this;
    }

    //TODO documentation
    @SuppressWarnings("unchecked")
    public <K, V> TreeMap<K, V> build() {
	Preconditions.checkNotNull(keyTypeDescriptor,
		"key TypeDescriptor is null, use .setKeyType in builder");
	Preconditions.checkNotNull(valueTypeDescriptor,
		"value TypeDescriptor is null, use .setValueType in builder");
	final TypeDescriptor<Integer> linkTypeDescriptor = new TypeDescriptorInteger();
	final IdGenerator idGenerator = new IdGeneratorImpl();
	final NodeBuilder<K, V> nodeBuilder = new NodeBuilderImpl<K, V>(l,
		(TypeDescriptor<K>) keyTypeDescriptor, (TypeDescriptor<V>) valueTypeDescriptor,
		linkTypeDescriptor);
	final NodeStoreImpl<K, V> nodeStore = new NodeStoreImpl<K, V>(idGenerator, nodeBuilder);
	final JbTreeTool<K, V> jbTreeTool = new JbTreeToolImpl<K, V>(nodeStore,
		(TypeDescriptor<K>) keyTypeDescriptor, nodeBuilder);
	final JbTreeData<K, V> treeData = new JbTreeDataImpl<K, V>(nodeStore, jbTreeTool);
	final JbTreeTraversingService<K, V> treeLockingTool = new JbTreeTraversingServiceImpl<K, V>(jbTreeTool);
	final JbTreeService<K, V> treeService = new JbTreeServiceImpl<K, V>(nodeStore,
		treeLockingTool);
	final JbTreeHelper<K, V> jbTreeHelper = new JbTreeHelperImpl<K, V>(l, nodeStore,
		jbTreeTool, treeService, treeData, (TypeDescriptor<V>) valueTypeDescriptor,
		linkTypeDescriptor);
	final JbTree<K, V> tree = new JbTreeImpl<K, V>(nodeStore, jbTreeTool, jbTreeHelper,
		treeData, treeLockingTool, treeService);

	return new TreeMapImpl<K, V>(tree, (TypeDescriptor<K>) keyTypeDescriptor,
		(TypeDescriptor<V>) valueTypeDescriptor);
    }
}
