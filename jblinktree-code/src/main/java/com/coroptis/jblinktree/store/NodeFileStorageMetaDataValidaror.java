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

import com.coroptis.jblinktree.JbTreeData;
import com.coroptis.jblinktree.Node;
import com.google.common.base.Preconditions;

/**
 * Decorator class that verify format of stored tree with given format.
 *
 * @author jajir
 *
 * @param <K>
 *            key type
 * @param <V>
 *            value type
 */
public final class NodeFileStorageMetaDataValidaror<K, V>
        implements NodeFileStorage<K, V> {

    /**
     * Meta data file storage.
     */
    private final MetaDataStore metaDataStore;

    /**
     * Node storage to which will be operation delegated.
     */
    private final NodeFileStorage<K, V> next;

    /**
     * Simple constructor.
     *
     * @param jbTreeData
     *            required tree data definition
     * @param directory
     *            required directory
     * @param nodeFileStorage
     *            required node file storage to which will be operation
     *            delegated
     */
    public NodeFileStorageMetaDataValidaror(final JbTreeData<K, V> jbTreeData,
            final String directory,
            final NodeFileStorage<K, V> nodeFileStorage) {
        this.next = Preconditions.checkNotNull(nodeFileStorage);
        this.metaDataStore = new MetaDataStoreImpl<K, V>(
                addFileToDir(directory, NodeFileStorageImpl.FILE_META_DATA),
                jbTreeData);
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
        next.store(node);
    }

    @Override
    public Node<K, V> load(final Integer nodeId) {
        return next.load(nodeId);
    }

    @Override
    public void close() {
        metaDataStore.close();
        next.close();
    }

    /**
     * @return the isNewlyCreated
     */
    @Override
    public boolean isNewlyCreated() {
        return next.isNewlyCreated();
    }

}
