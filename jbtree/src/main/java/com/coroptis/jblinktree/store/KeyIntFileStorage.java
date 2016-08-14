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
import java.util.Arrays;

import com.coroptis.jblinktree.JbNodeDef;
import com.coroptis.jblinktree.JblinktreeException;
import com.coroptis.jblinktree.Node;
import com.coroptis.jblinktree.JbNodeBuilder;
import com.google.common.base.Preconditions;

/**
 * Simple thread safe node storage. Could be used just in case when values
 * associated with keys occupy 4 or less bytes.
 *
 * @author jajir
 *
 * @param <K>
 *            key type
 */
public final class KeyIntFileStorage<K> implements NodeFileStorage<K, Integer> {

    private final JbNodeBuilder<K, Integer> nodeBuilder;

    private final RandomAccessFile raf;

    private final int maxNodeRecordSize;

    private final JbNodeDef<K, Integer> nodeDef;

    private static final int NUMBER_OF_KEYS_IN_NODE_LENGTH = 1;

    public KeyIntFileStorage(final File file,
            final JbNodeDef<K, Integer> nodeDef,
            final JbNodeBuilder<K, Integer> nodeBuilder) {
        this.nodeDef = Preconditions.checkNotNull(nodeDef);
        this.nodeBuilder = Preconditions.checkNotNull(nodeBuilder);
        Preconditions.checkNotNull(file);
        maxNodeRecordSize = NUMBER_OF_KEYS_IN_NODE_LENGTH
                + nodeDef.getFieldMaxLength();
        try {
            raf = new RandomAccessFile(file, "rw");
        } catch (FileNotFoundException e) {
            throw new JblinktreeException(e.getMessage(), e);
        }
    }

    @Override
    public void store(Node<K, Integer> node) {
        try {
            raf.seek(getPosition(node.getId()));
            raf.writeByte(node.getKeysCount());
            byte bytes[] = node.getFieldBytes();
            if (bytes.length < nodeDef.getFieldMaxLength()) {
                // add zeros to record
                bytes = Arrays.copyOf(bytes, nodeDef.getFieldMaxLength());
            }
            raf.write(bytes);
        } catch (IOException e) {
            throw new JblinktreeException(e.getMessage(), e);
        }
    }

    @Override
    public Node<K, Integer> load(Integer nodeId) {
        try {
            raf.seek(getPosition(nodeId));
            byte keys = raf.readByte();
            int fieldSize = nodeDef.getFieldActualLength(keys);
            byte[] bytes = new byte[fieldSize];
            raf.readFully(bytes);
            return nodeBuilder.makeNode(nodeId, bytes, nodeDef);
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

    private long getPosition(final Integer nodeId) {
        return ((long) maxNodeRecordSize) * nodeId;
    }

}
