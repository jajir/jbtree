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

import com.coroptis.jblinktree.JbNodeBuilder;
import com.coroptis.jblinktree.JbNodeDef;
import com.coroptis.jblinktree.JbTreeData;
import com.coroptis.jblinktree.JblinktreeException;
import com.coroptis.jblinktree.Node;
import com.google.common.base.Preconditions;

/**
 * Simple thread unsafe node storage.
 *
 * @author jajir
 *
 * @param <K>
 *            key type
 * @param <V>
 *            value type
 */
public final class KeyValueFileStorage<K, V> implements NodeFileStorage<K, V> {

    /**
     * Leaf node identification byte.
     */
    private static final byte LEAF_NODE = 1;

    /**
     * Non-leaf node identification byte.
     */
    private static final byte NON_LEAF_NODE = 2;

    /**
     * Node builder factory.
     */
    private final JbNodeBuilder<K, V> nodeBuilder;

    /**
     * Random access file.
     */
    private final RandomAccessFile raf;

    /**
     * Tree definition.
     */
    private final JbTreeData<K, V> treeData;

    /**
     * Maximal length of nodes.
     */
    private final int maxFieldLength;

    /**
     * how many byte occupy on disk information about actual number of key value
     * pairs in node.
     */
    private static final int NUMBER_OF_KEYS_IN_NODE_LENGTH = 2;

    /**
     * When it's <code>true</code> than tree is newly created.
     */
    private final boolean isNewlyCreated;

    /**
     * Simple constructor.
     *
     * @param file
     *            required file
     * @param jbTreeData
     *            tree meta data
     * @param jbNodeBuilder
     *            node builder factory
     */
    public KeyValueFileStorage(final File file,
            final JbTreeData<K, V> jbTreeData,
            final JbNodeBuilder<K, V> jbNodeBuilder) {
        this.treeData = Preconditions.checkNotNull(jbTreeData);
        this.nodeBuilder = Preconditions.checkNotNull(jbNodeBuilder);
        maxFieldLength = treeData.getLeafNodeDescriptor().getFieldMaxLength();
        Preconditions.checkNotNull(file);
        isNewlyCreated = !file.exists();
        try {
            raf = new RandomAccessFile(file, "rw");
        } catch (FileNotFoundException e) {
            throw new JblinktreeException(e.getMessage(), e);
        }
    }

    @Override
    public void store(final Node<K, V> node) {
        try {
            raf.seek(getPosition(node.getId()));
            if (node.isLeafNode()) {
                raf.writeByte(LEAF_NODE);
            } else {
                raf.writeByte(NON_LEAF_NODE);
            }
            raf.writeByte(node.getKeyCount());
            byte[] bytes = node.getFieldBytes();
            if (bytes.length < maxFieldLength) {
                // add zeros to record
                bytes = Arrays.copyOf(bytes, maxFieldLength);
            }
            raf.write(bytes);
        } catch (IOException e) {
            throw new JblinktreeException(e.getMessage(), e);
        }
    }

    /**
     * For flag byte find node definition.
     *
     * @param flagByte
     *            flag byte
     * @return found node definition
     */
    private JbNodeDef<K, V> findNodeDef(final byte flagByte) {
        if (NON_LEAF_NODE == flagByte) {
            return (JbNodeDef<K, V>) treeData.getNonLeafNodeDescriptor();
        } else {
            return treeData.getLeafNodeDescriptor();
        }
    }

    @Override
    public Node<K, V> load(final Integer nodeId) {
        try {
            raf.seek(getPosition(nodeId));
            byte[] r = new byte[maxFieldLength + 2];
            raf.readFully(r);
            final JbNodeDef<K, V> nodeDef = findNodeDef(r[0]);
            byte keys = r[1];
            int fieldSize = nodeDef.getFieldActualLength(keys);
            byte[] bytes = new byte[fieldSize];
            System.arraycopy(r, 2, bytes, 0, fieldSize);
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
        return ((long) NUMBER_OF_KEYS_IN_NODE_LENGTH + maxFieldLength) * nodeId;
    }

    @Override
    public boolean isNewlyCreated() {
        return isNewlyCreated;
    }

}
