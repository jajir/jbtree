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

import com.coroptis.jblinktree.Field;
import com.coroptis.jblinktree.JbNodeDef;
import com.coroptis.jblinktree.JbTreeData;
import com.coroptis.jblinktree.Node;
import com.coroptis.jblinktree.NodeBuilder;
import com.google.common.base.Preconditions;

/**
 * Simple thread safe node storage. Could be used just in case when values
 * associated with keys occupy 4 or less bytes.
 *
 * @author jajir
 *
 * @param <K>
 *            key type
 * @param <V>
 *            value type
 */
public final class NodeFileStorageImpl<K, V> implements NodeFileStorage<K, V> {

    private final NodeBuilder<K, V> nodeBuilder;

    private final JbNodeDef<K, V> nodeDef;

    private final JbTreeData<K, V> treeData;

    private final ReentrantLock lock = new ReentrantLock(false);

    private final ValueFileStorage<K, V> valueFileStorage;

    private final KeyIntFileStorage<K> keyIntFileStorage;

    public NodeFileStorageImpl(final JbTreeData<K, V> jbTreeData,
            final NodeBuilder<K, V> nodeBuilder, String directory) {
        this.treeData = Preconditions.checkNotNull(jbTreeData);
        this.nodeDef = treeData.getLeafNodeDescriptor();
        this.nodeBuilder = Preconditions.checkNotNull(nodeBuilder);
        Preconditions.checkNotNull(directory);
        this.valueFileStorage = new ValueFileStorageImpl<K, V>(
                addFileToDir(directory, "value.str"),
                nodeDef.getValueTypeDescriptor(), nodeDef.getL());
        this.keyIntFileStorage = new KeyIntFileStorage<K>(
                addFileToDir(directory, "key.str"),
                treeData.getNonLeafNodeDescriptor(),
                (NodeBuilder<K, Integer>) nodeBuilder);
    }

    private File addFileToDir(final String directory, final String fileName) {
        return new File(directory + File.separator + fileName);
    }

    @Override
    public void store(Node<K, V> node) {
        lock.lock();
        try {
            if (node.isLeafNode()) {
                valueFileStorage.storeValues(node);
                keyIntFileStorage.store(convertToKeyInt(node));
            } else {
                keyIntFileStorage.store((Node<K, Integer>) node);
            }
        } finally {
            lock.unlock();
        }
    }

    private Node<K, Integer> convertToKeyInt(Node<K, V> node) {
        byte[] b = new byte[treeData.getNonLeafNodeDescriptor()
                .getFieldActualLength(node.getKeysCount())];
        b[0] = Node.M;
        Node<K, Integer> out = nodeBuilder.makeNode(node.getId(), b, treeData.getNonLeafNodeDescriptor());
        Preconditions.checkState(out.isLeafNode());
        Field<K, V> f = node.getField();
        for (int i = 0; i < f.getKeyCount(); i++) {
            out.getField().setKey(i, f.getKey(i));
        }
        out.setLink(node.getLink());
        return out;
    }

    private Node<K, V> convertToKeyValue(Node<K, Integer> node) {
        byte[] b = new byte[treeData.getLeafNodeDescriptor()
                .getFieldActualLength(node.getKeysCount())];
        b[0] = Node.M;
        Node<K, V> out = nodeBuilder.makeNode(node.getId(), b);
        Preconditions.checkState(out.isLeafNode());
        Field<K, Integer> f = node.getField();
        for (int i = 0; i < f.getKeyCount(); i++) {
            out.getField().setKey(i, f.getKey(i));
        }
        out.setLink(node.getLink());
        return out;
    }

    @Override
    public Node<K, V> load(Integer nodeId) {
        lock.lock();
        try {
            Node<K, Integer> node = keyIntFileStorage.load(nodeId);
            if (node.isLeafNode()) {
                Node<K, V> out = convertToKeyValue(node);
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
        keyIntFileStorage.close();
        valueFileStorage.close();
    }

}
