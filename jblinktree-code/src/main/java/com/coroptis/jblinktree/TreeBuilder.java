package com.coroptis.jblinktree;

import java.io.File;
import java.util.Objects;

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

import com.coroptis.jblinktree.store.Cache;
import com.coroptis.jblinktree.store.CacheListener;
import com.coroptis.jblinktree.store.CacheLru;
import com.coroptis.jblinktree.store.KeyValueFileStorage;
import com.coroptis.jblinktree.store.NodeConverter;
import com.coroptis.jblinktree.store.NodeConverterImpl;
import com.coroptis.jblinktree.store.NodeFileStorage;
import com.coroptis.jblinktree.store.NodeFileStorageImpl;
import com.coroptis.jblinktree.store.NodeFileStorageLockDecorator;
import com.coroptis.jblinktree.store.NodeFileStorageMetaDataValidaror;
import com.coroptis.jblinktree.store.NodeStoreInFile;
import com.coroptis.jblinktree.store.NodeStoreInMem;
import com.coroptis.jblinktree.type.TypeDescriptor;
import com.coroptis.jblinktree.type.TypeDescriptorInteger;
import com.coroptis.jblinktree.util.JblinktreeException;

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
            this.fileName = Objects.requireNonNull(directory);
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
            this.noOfCachedNodes =
                    Objects.requireNonNull(numberOfCachedNodes);
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
        this.treeWrapperFileName = Objects.requireNonNull(fileName);
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
        this.nodeStoreInFileBuilder =
                Objects.requireNonNull(nodeStoreFileBuilder);
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
     * Create node file storage.
     *
     * @param treeData
     *            required tree data
     * @param nodeBuilder
     *            required node builder
     * @param <K>
     *            key type
     * @param <V>
     *            value type
     * @return node file storage
     */
    private <K, V> NodeFileStorage<K, V> makeNodeFileStorage(
            final JbTreeData<K, V> treeData,
            final JbNodeBuilder<K, V> nodeBuilder) {
        NodeFileStorage<K, V> nodeFileStorage;
        if (treeData.getNonLeafNodeDescriptor().getValueTypeDescriptor()
                .getMaxLength() == treeData.getNonLeafNodeDescriptor()
                        .getValueTypeDescriptor().getMaxLength()) {
            /**
             * When value max length is same as length of node id than value
             * could be saved to node id space.
             */
            nodeFileStorage = new KeyValueFileStorage<K, V>(
                    new File(nodeStoreInFileBuilder.getFileName()
                            + File.separator + NodeFileStorageImpl.FILE_KEYS),
                    treeData, nodeBuilder);
        } else {
            final NodeConverter<K, V> nodeConverter =
                    new NodeConverterImpl<K, V>(treeData, nodeBuilder);
            nodeFileStorage = new NodeFileStorageImpl<K, V>(treeData,
                    nodeBuilder, nodeStoreInFileBuilder.getFileName(),
                    nodeConverter);
        }
        final NodeFileStorage<K, V> metaDataValidator =
                new NodeFileStorageMetaDataValidaror<K, V>(treeData,
                        nodeStoreInFileBuilder.getFileName(), nodeFileStorage);
        final NodeFileStorage<K, V> fileStorage =
                new NodeFileStorageLockDecorator<K, V>(metaDataValidator);
        return fileStorage;
    }

    /**
     * Create in-file node store.
     *
     * @param treeData
     *            required tree data
     * @param nodeBuilder
     *            required node builder
     * @param jbNodeLockProvider
     *            required node lock provider
     * @param <K>
     *            key type
     * @param <V>
     *            value type
     * @return node store
     */
    private <K, V> NodeStore<K> makeNodeStoreInFile(
            final JbTreeData<K, V> treeData,
            final JbNodeBuilder<K, V> nodeBuilder,
            final JbNodeLockProvider jbNodeLockProvider) {

        final NodeFileStorage<K, V> nodeFileStorage =
                makeNodeFileStorage(treeData, nodeBuilder);
        final Cache<K, V> nodeCache = new CacheLru<K, V>(nodeBuilder,
                nodeStoreInFileBuilder.getNoOfCachedNodes(), nodeFileStorage);
        nodeCache.addCacheListener(new CacheListener<K, V>() {

            @Override
            public void onUnload(final Node<K, V> node,
                    final boolean wasChanged) {
                if (wasChanged) {
                    nodeFileStorage.store(node);
                }
            }

        });

        nodeCache.addCacheListener(new CacheListener<K, V>() {

            @Override
            public void onUnload(final Node<K, V> node,
                    final boolean wasChanged) {
                jbNodeLockProvider.removeLock(node.getId());
            }

        });

        final NodeStore<K> nodeStore = new NodeStoreInFile<K, V>(nodeCache,
                nodeFileStorage, jbNodeLockProvider);
        return nodeStore;
    }

    /**
     * Build {@link java.util.Map} tree data.
     *
     * @param <K>
     *            key type
     * @param <V>
     *            value type
     * @return {@link JbTreeData} instance
     */
    @SuppressWarnings("unchecked")
    private <K, V> JbTreeData<K, V> buildTreeData() {
        final TypeDescriptor<Integer> linkTypeDesc =
                new TypeDescriptorInteger();
        final TypeDescriptor<K> keyTypeDesc =
                (TypeDescriptor<K>) keyTypeDescriptor;
        final TypeDescriptor<V> valueTypeDesc =
                (TypeDescriptor<V>) valueTypeDescriptor;

        final JbNodeDefImpl.Initializator<K, V> initLeaf =
                new JbNodeDefImpl.InitializatorShort<K, V>();
        final JbNodeDefImpl.Initializator<K, Integer> initNonLeaf =
                new JbNodeDefImpl.InitializatorShort<K, Integer>();
        final JbNodeDef<K, V> leafNodeDescriptor = new JbNodeDefImpl<K, V>(l,
                keyTypeDesc, valueTypeDesc, linkTypeDesc, initLeaf);
        final JbNodeDef<K, Integer> nonLeafNodeDescriptor =
                new JbNodeDefImpl<K, Integer>(l, keyTypeDesc, linkTypeDesc,
                        linkTypeDesc, initNonLeaf);
        final JbTreeData<K, V> treeData =
                new JbTreeDataImpl<K, V>(NodeStore.FIRST_NODE_ID, l,
                        leafNodeDescriptor, nonLeafNodeDescriptor);
        return treeData;
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
    public <K, V> TreeMap<K, V> build() {
        Objects.requireNonNull(keyTypeDescriptor,
                "key TypeDescriptor is null, use .setKeyType in builder");
        Objects.requireNonNull(valueTypeDescriptor,
                "value TypeDescriptor is null, use .setValueType in builder");
        final JbTreeData<K, V> treeData = buildTreeData();
        final JbNodeBuilder<K, V> nodeBuilder =
                new JbNodeBuilderShort<K, V>(treeData);
        final JbNodeLockProvider jbNodeLockProvider =
                new JbNodeLockProviderImpl();
        final NodeStore<K> nodeStore;
        if (nodeStoreInFileBuilder == null) {
            nodeStore =
                    new NodeStoreInMem<K, V>(nodeBuilder, jbNodeLockProvider);
        } else {
            nodeStore = makeNodeStoreInFile(treeData, nodeBuilder,
                    jbNodeLockProvider);
        }
        final JbNodeService<K, V> jbNodeService = new JbNodeServiceImpl<K, V>();
        final JbTreeTool<K, V> jbTreeTool = new JbTreeToolImpl<K, V>(nodeStore,
                treeData, nodeBuilder, jbNodeService);
        final JbTreeTraversingService<K, V> treeLockingTool =
                new JbTreeTraversingServiceImpl<K, V>(jbTreeTool,
                        jbNodeService);
        final JbTreeService<K, V> treeService = new JbTreeServiceImpl<K, V>(
                nodeStore, treeLockingTool, jbNodeService);
        final JbTreeHelper<K, V> jbTreeHelper = new JbTreeHelperImpl<K, V>(
                nodeStore, jbTreeTool, treeService, treeData);
        final JbTree<K, V> tree =
                new JbTreeImpl<K, V>(nodeStore, jbTreeTool, jbTreeHelper,
                        treeData, treeLockingTool, treeService, jbNodeService);

        if (nodeStore.isNewlyCreated()) {
            /**
             * Initialize tree, create first node.
             */
            jbTreeTool.createRootNode();
        }

        if (treeWrapperFileName == null) {
            return new TreeMapImpl<K, V>(tree, treeData);
        } else {
            return new TreeMapImpl<K, V>(new JbTreeWrapper<K, V>(tree, treeData,
                    nodeStore, treeWrapperFileName, jbNodeService), treeData);

        }
    }
}
