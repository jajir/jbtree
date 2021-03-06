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
import java.util.Objects;

import com.coroptis.jblinktree.JbNodeBuilder;
import com.coroptis.jblinktree.JbNodeDef;
import com.coroptis.jblinktree.Node;
import com.coroptis.jblinktree.util.JblinktreeException;

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

    /**
     * Node builder factory.
     */
    private final JbNodeBuilder<K, Integer> nodeBuilder;

    /**
     * Random access file.
     */
    private final RandomAccessFile raf;

    /**
     * Non-leaf node definition.
     */
    private final JbNodeDef<K, Integer> nodeDef;

    /**
     * how many byte occupy on disk information about actual number of key value
     * pairs in node.
     */
    private static final int NUMBER_OF_KEYS_IN_NODE_LENGTH = 1;

    /**
     * When it's <code>true</code> than tree is newly created.
     */
    private final boolean isNewlyCreated;

    /**
     *
     * @param file
     *            required file
     * @param jbNodeDef
     *            required non-leaf definition
     * @param jbNodeBuilder
     *            node builder factory
     */
    public KeyIntFileStorage(final File file,
            final JbNodeDef<K, Integer> jbNodeDef,
            final JbNodeBuilder<K, Integer> jbNodeBuilder) {
        this.nodeDef = Objects.requireNonNull(jbNodeDef);
        this.nodeBuilder = Objects.requireNonNull(jbNodeBuilder);
        Objects.requireNonNull(file);
        isNewlyCreated = !file.exists();
        try {
            raf = new RandomAccessFile(file, "rw");
        } catch (FileNotFoundException e) {
            throw new JblinktreeException(e.getMessage(), e);
        }
    }

    @Override
    public void store(final Node<K, Integer> node) {
        try {
            raf.seek(getPosition(node.getId()));
            raf.writeByte(node.getKeyCount());
            byte[] bytes = node.getFieldBytes();
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
    public Node<K, Integer> load(final Integer nodeId) {
        try {
            raf.seek(getPosition(nodeId));
            byte[] r = new byte[nodeDef.getFieldMaxLength() + 1];
            raf.readFully(r);
            byte keys = r[0];
            int fieldSize = nodeDef.getFieldActualLength(keys);
            byte[] bytes = new byte[fieldSize];
            System.arraycopy(r, 1, bytes, 0, fieldSize);
            return nodeBuilder.makeNode(nodeId, bytes, nodeDef);
        } catch (IOException e) {
            throw new JblinktreeException("Can't find nodeId '" + nodeId + "'",
                    e);
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
     * Get position where node should be written.
     *
     * @param nodeId
     *            required node id
     * @return position in file
     */
    private long getPosition(final Integer nodeId) {
        return ((long) NUMBER_OF_KEYS_IN_NODE_LENGTH
                + nodeDef.getFieldMaxLength()) * nodeId;
    }

    @Override
    public boolean isNewlyCreated() {
        return isNewlyCreated;
    }

}
