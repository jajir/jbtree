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
import java.util.concurrent.locks.ReentrantLock;

import com.coroptis.jblinktree.JbNodeDef;
import com.coroptis.jblinktree.JblinktreeException;
import com.coroptis.jblinktree.Node;
import com.coroptis.jblinktree.NodeBuilder;
import com.google.common.base.Preconditions;

/**
 * Simple thread safe node storage.
 * 
 * @author jan
 *
 * @param <K>
 *            key type
 * @param <V>
 *            value type
 */
public class NodeFileStorageImpl<K, V> implements NodeFileStorage<K, V> {

    private final NodeBuilder<K, V> nodeBuilder;

    private final String fileName;

    private final RandomAccessFile raf;

    private final int maxNodeRecordSize;

    private final JbNodeDef<K, V> nodeDef;

    private final ReentrantLock lock = new ReentrantLock(false);

    private final static int NUMBER_OF_KEYS_IN_NODE_LENGTH = 1;

    public NodeFileStorageImpl(final JbNodeDef<K, V> nodeDef,
	    final NodeBuilder<K, V> nodeBuilder, String fileName) {
	this.nodeDef = Preconditions.checkNotNull(nodeDef);
	this.nodeBuilder = Preconditions.checkNotNull(nodeBuilder);
	this.fileName = Preconditions.checkNotNull(fileName);
	maxNodeRecordSize = NUMBER_OF_KEYS_IN_NODE_LENGTH
		+ nodeDef.getFieldMaxLength();
	try {
	    raf = new RandomAccessFile(new File(fileName), "rw");
	} catch (FileNotFoundException e) {
	    throw new JblinktreeException(e.getMessage(), e);
	}
    }

    @Override
    public void store(Node<K, V> node) {
	lock.lock();
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
	} finally {
	    lock.unlock();
	}
    }

    @Override
    public Node<K, V> load(Integer nodeId) {
	lock.lock();
	try {
	    raf.seek(getPosition(nodeId));
	    byte keys = raf.readByte();
	    int fieldSize = nodeDef.getFieldActualLength(keys);
	    byte[] bytes = new byte[fieldSize];
	    raf.readFully(bytes);
	    return nodeBuilder.makeNode(nodeId, bytes);
	} catch (IOException e) {
	    String msg = null;
	    if (e.getMessage() == null) {
		msg = "problem in reading node '" + nodeId + "' form file "
			+ fileName;
	    } else {
		msg = e.getMessage() + ", problem in reading node '" + nodeId
			+ "' from file " + fileName;
	    }
	    throw new JblinktreeException(msg, e);
	} finally {
	    lock.unlock();
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
	return (long) maxNodeRecordSize * nodeId;
    }

}
