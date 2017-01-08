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
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.Charset;

import com.coroptis.jblinktree.JbTreeData;
import com.coroptis.jblinktree.type.AbstractMetaType;
import com.coroptis.jblinktree.type.MetaTypesResolver;
import com.coroptis.jblinktree.type.TypeDescriptor;
import com.coroptis.jblinktree.util.JblinktreeException;
import com.google.common.base.Preconditions;

/**
 * Immutable implementation.
 *
 * @author jajir
 *
 * @param <K>
 *            key type
 * @param <V>
 *            value type
 */
public final class MetaDataStoreImpl<K, V> implements MetaDataStore {

    /**
     * Tree data descriptor.
     */
    private final JbTreeData<K, V> treeData;

    /**
     * Random access file object.
     */
    private final RandomAccessFile raf;

    /**
     * Header that will be written as first bytes to file.
     */
    private static final String HEADER = "jbTree-metadata";

    /**
     *
     * @param file
     *            required file where will be data written or readed from
     * @param jbTreeData
     *            tree data definition
     */
    public MetaDataStoreImpl(final File file,
            final JbTreeData<K, V> jbTreeData) {
        this.treeData = Preconditions.checkNotNull(jbTreeData);
        Preconditions.checkNotNull(file);
        final boolean isFileNew = !file.exists();
        try {
            raf = new RandomAccessFile(file, "rw");
            init(isFileNew);
        } catch (IOException e) {
            throw new JblinktreeException(e.getMessage(), e);
        }
    }

    /**
     * Initialize meta data file. If already exists than verify that data are
     * correct and read them. If doesn't exists write default values.
     *
     * @param isFileNew
     *            required boolean is <code>true</code> when file is newly
     *            created otherwise should be <code>false</code>
     * @throws IOException
     *             read or write IOException
     */
    private void init(final boolean isFileNew) throws IOException {
        if (isFileNew) {
            writeHeader();
            writeMeta();
        } else {
            verifyHeader();
            treeData.setRootNodeId(loadInt(HEADER.length()));
            treeData.setMaxNodeId(
                    loadInt(HEADER.length() + treeData.getLeafNodeDescriptor()
                            .getLinkTypeDescriptor().getMaxLength()));
            verifyDataTypes();
        }
    }

    /**
     * Write root node id and max used node id.
     *
     * @throws IOException
     *             read or write IOException
     */
    private void writeMeta() throws IOException {
        writeInt(HEADER.length(), treeData.getRootNodeId());
        writeInt(
                HEADER.length() + treeData.getLeafNodeDescriptor()
                        .getLinkTypeDescriptor().getMaxLength(),
                treeData.getMaxNodeId());
        writeDataTypes();
    }

    /**
     * Verify that file header {@link #HEADER} is correctly stored.
     *
     * @throws IOException
     *             read or write IOException
     * @throws JblinktreeException
     *             exception is throws when header is not correctly read from
     *             file
     */
    private void verifyHeader() throws IOException {
        raf.seek(0);
        byte[] b = new byte[HEADER.length()];
        raf.readFully(b);
        String loaded = new String(b, Charset.forName("ISO-8859-1"));
        if (!HEADER.equals(loaded)) {
            throw new JblinktreeException(
                    "Header in tree meta file is not correct '" + loaded
                            + "', expected was '" + HEADER + "'");
        }
    }

    /**
     * Write {@link #HEADER} to file.
     *
     * @throws IOException
     *             read or write IOException
     */
    private void writeHeader() throws IOException {
        raf.seek(0);
        raf.write(HEADER.getBytes(Charset.forName("ISO-8859-1")));
    }

    /**
     * Load single {@link java.lang.Integer} value from file.
     *
     * @param position
     *            required position in file
     * @return loaded value
     * @throws IOException
     *             read or write IOException
     */
    private Integer loadInt(final long position) throws IOException {
        raf.seek(position);
        final TypeDescriptor<Integer> linkTd = treeData.getLeafNodeDescriptor()
                .getLinkTypeDescriptor();
        byte[] b = new byte[linkTd.getMaxLength()];
        raf.readFully(b);
        return linkTd.load(b, 0);
    }

