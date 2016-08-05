package com.coroptis.jblinktree;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

import com.google.common.base.Preconditions;

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

/**
 * Wrap {@link JbTree} implementation and provide additional exception logging.
 * When exception is raised than whole tree is written to file. Written tree
 * could be later visualized.
 * <p>
 * Method is useful for development. And should not be used in production
 * system.
 * </p>
 * 
 * @author jajir
 * 
 * @param <K>
 *            key type
 * @param <V>
 *            value type
 * 
 */
public class JbTreeWrapper<K, V> implements JbTree<K, V> {

    private final JbTree<K, V> tree;

    private final NodeStore<K> nodeStore;

    private final File file;

    JbTreeWrapper(final JbTree<K, V> tree, final NodeStore<K> nodeStore, final String fileName) {
	this.tree = Preconditions.checkNotNull(tree);
	this.nodeStore = Preconditions.checkNotNull(nodeStore);
	this.file = new File(fileName);
    }

    @SuppressWarnings("unchecked")
    @Override
    public V insert(final K key, final V value) {
	return (V) saveExecution(new Execute() {

	    @Override
	    public Object execute() {
		return tree.insert(key, value);
	    }
	});
    }

    @SuppressWarnings("unchecked")
    @Override
    public V remove(final K key) {
	return (V) saveExecution(new Execute() {

	    @Override
	    public Object execute() {
		return tree.remove(key);
	    }
	});
    }

    @SuppressWarnings("unchecked")
    @Override
    public V search(final K key) {
	return (V) saveExecution(new Execute() {

	    @Override
	    public Object execute() {
		return tree.search(key);
	    }
	});
    }

    @Override
    public int countValues() {
	return (Integer) saveExecution(new Execute() {

	    @Override
	    public Object execute() {
		return tree.countValues();
	    }
	});
    }

    @Override
    public boolean containsKey(final K key) {
	return (Boolean) saveExecution(new Execute() {

	    @Override
	    public Object execute() {
		return tree.containsKey(key);
	    }
	});
    }

    @Override
    public void verify() {
	saveExecution(new Execute() {

	    @Override
	    public Object execute() {
		tree.verify();
		return null;
	    }
	});
    }

    @Override
    public int countLockedNodes() {
	return (Integer) saveExecution(new Execute() {

	    @Override
	    public Object execute() {
		return tree.countLockedNodes();
	    }
	});
    }

    @Override
    public void visit(final JbTreeVisitor<K, V> treeVisitor) {
	saveExecution(new Execute() {

	    @Override
	    public Object execute() {
		tree.visit(treeVisitor);
		return null;
	    }
	});
    }

    @Override
    public void visitLeafNodes(final JbTreeVisitor<K, V> treeVisitor) {
	saveExecution(new Execute() {

	    @Override
	    public Object execute() {
		tree.visitLeafNodes(treeVisitor);
		return null;
	    }
	});
    }

    /**
     * Execute given {@link Execute} class instance. When operation fails with
     * exception than tree content is written to file.
     * 
     * @param execute
     *            required execute
     * @return execution result
     */
    private Object saveExecution(final Execute execute) {
	try {
	    return execute.execute();
	} catch (RuntimeException e) {
	    printData();
	    throw e;
	}
    }

    private final static String intendation = "    ";

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private void printData() {
	final StringBuilder buff = new StringBuilder();

	buff.append("digraph graphname {\n");
	buff.append(intendation);
	buff.append("edge [label=0];\n");
	buff.append(intendation);
	buff.append("graph [ranksep=1];\n");
	buff.append(intendation);
	buff.append("node [shape=record]\n");

	for (int i = 0; i < nodeStore.getMaxNodeId(); i++) {
	    nodeStore.get(i).writeTo(buff, intendation);
	}

	for (int i = 0; i < nodeStore.getMaxNodeId(); i++) {
	    Node n = (Node) nodeStore.get(i);
	    addLink(n, buff);
	    if (!n.isLeafNode()) {
		addNextNodes(n, buff);
	    }
	}

	buff.append("");
	buff.append("}");

	try {
	    write(buff);
	} catch (IOException e) {
	    throw new JblinktreeException(e.getMessage());
	}
	buff.toString();
    }

    private void addNextNodes(final Node<Integer, Integer> node, final StringBuilder buff) {
	for (final Object o : node.getNodeIds()) {
	    Integer i = (Integer) o;
	    buff.append(intendation);
	    buff.append("\"node");
	    buff.append(node.getId());
	    buff.append("\":F");
	    buff.append(i);
	    buff.append(" -> ");
	    buff.append("\"node");
	    buff.append(i);
	    buff.append("\" [label=\"");
	    buff.append(i);
	    buff.append("\"];");
	    buff.append("\n");
	}
    }

    private void addLink(final Node<Integer, Integer> node, final StringBuilder buff) {
	if (node.getLink() != null) {
	    buff.append(intendation);
	    buff.append("\"node");
	    buff.append(node.getId());
	    buff.append("\":L");
	    buff.append(node.getLink());
	    buff.append(" -> ");
	    buff.append("\"node");
	    buff.append(node.getLink());
	    buff.append("\" [constraint=false, label=\"");
	    buff.append(node.getLink());
	    buff.append("\"]");
	    buff.append(";\n");
	}
    }

    private void write(final StringBuilder buff) throws IOException {
	Writer out = null;
	try {
	    out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF-8"));
	    out.write(buff.toString());
	    out.flush();
	} catch (IOException e) {
	    throw e;
	} finally {
	    if (out != null) {
		out.close();
	    }
	}
    }

    /**
     * Define class that will be executed in methods.
     * 
     * @author jajir
     * 
     */
    interface Execute {

	/**
	 * Each method calls this method.
	 * 
	 * @return generic method object
	 */
	Object execute();
    }

}
