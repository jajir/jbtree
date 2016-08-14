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
import com.coroptis.jblinktree.JblinktreeException;
import com.coroptis.jblinktree.type.AbstractTypeDescriptorMetaData;
import com.coroptis.jblinktree.type.MetaTypesResolver;
import com.coroptis.jblinktree.type.TypeDescriptor;
import com.google.common.base.Preconditions;

/**
 * Immutable implementation.
 *
 * @author jan
 *
 * @param <K>
 *            key type
 * @param <V>
 *            value type
 */
public class MetaDataStoreImpl<K, V> implements MetaDataStore<K, V> {

    private final JbTreeData<K, V> treeData;

    private final File metaFile;

    private final RandomAccessFile raf;

    private static final String HEADER = "jbTree-metadata";

    public MetaDataStoreImpl(final File file,
            final JbTreeData<K, V> jbTreeData) {
        this.treeData = Preconditions.checkNotNull(jbTreeData);
        this.metaFile = Preconditions.checkNotNull(file);
        final boolean isFileNew = !file.exists();
        try {
            raf = new RandomAccessFile(metaFile, "rw");
            init(isFileNew);
        } catch (IOException e) {
            throw new JblinktreeException(e.getMessage(), e);
        }
    }

    private void init(final boolean isFileNew) throws IOException {
        if (isFileNew) {
            writeHeader();
            writeRootNodeId();
            writeDataTypes();
        } else {
            verifyHeader();
            loadRootNodeId();
            verifyDataTypes();
        }
    }

    private void verifyHeader() throws IOException {
        raf.seek(0);
        byte[] b = new byte[HEADER.length()];
        raf.read(b);
        String loaded = new String(b, Charset.forName("ISO-8859-1"));
        if (!HEADER.equals(loaded)) {
            throw new JblinktreeException(
                    "Header in tree meta file is not correct '" + loaded
                            + "', expected was '" + HEADER + "'");
        }
    }

    private void loadRootNodeId() throws IOException {
        raf.seek(HEADER.length());
        final TypeDescriptor<Integer> linkTd = treeData.getLeafNodeDescriptor()
                .getLinkTypeDescriptor();
        byte[] b = new byte[linkTd.getMaxLength()];
        raf.readFully(b);
        treeData.setRootNodeId(linkTd.load(b, 0));
    }

    private void writeHeader() throws IOException {
        raf.seek(0);
        raf.write(HEADER.getBytes(Charset.forName("ISO-8859-1")));
    }

    private void writeRootNodeId() throws IOException {
        raf.seek(HEADER.length());
        final TypeDescriptor<Integer> linkTd = treeData.getLeafNodeDescriptor()
                .getLinkTypeDescriptor();
        byte[] b = new byte[linkTd.getMaxLength()];
        linkTd.save(b, 0, treeData.getRootNodeId());
        raf.write(b);
    }

    private <S> int writeTypeDescriptor(final TypeDescriptor<S> td,
            long position) throws IOException {
        raf.seek(position);
        MetaTypesResolver metaTypesResolver = MetaTypesResolver.getInstance();
        AbstractTypeDescriptorMetaData<TypeDescriptor<S>> mt = metaTypesResolver
                .resolve(td.getClass());
        raf.writeByte(mt.getCode());
        if (mt.getMaxLength() > 0) {
            byte[] b = new byte[mt.getMaxLength()];
            mt.save(b, 0, td);
            raf.write(b);
        }
        return 1 + mt.getMaxLength();
    }

    private <S> int verifyTypeDescriptor(final TypeDescriptor<S> td,
            long position) throws IOException {
        raf.seek(position);
        MetaTypesResolver metaTypesResolver = MetaTypesResolver.getInstance();
        byte code = raf.readByte();
        AbstractTypeDescriptorMetaData<TypeDescriptor<S>> mt = metaTypesResolver
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
            writeRootNodeId();
            writeDataTypes();
            raf.close();
        } catch (IOException e) {
            throw new JblinktreeException(e.getMessage(), e);
        }
    }

    private int getDataTypePos() {
        return HEADER.length() + treeData.getLeafNodeDescriptor()
                .getLinkTypeDescriptor().getMaxLength();
    }

}