    /**
     * Write single {@link java.lang.Integer} value to file.
     *
     * @param position
     *            required position of integer in file
     * @param value
     *            stored value
     * @throws IOException
     *             read or write IOException
     */
    private void writeInt(final long position, final Integer value)
            throws IOException {
        raf.seek(position);
        final TypeDescriptor<Integer> linkTd = treeData.getLeafNodeDescriptor()
                .getLinkTypeDescriptor();
        byte[] b = new byte[linkTd.getMaxLength()];
        linkTd.save(b, 0, value);
        raf.write(b);
    }

    /**
     * Write type descriptor to file.
     *
     * @param td
     *            required type descriptor
     * @param position
     *            required position where will be value written
     * @return how many bytes was written to file
     * @throws IOException
     *             read or write IOException
     * @param <S>
     *            type described by data type descriptor
     */
    private <S> int writeTypeDescriptor(final TypeDescriptor<S> td,
            final long position) throws IOException {
        raf.seek(position);
        MetaTypesResolver metaTypesResolver = MetaTypesResolver.getInstance();
        AbstractMetaType<TypeDescriptor<S>> mt = metaTypesResolver
                .resolve(td.getClass());
        raf.writeByte(mt.getCode());
        if (mt.getMaxLength() > 0) {
            byte[] b = new byte[mt.getMaxLength()];
            mt.save(b, 0, td);
            raf.write(b);
        }
        return 1 + mt.getMaxLength();
    }

    /**
     * Load type descriptor and compare it with given.
     *
     * @param td
     *            required data type descriptor
     * @param position
     *            required position from which will be data type descriptor
     *            loaded
     * @return how many bytes was loaded from file
     * @throws IOException
     *             read or write IOException
     * @throws JblinktreeException
     *             when loaded data type descriptor is not equals as given one
     * @param <S>
     *            type described by data type descriptor
     */
    private <S> int verifyTypeDescriptor(final TypeDescriptor<S> td,
            final long position) throws IOException {
        raf.seek(position);
        MetaTypesResolver metaTypesResolver = MetaTypesResolver.getInstance();
        byte code = raf.readByte();
        AbstractMetaType<TypeDescriptor<S>> mt = metaTypesResolver
                .resolve(code);
        TypeDescriptor<S> td2;
        if (mt.getMaxLength() > 0) {
            byte[] b = new byte[mt.getMaxLength()];
            raf.readFully(b);
            td2 = mt.load(b, 0);
        } else {
            td2 = mt.getInstance();
        }
        if (td2.equals(td)) {
            return 1 + mt.getMaxLength();
        } else {
            throw new JblinktreeException("Expected data type '" + td
                    + "' is not same as stored '" + td2 + "'");
        }
    }

    /**
     * Write all data type descriptors from {@link JbTreeData}.
     *
     * @throws IOException
     *             read or write IOException
     */
    private void writeDataTypes() throws IOException {
        int pos = getDataTypePos();
        raf.seek(pos);
        pos += writeTypeDescriptor(
                treeData.getLeafNodeDescriptor().getKeyTypeDescriptor(), pos);
        pos += writeTypeDescriptor(
                treeData.getLeafNodeDescriptor().getValueTypeDescriptor(), pos);
        pos += writeTypeDescriptor(
                treeData.getLeafNodeDescriptor().getLinkTypeDescriptor(), pos);
    }

    /**
     * Verify all data type descriptors from {@link JbTreeData} and from loaded
     * values.
     *
     * @throws IOException
     *             read or write IOException
     */
    private void verifyDataTypes() throws IOException {
        int pos = getDataTypePos();
        raf.seek(pos);
        pos += verifyTypeDescriptor(
                treeData.getLeafNodeDescriptor().getKeyTypeDescriptor(), pos);
        pos += verifyTypeDescriptor(
                treeData.getLeafNodeDescriptor().getValueTypeDescriptor(), pos);
        pos += verifyTypeDescriptor(
                treeData.getLeafNodeDescriptor().getLinkTypeDescriptor(), pos);
    }

    @Override
    public void close() {
        try {
            writeMeta();
            raf.close();
        } catch (IOException e) {
            throw new JblinktreeException(e.getMessage(), e);
        }
    }

    /**
     * return position where ends file header and starts place for data types
     * descriptors.
     *
     * @return length of header in bytes
     */
    private int getDataTypePos() {
        return HEADER.length() + treeData.getLeafNodeDescriptor()
                .getLinkTypeDescriptor().getMaxLength() * 2;
    }

}
