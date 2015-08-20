package com.coroptis.jblinktree;

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

    JbTreeWrapper(final JbTree<K, V> tree, final NodeStore<K> nodeStore) {
	this.tree = Preconditions.checkNotNull(tree);
	this.nodeStore = Preconditions.checkNotNull(nodeStore);
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

    private Object saveExecution(final Execute execute) {
	try {
	    return execute.execute();
	} catch (JblinktreeException e) {

	    throw e;
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
