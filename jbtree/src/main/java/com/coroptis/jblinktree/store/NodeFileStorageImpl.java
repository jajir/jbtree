package com.coroptis.jblinktree.store;

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

import java.io.File;
import java.util.concurrent.locks.ReentrantLock;

import com.coroptis.jblinktree.JbNodeBuilder;
import com.coroptis.jblinktree.JbTreeData;
import com.coroptis.jblinktree.JblinktreeException;
import com.coroptis.jblinktree.Node;
import com.google.common.base.Preconditions;

/**
 * Simple immutable thread safe node storage. Could be used just in case when
 * values associated with keys occupy 4 or less bytes.
 *
 * @author jajir
 *
 * @param <K>
 *            key type
 * @param <V>
 *            value type
 */
public final class NodeFileStorageImpl<K, V> implements NodeFileStorage<K, V> {

    /**
     * Name of the file where values will be stored.
     */
    private static final String FILE_VALUES = "value.str";

    /**
     * Name of the file where tree metadata will be stored.
     */
    private static final String FILE_META_DATA = "meta.str";

    /**
     * Name of the file where keys will be stored.
     */
    private static final String FILE_KEYS = "key.str";

    /**
     * Contains names of all files. Simplify work with files.
     */
    private static final String[] FILES = {
            FILE_VALUES, FILE_META_DATA, FILE_KEYS };

    /**
     * Unified locking for file system operations.
     */
    private final ReentrantLock lock = new ReentrantLock(false);

    /**
     * Store values from nodes.
     */
    private final ValueFileStorage<K, V> valueFileStorage;

    /**
     * Key Integer node file storage.
     */
    private final KeyIntFileStorage<K> keyIntFileStorage;

    /**
     * Meta data file storage.
     */
    private final MetaDataStore metaDataStore;

    /**
     * Helping class for node.
     */
    private final NodeConverter<K, V> nodeConverter;

    /**
     * When it's <code>true</code> than tree is newly created.
     */
    private final boolean isNewlyCreated;

    /**
     *
     * @param jbTreeData
     *            required tree data definition
     * @param nodeBuilder
     *            required node builder factory
     * @param directory
     *            required directory
     * @param initNodeConverter
     *            required node converter
     */
    public NodeFileStorageImpl(final JbTreeData<K, V> jbTreeData,
            final JbNodeBuilder<K, V> nodeBuilder, final String directory,
            final NodeConverter<K, V> initNodeConverter) {
        this.nodeConverter = Preconditions.checkNotNull(initNodeConverter);
        verifyDirectory(directory);
        isNewlyCreated = isNewlyCreatedInternal(directory);
        this.valueFileStorage = new ValueFileStorageImpl<K, V>(
                addFileToDir(directory, FILE_VALUES),
                jbTreeData.getLeafNodeDescriptor().getValueTypeDescriptor(),
                jbTreeData.getLeafNodeDescriptor().getL());
        this.keyIntFileStorage =
                new KeyIntFileStorage<K>(addFileToDir(directory, FILE_KEYS),
                        jbTreeData.getNonLeafNodeDescriptor(),
                        (JbNodeBuilder<K, Integer>) nodeBuilder);
        this.metaDataStore = new MetaDataStoreImpl<K, V>(
                addFileToDir(directory, FILE_META_DATA), jbTreeData);
    }

    /**
     * Create File object form file name and directory.
     *
     * @param directory
     *            required directory name
     * @param fileName
     *            file name
     * @return {@link File} object
     */
    private File addFileToDir(final String directory, final String fileName) {
        return new File(directory + File.separator + fileName);
    }

    @Override
    public void store(final Node<K, V> node) {
        lock.lock();
        try {
            if (node.isLeafNode()) {
                valueFileStorage.storeValues(node);
                keyIntFileStorage.store(nodeConverter.convertToKeyInt(node));
            } else {
                keyIntFileStorage.store((Node<K, Integer>) node);
            }
        } finally {
            lock.unlock();
        }
    }

    @Override
    public Node<K, V> load(final Integer nodeId) {
        lock.lock();
        try {
            Node<K, Integer> node = keyIntFileStorage.load(nodeId);
            if (node.isLeafNode()) {
                Node<K, V> out = nodeConverter.convertToKeyValue(node);
                return valueFileStorage.loadValues(out);
            } else {
                return (Node<K, V>) node;
            }
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void close() {
        lock.lock();
        try {
            keyIntFileStorage.close();
            valueFileStorage.close();
            metaDataStore.close();
        } finally {
            lock.unlock();
        }
    }

    /**
     * @return the isNewlyCreated
     */
    @Override
    public boolean isNewlyCreated() {
        return isNewlyCreated;
    }

    /**
     * Verify that all tree files exists or all of then doesn't exists.
     *
     * @param directory
     *            required directory
     * @return <code>true</code> when it's new tree and any file exists
     *         otherwise return <code>false</code>
     */
    private boolean isNewlyCreatedInternal(final String directory) {
        boolean out = true;
        File f = addFileToDir(directory, FILES[0]);
        if (f.exists()) {
            out = false;
            if (f.isDirectory()) {
                throw new JblinktreeException(
                        "file '" + f.getAbsolutePath() + "' should be a file");
            }
            for (int i = 1; i < FILES.length; i++) {
                f = addFileToDir(directory, FILES[i]);
                if (!f.exists()) {
                    throw new JblinktreeException("inconsistent tree, file '"
                            + f.getAbsolutePath() + "' should exist");
                }
                if (f.isDirectory()) {
                    throw new JblinktreeException("file '" + f.getAbsolutePath()
                            + "' should be a file");
                }
            }
        } else {
            for (int i = 1; i < FILES.length; i++) {
                f = addFileToDir(directory, FILES[i]);
                if (f.exists()) {
                    throw new JblinktreeException("inconsistent tree, file '"
                            + f.getAbsolutePath() + "' should not exist");
                }
            }
        }
        return out;
    }

    /**
     * Verify that tree home directory is existing directory.
     *
     * @param directory
     *            required directory
     */
    private void verifyDirectory(final String directory) {
        Preconditions.checkNotNull(directory);
        File f = new File(directory);
        if (f.exists()) {
            if (!f.isDirectory()) {
                throw new JblinktreeException(
                        "Tree home '" + directory + "' is not directory.");
            }
        } else {
            throw new JblinktreeException(
                    "Tree home '" + directory + "' doesn't exists.");
        }
    }

}
