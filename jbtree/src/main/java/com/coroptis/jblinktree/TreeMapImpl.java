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
 * <p>
 * Working with closed tree could leads to exceptions.
 * </p>
 *
 * @author jajir
 *
 * @param <K>
 *            key type
 * @param <V>
 *            value type
 */
public final class TreeMapImpl<K, V> implements TreeMap<K, V> {

    /**
     * reference to tree.
     */
    private final JbTree<K, V> tree;

    /**
     * Key type descriptor.
     */
    private final TypeDescriptor<K> keyTypeDescriptor;

    /**
     * Value type descriptor.
     */
    private final TypeDescriptor<V> valueTypeDescriptor;

    private boolean isClosed;

    /**
     * Constructor should by called just from builder.
     *
     * @param jbTree
     *            required tree
     * @param treeData
     *            required tree data descriptor
     */
    TreeMapImpl(final JbTree<K, V> jbTree, final JbTreeData<K, V> treeData) {
        this.tree = Preconditions.checkNotNull(jbTree);
        this.keyTypeDescriptor = Preconditions.checkNotNull(
                treeData.getLeafNodeDescriptor().getKeyTypeDescriptor());
        this.valueTypeDescriptor = Preconditions.checkNotNull(
                treeData.getLeafNodeDescriptor().getValueTypeDescriptor());
        isClosed = false;
    }

    /**
     * Verify key type and size.
     *
     * @param object
     *            required object
     * @return typed key
     */
    @SuppressWarnings("unchecked")
    private K verifyKey(final Object object) {
        Preconditions.checkNotNull(object, "key can't be null.");
        keyTypeDescriptor.verifyType(object);
        return (K) object;
    }

    /**
     * Verify value type and size.
     *
     * @param object
     *            required object
     * @return typed value
     */
    @SuppressWarnings("unchecked")
    private V verifyValue(final Object object) {
        Preconditions.checkNotNull(object, "value can't be null.");
        valueTypeDescriptor.verifyType(object);
        return (V) object;
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean containsKey(final Object key) {
        checkIsClosed();
        return tree.containsKey(verifyKey(key));
    }

    @Override
    public boolean containsValue(final Object value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Set<Map.Entry<K, V>> entrySet() {
        throw new UnsupportedOperationException();
    }

    @Override
    public V get(final Object key) {
        checkIsClosed();
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
    public V put(final K key, final V value) {
        checkIsClosed();
        return tree.insert(verifyKey(key), verifyValue(value));
    }

    @Override
    public void putAll(final Map<? extends K, ? extends V> m) {
        throw new UnsupportedOperationException();
    }

    @Override
    public V remove(final Object key) {
        checkIsClosed();
        return tree.remove(verifyKey(key));
    }

    @Override
    public int size() {
        checkIsClosed();
        return tree.countValues();
    }

    @Override
    public Collection<V> values() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void verify() {
        checkIsClosed();
        tree.verify();
    }

    @Override
    @Deprecated
    public void visit(final JbTreeVisitor<K, V> treeVisitor) {
        checkIsClosed();
        tree.visit(treeVisitor);
    }

    @Override
    public void visit(final JbDataVisitor<K, V> dataVisitor) {
        checkIsClosed();
        tree.visit(dataVisitor);
    }

    @Override
    public int countLockedNodes() {
        checkIsClosed();
        return tree.countLockedNodes();
    }

    @Override
    public void close() {
        tree.close();
    }

    private void checkIsClosed() {
        if (isClosed) {
            throw new JblinktreeException("Attempt to work with closed tree.");
        }
    }

}
