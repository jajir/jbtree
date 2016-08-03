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

import com.coroptis.jblinktree.JbTreeData;
import com.coroptis.jblinktree.JblinktreeException;
import com.coroptis.jblinktree.Node;
import com.coroptis.jblinktree.NodeBuilder;
import com.google.common.base.Preconditions;

public class FileStorageImpl<K, V> implements FileStorage<K, V> {

    private final NodeBuilder<K, V> nodeBuilder;

    private final String fileName;

    private final RandomAccessFile raf;

    private final int maxNodeSize;

    private final JbTreeData<K, V> treeData;

    public FileStorageImpl(final JbTreeData<K, V> treeData, final NodeBuilder<K, V> nodeBuilder,
	    String fileName) {
	this.treeData = Preconditions.checkNotNull(treeData);
	this.nodeBuilder = Preconditions.checkNotNull(nodeBuilder);
	this.fileName = Preconditions.checkNotNull(fileName);
	/**
	 * max node size is: flags + L * (key and value record size) + next link
	 * 
	 */
	// TODO following code is related to filed implementation, should be
	// there.
	maxNodeSize = 1 + treeData.getL() * (treeData.getKeyTypeDescriptor().getMaxLength()
		+ treeData.getValueTypeDescriptor().getMaxLength()) + 4;
	try {
	    raf = new RandomAccessFile(new File(fileName), "rw");
	} catch (FileNotFoundException e) {
	    throw new JblinktreeException(e.getMessage(), e);
	}
    }

    @Override
    public void store(Node<K, V> node) {
	try {
	    raf.seek(getPosition(node.getId()));
	    raf.writeByte(node.getKeysCount());
	    byte bytes[] = node.getFieldBytes();
	    if (bytes.length < maxNodeSize) {
		// add zeros to record
		bytes = Arrays.copyOf(bytes, maxNodeSize);
	    }
	    raf.write(bytes);
	} catch (IOException e) {
	    throw new JblinktreeException(e.getMessage(), e);
	}
    }

    @Override
    public Node<K, V> load(Integer nodeId) {
	try {
	    raf.seek(getPosition(nodeId));
	    byte keys = raf.readByte();
	    // TODO following code is related to filed implementation, should be
	    // there.
	    int fieldSize = 1 + keys * (treeData.getKeyTypeDescriptor().getMaxLength()
		    + treeData.getValueTypeDescriptor().getMaxLength()) + 4;
	    byte[] bytes = new byte[fieldSize];
	    raf.readFully(bytes);
	    return nodeBuilder.makeNode(nodeId, bytes);
	} catch (IOException e) {
	    String msg = null;
	    if (e.getMessage() == null) {
		msg = "problem in reading node '" + nodeId + "' form file " + fileName;
	    } else {
		msg = e.getMessage() + ", problem in reading node '" + nodeId + "' from file "
			+ fileName;
	    }
	    throw new JblinktreeException(msg, e);
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
	// TODO following code is related to filed implementation, should be
	// there.
	return (long) (maxNodeSize + 1) * nodeId;
    }

}
