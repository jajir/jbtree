package com.coroptis.jblinktree;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Objects;

import com.coroptis.jblinktree.type.Wrapper;
import com.coroptis.jblinktree.util.JblinktreeException;

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
public final class JbTreeWrapper<K, V> implements JbTree<K, V> {

    /**
     * Tree.
     */
    private final JbTree<K, V> tree;

    /**
     * Node store.
     */
    private final NodeStore<K> nodeStore;

    /**
     * In this file will be saved tree.
     */
    private final File file;

    /**
     * Tree data description.
     */
    private final JbTreeData<K, V> treeData;

    /**
     * Node service.
     */
    private final JbNodeService<K, V> nodeService;

    /**
     * Simple constructor.
     *
     * @param jbTree
     *            required tree
     * @param jbTreeData
     *            required tree data description
     * @param initNodeStore
     *            required node store
     * @param fileName
     *            required file name which will contain tree in .dot format
     * @param jbNodeService
     *            node service
     */
    JbTreeWrapper(final JbTree<K, V> jbTree, final JbTreeData<K, V> jbTreeData,
            final NodeStore<K> initNodeStore, final String fileName,
            final JbNodeService<K, V> jbNodeService) {
        this.tree = Objects.requireNonNull(jbTree);
        this.nodeStore = Objects.requireNonNull(initNodeStore);
        this.treeData = Objects.requireNonNull(jbTreeData);
        this.file = new File(fileName);
        this.nodeService = Objects.requireNonNull(jbNodeService);
    }

    @SuppressWarnings("unchecked")
    @Override
    public V insert(final Wrapper<K> key, final V value) {
        return (V) saveExecution(new Execute() {

            @Override
            public Object execute() {
                return tree.insert(key, value);
            }
        });
    }

    @SuppressWarnings("unchecked")
    @Override
    public V remove(final Wrapper<K> key) {
        return (V) saveExecution(new Execute() {

            @Override
            public Object execute() {
                return tree.remove(key);
            }
        });
    }

    @SuppressWarnings("unchecked")
    @Override
    public V search(final Wrapper<K> key) {
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
    public boolean containsKey(final Wrapper<K> key) {
        return (Boolean) saveExecution(new Execute() {

            @Override
            public Object execute() {
                return tree.containsKey(key);
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

    /**
     * Length of indentation in tree .dot representation.
     */
    private static final String INDENDATION = "    ";

    /**
     * Writes tree data to .dot file. Useful for debugging.
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    private void printData() {
        final StringBuilder buff = new StringBuilder();

        buff.append("digraph graphname {\n");
        buff.append(INDENDATION);
        buff.append("edge [label=0];\n");
        buff.append(INDENDATION);
        buff.append("graph [ranksep=1];\n");
        buff.append(INDENDATION);
        buff.append("node [shape=record]\n");

        for (int i = 0; i < treeData.getMaxNodeId(); i++) {
            nodeService.writeTo(nodeStore.get(i), buff, INDENDATION);
        }

        for (int i = 0; i < treeData.getMaxNodeId(); i++) {
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

    /**
     * Write nodes to file in .dot file syntax.
     *
     * @param node
     *            required node
     * @param buff
     *            required string buffer
     */
    private void addNextNodes(final Node<K, Integer> node,
            final StringBuilder buff) {
        for (final Object o : nodeService.getNodeIds(node)) {
            Integer i = (Integer) o;
            buff.append(INDENDATION);
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

    /**
     * Generate link to another node in .dot file syntax.
     *
     * @param node
     *            required node
     * @param buff
     *            required string buffer
     */
    private void addLink(final Node<Integer, Integer> node,
            final StringBuilder buff) {
        if (node.getLink() != null) {
            buff.append(INDENDATION);
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

    /**
     * Write content of buffer to file.
     *
     * @param buff
     *            required {@link StringBuilder}
     * @throws IOException
     *             when file operation fails
     */
    private void write(final StringBuilder buff) throws IOException {
        Writer out = null;
        try {
            out = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(file), "UTF-8"));
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

    @Override
    public void close() {
        tree.close();
    }

    @Override
    public void visit(final JbDataVisitor<K, V> dataVisitor) {
        tree.visit(dataVisitor);
    }

}
