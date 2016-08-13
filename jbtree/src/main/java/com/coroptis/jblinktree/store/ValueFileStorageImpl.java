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

import com.coroptis.jblinktree.Field;
import com.coroptis.jblinktree.JblinktreeException;
import com.coroptis.jblinktree.Node;
import com.coroptis.jblinktree.type.TypeDescriptor;

/**
 * Immutable class store just values from key value pairs.
 *
 * @author jajir
 *
 * @param <V>
 *            value type
 */
public class ValueFileStorageImpl<K, V> implements ValueFileStorage<K, V> {

    private final RandomAccessFile raf;

    private final TypeDescriptor<V> valueTypeDescriptor;

    private final int l;

    public ValueFileStorageImpl(final File storeFile,
            final TypeDescriptor<V> valueTypeDesc, final int intL) {
        this.valueTypeDescriptor = valueTypeDesc;
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
    public void storeValues(Node<K, V> node) {
        try {
            raf.seek(filePosition(node.getId()));
            Field<K, V> field = node.getField();
            byte data[] = new byte[valueTypeDescriptor.getMaxLength()];
            for (int i = 0; i < field.getKeyCount(); i++) {
                valueTypeDescriptor.save(data, 0, field.getValue(i));
                raf.write(data);
            }
        } catch (IOException e) {
            throw new JblinktreeException(e.getMessage(), e);
        }
    }

    @Override
    public Node<K, V> loadValues(Node<K, V> node) {
        try {
            raf.seek(filePosition(node.getId()));
            Field<K, V> field = node.getField();
            byte data[] = new byte[valueTypeDescriptor.getMaxLength()];
            for (int i = 0; i < field.getKeyCount(); i++) {
                raf.readFully(data);
                field.setValue(0, valueTypeDescriptor.load(data, 0));
            }
            return node;
        } catch (IOException e) {
            throw new JblinktreeException(e.getMessage(), e);
        }
    }

}
