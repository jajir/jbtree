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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

import com.coroptis.jblinktree.JblinktreeException;
import com.coroptis.jblinktree.Node;
import com.coroptis.jblinktree.type.TypeDescriptor;
import com.google.common.base.Preconditions;

/**
 * Immutable class store just values from key value pairs. Class doesn't use any
 * caching.
 * <p>
 * When node A is stored with 3 values and later with just 1 value than
 * physically unused values are still stored.
 * </p>
 *
 * @author jajir
 *
 * @param <K>
 *            key type
 * @param <V>
 *            value type
 */
public final class ValueFileStorageImpl<K, V>
        implements ValueFileStorage<K, V> {

    /**
     * File where are data stored.
     */
    private final RandomAccessFile raf;

    /**
     * Value type descriptor.
     */
    private final TypeDescriptor<V> valueTypeDescriptor;

    /**
     * Maximal number of key value pairs in node.
     */
    private final int l;

    /**
     *
     * @param storeFile
     *            required {@link java.io.File} where all data will be stored
     * @param valueTypeDesc
     *            required value type description
     * @param intL
     *            required maximal number of key value pairs in node.
     */
    public ValueFileStorageImpl(final File storeFile,
            final TypeDescriptor<V> valueTypeDesc, final int intL) {
        this.valueTypeDescriptor = Preconditions.checkNotNull(valueTypeDesc);
        this.l = intL;
        try {
            raf = new RandomAccessFile(storeFile, "rw");
        } catch (FileNotFoundException e) {
            throw new JblinktreeException(e.getMessage(), e);
        }

    }

    @Override
    public void close() {
        try {
            raf.close();
        } catch (IOException e) {
            throw new JblinktreeException(e.getMessage(), e);
        }
    }

    /**
     * Get position where is value stored.
     *
     * @param valueId
     *            required value id
     * @return position in file
     */
    private long filePosition(final Integer valueId) {
        return valueId * ((long) valueTypeDescriptor.getMaxLength() * l);
    }

    @Override
    public void storeValues(final Node<K, V> node) {
        verifyLeafNode(node);
        try {
            raf.seek(filePosition(node.getId()));
            final int keyCount = node.getKeyCount();
            byte[] data = new byte[valueTypeDescriptor.getMaxLength()
                    * node.getNodeDef().getL()];
            for (int i = 0; i < keyCount; i++) {
                valueTypeDescriptor.save(data,
                        valueTypeDescriptor.getMaxLength() * i,
                        node.getValue(i));
            }
            raf.write(data);
        } catch (IOException e) {
            throw new JblinktreeException(e.getMessage(), e);
        }
    }

    @Override
    public Node<K, V> loadValues(final Node<K, V> node) {
        verifyLeafNode(node);
        try {
            raf.seek(filePosition(node.getId()));
            byte[] data = new byte[valueTypeDescriptor.getMaxLength()
                    * node.getNodeDef().getL()];
            raf.readFully(data);
            for (int i = 0; i < node.getKeyCount(); i++) {
                node.setValue(i, valueTypeDescriptor.load(data,
                        valueTypeDescriptor.getMaxLength() * i));
            }
            return node;
        } catch (IOException e) {
            throw new JblinktreeException(e.getMessage(), e);
        }
    }

    /**
     * Verify that it's leaf node. When it's not leaf node than
     * {@link JblinktreeException} is thrown.
     *
     * @param node
     *            required leaf node
     */
    private void verifyLeafNode(final Node<K, V> node) {
        if (!node.isLeafNode()) {
            throw new JblinktreeException("Leaf node is required. " + node);
        }
    }
}
