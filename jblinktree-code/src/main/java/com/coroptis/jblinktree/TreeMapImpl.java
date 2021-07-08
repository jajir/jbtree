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
import java.util.Objects;
import java.util.Set;

import com.coroptis.jblinktree.type.TypeDescriptor;
import com.coroptis.jblinktree.type.Wrapper;
import com.coroptis.jblinktree.util.JblinktreeException;

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

    /**
     * Is <code>true</code> when tree is closed.
     */
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
        this.tree = Objects.requireNonNull(jbTree);
        this.keyTypeDescriptor = Objects.requireNonNull(
                treeData.getLeafNodeDescriptor().getKeyTypeDescriptor());
        this.valueTypeDescriptor = Objects.requireNonNull(
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
        Objects.requireNonNull(object, "key can't be null.");
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
        Objects.requireNonNull(object, "value can't be null.");
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
        return tree
                .containsKey(Wrapper.make(verifyKey(key), keyTypeDescriptor));
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
        return tree.search(Wrapper.make(verifyKey(key), keyTypeDescriptor));
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
        return tree.insert(Wrapper.make(verifyKey(key), keyTypeDescriptor),
                verifyValue(value));
    }

    @Override
    public void putAll(final Map<? extends K, ? extends V> m) {
        throw new UnsupportedOperationException();
    }

    @Override
    public V remove(final Object key) {
        checkIsClosed();
        return tree.remove(Wrapper.make(verifyKey(key), keyTypeDescriptor));
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

    /**
     * Traverse through entire tree and visit all nodes.
     * <p>
     * Method should't be called at most cases.
     * </p>
     *
     * @param treeVisitor
     *            required visitor implementation
     */
    public void visit(final JbTreeVisitor<K, V> treeVisitor) {
        checkIsClosed();
        tree.visit(treeVisitor);
    }

    @Override
    public String toString() {
        return tree.toString();
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

    /**
     * Verify that tree is not closed. When user invoke some operation on closed
     * node than {@link JblinktreeException} is thrown.
     */
    private void checkIsClosed() {
        if (isClosed) {
            throw new JblinktreeException("Attempt to work with closed tree.");
        }
    }

}
