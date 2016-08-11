package com.coroptis.jblinktree.store;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

import com.coroptis.jblinktree.JblinktreeException;
import com.coroptis.jblinktree.type.TypeDescriptor;

/**
 * Immutable class store just values from key value pairs.
 *
 * @author jajir
 *
 * @param <V>
 *            value type
 */
public class ValueFileStorageImpl<V> implements ValueFileStorage<V> {

    private final RandomAccessFile raf;

    private final TypeDescriptor<V> valueTypeDescriptor;

    public ValueFileStorageImpl(final File storeFile,
            final TypeDescriptor<V> valueTypeDesc) {
        this.valueTypeDescriptor = valueTypeDesc;
        try {
            raf = new RandomAccessFile(storeFile, "rw");
        } catch (FileNotFoundException e) {
            throw new JblinktreeException(e.getMessage(), e);
        }

    }

    @Override
    public void store(final Integer valueId, final V value) {
        try {
            raf.seek(filePosition(valueId));
            byte data[] = new byte[valueTypeDescriptor.getMaxLength()];
            valueTypeDescriptor.save(data, 0, value);
            raf.write(data);
        } catch (IOException e) {
            throw new JblinktreeException(e.getMessage(), e);
        }
    }

    @Override
    public V load(final Integer valueId) {
        try {
            raf.seek(filePosition(valueId));
            byte data[] = new byte[valueTypeDescriptor.getMaxLength()];
            raf.readFully(data);
            return valueTypeDescriptor.load(data, 0);
        } catch (IOException e) {
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
        return valueId * (long) valueTypeDescriptor.getMaxLength();
    }

}
