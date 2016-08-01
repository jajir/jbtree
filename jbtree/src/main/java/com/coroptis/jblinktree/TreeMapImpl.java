package com.coroptis.jblinktree;

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

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import com.coroptis.jblinktree.type.TypeDescriptor;
import com.google.common.base.Preconditions;

/**
 * {@link Map} Implementation. Wrap JBtree map. Not all methods are fully
 * implemented.
 * 
 * @author jajir
 * 
 * @param <K>
 *            key type
 * @param <V>
 *            value type
 */
public class TreeMapImpl<K, V> implements TreeMap<K, V> {

    private final JbTree<K, V> tree;

    private final TypeDescriptor<K> keyTypeDescriptor;

    private final TypeDescriptor<V> valueTypeDescriptor;

    TreeMapImpl(final JbTree<K, V> tree, final JbTreeData<K, V> treeData) {
	this.tree = Preconditions.checkNotNull(tree);
	this.keyTypeDescriptor = Preconditions.checkNotNull(treeData.getKeyTypeDescriptor());
	this.valueTypeDescriptor = Preconditions.checkNotNull(treeData.getValueTypeDescriptor());
    }

    @SuppressWarnings("unchecked")
    private K verifyKey(Object object) {
	Preconditions.checkNotNull(object, "key can't be null.");
	keyTypeDescriptor.verifyType(object);
	return (K) object;
    }

    @SuppressWarnings("unchecked")
    private V verifyValue(Object object) {
	Preconditions.checkNotNull(object, "value can't be null.");
	valueTypeDescriptor.verifyType(object);
	return (V) object;
    }

    @Override
    public void clear() {
	throw new UnsupportedOperationException();
    }

    @Override
    public boolean containsKey(Object key) {
	return tree.containsKey(verifyKey(key));
    }

    @Override
    public boolean containsValue(Object value) {
	throw new UnsupportedOperationException();
    }

    @Override
    public Set<java.util.Map.Entry<K, V>> entrySet() {
	throw new UnsupportedOperationException();
    }

    @Override
    public V get(Object key) {
	return tree.search(verifyKey(key));
    }

    @Override
    public boolean isEmpty() {
	throw new UnsupportedOperationException();
    }

    @Override
    public Set<K> keySet() {
	throw new UnsupportedOperationException();
    }

    @Override
    public V put(K key, V value) {
	return tree.insert(verifyKey(key), verifyValue(value));
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
	throw new UnsupportedOperationException();
    }

    @Override
    public V remove(Object key) {
	return tree.remove(verifyKey(key));
    }

    @Override
    public int size() {
	return tree.countValues();
    }

    @Override
    public Collection<V> values() {
	throw new UnsupportedOperationException();
    }

    @Override
    public void verify() {
	tree.verify();
    }

    @Override
    @Deprecated
    public void visit(final JbTreeVisitor<K, V> treeVisitor) {
	tree.visit(treeVisitor);
    }

    @Override
    public void visit(final JbDataVisitor<K, V> dataVisitor) {
	//FIXME this does't provide ordered output.
	tree.visit(new JbTreeVisitor<K, V>() {

	    @Override
	    public boolean visitedLeaf(final Node<K, V> node) {
		for (final K key : node.getKeys()) {
		    V value = node.getValue(key);
		    if (!dataVisitor.visited(key, value)) {
			return false;
		    }
		}
		return true;
	    }

	    @Override
	    public boolean visitedNonLeaf(final Node<K, Integer> node) {
		return true;
	    }
	});
    }

    @Override
    public int countLockedNodes() {
	return tree.countLockedNodes();
    }

}
