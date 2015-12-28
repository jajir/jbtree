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

import java.util.Map;

import com.coroptis.jblinktree.type.TypeDescriptor;
import com.coroptis.jblinktree.type.TypeDescriptorInteger;
import com.coroptis.jblinktree.type.Types;
import com.google.common.base.Preconditions;

/**
 * Provide fluent API for creating tree. Default tree node parameter L is 5.
 * It's maximum number of key,value pairs in node.
 * 
 * @author jajir
 * 
 */
public final class TreeBuilder {

    private Integer l;
    private TypeDescriptor<?> keyTypeDescriptor;
    private TypeDescriptor<?> valueTypeDescriptor;
    private String treeWrapperFileName;

    /**
     * Create new instance of builder.
     * 
     * @return new builder instance.
     */
    public static TreeBuilder builder() {
	return new TreeBuilder(5);
    }

    /**
     * Private constructor for builder. Can't be called from outside.
     * 
     * @param default_l
     *            default value of L node parameter.
     */
    private TreeBuilder(final Integer default_l) {
	this.l = default_l;
	treeWrapperFileName = null;
    }

    /**
     * Allow to set tree node parameter L. It's maximum number of key value
     * pairs in node.
     * 
     * @param l
     *            required L parameter value
     * @return current tree builder instance
     */
    public TreeBuilder setL(final Integer l) {
	this.l = l;
	return this;
    }

    /**
     * Allow to set tree wrapper instance. Wrapper store tree nodes into file.
     * It'd debug tool and never should be used in production.
     * 
     * @param treeWrapperFileName
     *            required tree wrapper output file
     * @return current tree builder instance
     */
    public TreeBuilder setTreeWrapper(final String treeWrapperFileName) {
	this.treeWrapperFileName = Preconditions.checkNotNull(treeWrapperFileName);
	return this;
    }

    /**
     * Allows to set key type descriptor. Choose them from {@link Types} static
     * objects.
     * 
     * @param keyTypeDescriptor
     *            required key type descriptor
     * @return current tree builder instance
     */
    public TreeBuilder setKeyType(final TypeDescriptor<?> keyTypeDescriptor) {
	this.keyTypeDescriptor = keyTypeDescriptor;
	return this;
    }

    /**
     * Allows to set value type descriptor. Choose them from {@link Types}
     * static objects.
     * 
     * @param valueTypeDescriptor
     *            required value type descriptor
     * @return current tree builder instance
     */
    public TreeBuilder setValueType(final TypeDescriptor<?> valueTypeDescriptor) {
	this.valueTypeDescriptor = valueTypeDescriptor;
	return this;
    }

    /**
     * Build {@link Map} instance with previously given parameters.
     * 
     * @param <K>
     *            key type
     * @param <V>
     *            value type
     * @return {@link TreeMap} instance
     */
    @SuppressWarnings("unchecked")
    public <K, V> TreeMap<K, V> build() {
	Preconditions.checkNotNull(keyTypeDescriptor,
		"key TypeDescriptor is null, use .setKeyType in builder");
	Preconditions.checkNotNull(valueTypeDescriptor,
		"value TypeDescriptor is null, use .setValueType in builder");
	final TypeDescriptor<Integer> linkTypeDescriptor = new TypeDescriptorInteger();
	final JbTreeData<K, V> treeData = new JbTreeDataImpl<K, V>(NodeStore.FIRST_NODE_ID, l,
		(TypeDescriptor<K>) keyTypeDescriptor, (TypeDescriptor<V>) valueTypeDescriptor,
		linkTypeDescriptor);

	final NodeBuilder<K, V> nodeBuilder = new NodeBuilderImpl<K, V>(treeData);
	final NodeStoreImpl<K, V> nodeStore = new NodeStoreImpl<K, V>(nodeBuilder);
	final JbTreeTool<K, V> jbTreeTool = new JbTreeToolImpl<K, V>(nodeStore,
		(TypeDescriptor<K>) keyTypeDescriptor, nodeBuilder);
	final JbTreeTraversingService<K, V> treeLockingTool = new JbTreeTraversingServiceImpl<K, V>(
		jbTreeTool);
	final JbTreeService<K, V> treeService = new JbTreeServiceImpl<K, V>(nodeStore,
		treeLockingTool);
	final JbTreeHelper<K, V> jbTreeHelper = new JbTreeHelperImpl<K, V>(l, nodeStore, jbTreeTool,
		treeService, treeData);
	final JbTree<K, V> tree = new JbTreeImpl<K, V>(nodeStore, jbTreeTool, jbTreeHelper,
		treeData, treeLockingTool, treeService);

	/**
	 * Initialize tree, create first node.
	 */
	jbTreeTool.createRootNode();

	if (treeWrapperFileName == null) {
	    return new TreeMapImpl<K, V>(tree, treeData);
	} else {
	    return new TreeMapImpl<K, V>(
		    new JbTreeWrapper<K, V>(tree, nodeStore, treeWrapperFileName), treeData);

	}
    }
}
