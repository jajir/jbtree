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

import com.coroptis.jblinktree.store.NodeStoreInFile;
import com.coroptis.jblinktree.store.NodeStoreInMem;
import com.coroptis.jblinktree.type.TypeDescriptor;
import com.coroptis.jblinktree.type.TypeDescriptorInteger;
import com.google.common.base.Preconditions;

/**
 * Provide fluent API for creating tree. Default tree node parameter L is 5.
 * It's maximum number of key,value pairs in node.
 *
 * @author jajir
 *
 */
public final class TreeBuilder {

    /**
     * Default max number of keys in node.
     */
    private static final int DEFAULT_MAX_NUMBER_OF_KEYS_IN_NODE = 5;

    /**
     * Actual max number of keys in node.
     */
    private Integer l;

    /**
     * Key type descriptor.
     */
    private TypeDescriptor<?> keyTypeDescriptor;

    /**
     * Value type descriptor.
     */
    private TypeDescriptor<?> valueTypeDescriptor;

    /**
     * File name where will be tree structure stored in .dot format.
     */
    private String treeWrapperFileName;

    /**
     * Node store instance. Could be in memory and files.
     */
    private NodeStoreInFileBuilder nodeStoreInFileBuilder;

    /**
     * Builder for parameters for storing to file system.
     *
     * @author jiroutj
     *
     */
    public static class NodeStoreInFileBuilder {

        /**
         * Directory where will be tree stored.
         */
        private String fileName;

        /**
         * How many nodes will be stored in memory in cache.
         */
        private int noOfCachedNodes;

        /**
         * Allow to set directory where will be tree stored.
         * <p>
         * When files exists than tree tries to load data from disk. If node was
         * stored with different key value types than will be throws
         * {@link JblinktreeException}.
         * </p>
         *
         * @param directory
         *            required directory path
         * @return return {@link NodeStoreInFileBuilder}
         */
        public final NodeStoreInFileBuilder setFileName(
                final String directory) {
            this.fileName = Preconditions.checkNotNull(directory);
            return this;
        }

        /**
         * Allow to set number of cached nodes.
         *
         * @param numberOfCachedNodes
         *            required number of nodes stored in cache
         * @return return {@link NodeStoreInFileBuilder}
         */
        public final NodeStoreInFileBuilder setNoOfCachedNodes(
                final int numberOfCachedNodes) {
            this.noOfCachedNodes = Preconditions
                    .checkNotNull(numberOfCachedNodes);
            return this;
        }

        /**
         * @return the fileName
         */
        public final String getFileName() {
            return fileName;
        }

        /**
         * @return the noOfCachedNodes
         */
        public final int getNoOfCachedNodes() {
            return noOfCachedNodes;
        }

    }

    /**
     * Provide builder for file node store.
     *
     * @return file node store builder
     */
    public static NodeStoreInFileBuilder getNodeStoreInFileBuilder() {
        return new TreeBuilder.NodeStoreInFileBuilder();
    }

    /**
     * Create new instance of builder.
     *
     * @return new builder instance.
     */
    public static TreeBuilder builder() {
        return new TreeBuilder(DEFAULT_MAX_NUMBER_OF_KEYS_IN_NODE);
    }

    /**
     * Private constructor for builder. Can't be called from outside.
     *
     * @param initL
     *            default value of L node parameter.
     */
    private TreeBuilder(final Integer initL) {
        this.l = initL;
        treeWrapperFileName = null;
    }

    /**
     * Allow to set tree node parameter L. It's maximum number of key value
     * pairs in node.
     *
     * @param newL
     *            required L parameter value
     * @return current tree builder instance
     */
    public TreeBuilder setL(final Integer newL) {
        if (newL < 2) {
            throw new JblinktreeException(
                    "Value of L parameter should higher that 1.");
        }
        if (newL > Byte.MAX_VALUE) {
            /**
             * This is because of storing number of nodes in one byte.
             */
            throw new JblinktreeException(
                    "maximal value of L parameter is " + Byte.MAX_VALUE + ".");
        }
        this.l = newL;
        return this;
    }

    /**
     * Allow to set tree wrapper instance. Wrapper store tree nodes into file.
     * It'd debug tool and never should be used in production.
     *
     * @param fileName
     *            required tree wrapper output file
     * @return current tree builder instance
     */
    public TreeBuilder setTreeWrapper(final String fileName) {
        this.treeWrapperFileName = Preconditions.checkNotNull(fileName);
        return this;
    }

    /**
     * Allows to set node store.
     *
     * @param nodeStoreFileBuilder
     *            required special builder instance
     * @return current tree builder instance
     */
    public TreeBuilder setNodeStoreInFileBuilder(
            final NodeStoreInFileBuilder nodeStoreFileBuilder) {
        this.nodeStoreInFileBuilder = Preconditions
                .checkNotNull(nodeStoreFileBuilder);
        return this;
    }

    /**
     * Allows to set key type descriptor. Choose them from
     * {@link com.coroptis.jblinktree.type.Types} static objects.
     *
     * @param typeDescriptor
     *            required key type descriptor
     * @return current tree builder instance
     */
    public TreeBuilder setKeyType(final TypeDescriptor<?> typeDescriptor) {
        this.keyTypeDescriptor = typeDescriptor;
        return this;
    }

    /**
     * Allows to set value type descriptor. Choose them from
     * {@link com.coroptis.jblinktree.type.Types} static objects.
     *
     * @param typeDescriptor
     *            required value type descriptor
     * @return current tree builder instance
     */
    public TreeBuilder setValueType(final TypeDescriptor<?> typeDescriptor) {
        this.valueTypeDescriptor = typeDescriptor;
        return this;
    }

    /**
     * Build {@link java.util.Map} instance with previously given parameters.
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
        final JbTreeData<K, V> treeData = new JbTreeDataImpl<K, V>(
                NodeStore.FIRST_NODE_ID, l,
                (TypeDescriptor<K>) keyTypeDescriptor,
                (TypeDescriptor<V>) valueTypeDescriptor, linkTypeDescriptor);

        final JbNodeBuilder<K, V> nodeBuilder = new JbNodeBuilderImpl<K, V>(
                treeData);

        final NodeStore<K> nodeStore;
        if (nodeStoreInFileBuilder == null) {
            nodeStore = new NodeStoreInMem<K, V>(nodeBuilder);
        } else {
            nodeStore = new NodeStoreInFile<K, V>(treeData, nodeBuilder,
                    nodeStoreInFileBuilder.getFileName(),
                    nodeStoreInFileBuilder.getNoOfCachedNodes());
        }
        final JbTreeTool<K, V> jbTreeTool = new JbTreeToolImpl<K, V>(nodeStore,
                (TypeDescriptor<K>) keyTypeDescriptor, nodeBuilder);
        final JbTreeTraversingService<K, V> treeLockingTool = new JbTreeTraversingServiceImpl<K, V>(
                jbTreeTool);
        final JbTreeService<K, V> treeService = new JbTreeServiceImpl<K, V>(
                nodeStore, treeLockingTool);
        final JbTreeHelper<K, V> jbTreeHelper = new JbTreeHelperImpl<K, V>(
                nodeStore, jbTreeTool, treeService, treeData);
        final JbTree<K, V> tree = new JbTreeImpl<K, V>(nodeStore, jbTreeTool,
                jbTreeHelper, treeData, treeLockingTool, treeService);

        /**
         * Initialize tree, create first node.
         */
        jbTreeTool.createRootNode();

        if (treeWrapperFileName == null) {
            return new TreeMapImpl<K, V>(tree, treeData);
        } else {
            return new TreeMapImpl<K, V>(new JbTreeWrapper<K, V>(tree,
                    nodeStore, treeWrapperFileName), treeData);

        }
    }
}
