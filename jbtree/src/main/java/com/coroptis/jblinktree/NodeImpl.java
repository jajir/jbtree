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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.common.base.Preconditions;

/**
 * <p>
 * Generally in tree are inserted keys and values (K,V). There are two kind of
 * nodes:
 * </p>
 * <ul>
 * <li>leaf node - contains keys and values pairs</li>
 * <li>non-leaf node - contains keys and pointers to another nodes</li>
 * </ul>
 * <p>
 * Node contains data stored in a following field:
 * </p>
 * <table border="1" style="border-collapse:collapse" summary=
 * "keys and values meaning in node">
 * <tr>
 * <td>value</td>
 * <td>P(0)</td>
 * <td>K(1)</td>
 * <td>P(1)</td>
 * <td>K(2)</td>
 * <td>P(2)</td>
 * <td>&nbsp;...&nbsp;</td>
 * <td>K(L*2)</td>
 * <td>P(L*2)</td>
 * <td>K(L*2+1)</td>
 * <td>link</td>
 * </tr>
 * <tr>
 * <td>index</td>
 * <td>0</td>
 * <td>1</td>
 * <td>2</td>
 * <td>3</td>
 * <td>4</td>
 * <td>&nbsp;...&nbsp;</td>
 * <td>L*2-1</td>
 * <td>L*2</td>
 * <td>L*2+1</td>
 * <td>L*2+2</td>
 * </tr>
 * </table>
 * Where:
 * <ul>
 * <li>P - pointer to some another node</li>
 * <li>K - Key inserted into tree, this value represents some user's data. Key
 * could be ordered and compared.</li>
 * <li>V - value inserted into tree</li>
 * <li>L - main parameter of tree, in tree could be maximally L nodes.</li>
 * <li>link - pointer to next sibling node</li>
 * <li>K(L*2+1) - highest key value from node in case of leaf node or highest
 * key value from all referenced nodes.</li>
 * </ul>
 * First value P0 at index 0 have special meaning, when it's {@link NodeImpl#M}
 * than this node is leaf node. In all other cases is non-leaf node.
 * <p>
 * Node is not thread save.
 * </p>
 *
 * @author jajir
 *
 * @param <K>
 *            key type
 * @param <V>
 *            value type
 */
public final class NodeImpl<K, V> implements Node<K, V> {

    private final Integer id;

    private Field<K, V> field;

    /**
     * Create and initialize node.
     *
     * @param nodeId
     *            required node id, node will be referred with this id.
     * @param isLeafNode
     *            required value, when it's <code>true</code> than it's leaf
     *            node otherwise it's non-leaf node.
     * @param treeData
     *            required tree definition
     */
    public NodeImpl(final Integer nodeId, final boolean isLeafNode,
            final JbNodeDef<K, V> treeData) {
        this.id = nodeId;
        /**
         * There is three position even in empty node: P0, max key and link.
         */
        field = new FieldImpl<K, V>(0, treeData);
        if (isLeafNode) {
            field.setFlag(M);
        }
        setLink(EMPTY_INT);
    }

    /**
     *
     * @param nodeId
     *            required node id
     * @param field
     *            required field with node byte array with data
     */
    public NodeImpl(final Integer nodeId, final Field<K, V> field) {
        this.id = nodeId;
        this.field = field;
    }

    @Override
    public Integer getLink() {
        return field.getLink();
    }

    @Override
    public void setLink(final Integer link) {
        field.setLink(link);
    }

    @Override
    public boolean isEmpty() {
        return getKeysCount() == 0;
    }

    @Override
    public int getKeysCount() {
        return field.getKeyCount();
    }

    @Override
    public void insert(final K key, final V value) {
        Preconditions.checkNotNull(key);
        Preconditions.checkNotNull(value);
        for (int i = 0; i < field.getKeyCount(); i++) {
            if (key.equals(field.getKey(i))) {
                /**
                 * Rewrite value.
                 */
                field.setValue(i, value);
                return;
            } else if (field.getNodeDef().getKeyTypeDescriptor()
                    .compare(field.getKey(i), key) > 0) {
                // field.get(i) > key
                couldInsertedKey();
                /**
                 * given value should be inserted 1 before current index
                 */
                insertToPosition(key, value, i);
                return;
            }
        }
        couldInsertedKey();
        /**
         * New key is bigger than all others so should be at the end.
         */
        insertToPosition(key, value, field.getKeyCount());
    }

    /**
     * When new value can't be inserted into this node it throws
     * {@link JblinktreeException}.
     */
    private void couldInsertedKey() {
        if (getKeysCount() >= field.getNodeDef().getL()) {
            throw new JblinktreeException("Leaf (" + id
                    + ") is full another value can't be inserted.");
        }
    }

    /**
     * Insert key and value to some specific index position in field.
     *
     * @param key
     *            required key
     * @param value
     *            required value
     * @param targetIndex
     *            required target index in field
     */
    private void insertToPosition(final K key, final V value,
            final int targetIndex) {
        /**
         * It's create new node with new key & value pair and than copy data
         * from current node. Finally switch new node to current on.
         */
        Field<K, V> field2 = new FieldImpl<K, V>(field.getKeyCount() + 1,
                field.getNodeDef());
        field2.setFlag(field.getFlag());
        field2.setLink(field.getLink());
        field2.setValue(targetIndex, value);
        field2.setKey(targetIndex, key);

        if (targetIndex > 0) {
            field2.copy(field, 0, 0, targetIndex);
        }
        if (field.getKeyCount() > targetIndex) {
            field2.copy(field, targetIndex, targetIndex + 1,
                    field.getKeyCount() - targetIndex);
        }
        field = field2;
    }

    @Override
    public V remove(final K key) {
        Preconditions.checkNotNull(key);
        for (int i = 0; i < field.getKeyCount(); i++) {
            if (key.equals(field.getKey(i))) {
                /**
                 * Remove key and value.
                 */
                final V oldValue = field.getValue(i);
                removeKeyValueAtPosition(i);
                return oldValue;
            } else if (field.getNodeDef().getKeyTypeDescriptor()
                    .compare(field.getKey(i), key) > 0) {
                /**
                 * if key in node is bigger than given key than node doesn't
                 * contains key to delete.
                 */
                return null;
            }
        }
        return null;
    }

    @Override
    public boolean updateKeyForValue(final Integer valueToUpdate,
            final K keyToSet) {
        if (isLeafNode()) {
            throw new JblinktreeException(
                    "method could by used just on non-leaf nodes");
        }
        for (int i = 0; i < field.getKeyCount(); i++) {
            if (field.getValue(i).equals(valueToUpdate)) {
                if (field.getKey(i).equals(keyToSet)) {
                    return false;
                } else {
                    field.setKey(i, keyToSet);
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Remove two bytes from node field at given position. Method doesn't care
     * about meaning of bites.
     *
     * @param position
     *            required position
     */
    private void removeKeyValueAtPosition(final int position) {
        Field<K, V> tmp = new FieldImpl<K, V>(field.getKeyCount() - 1,
                field.getNodeDef());
        if (position > 0) {
            tmp.copy(field, 0, 0, position);
        }
        if (field.getKeyCount() - position - 1 > 0) {
            tmp.copy(field, position + 1, position,
                    field.getKeyCount() - position - 1);
        }
        tmp.setLink(getLink());
        tmp.setFlag(field.getFlag());
        field = tmp;
    }

    @Override
    public void moveTopHalfOfDataTo(final Node<K, V> nodea) {
        final NodeImpl<K, V> node = (NodeImpl<K, V>) nodea;
        Preconditions.checkArgument(node.isEmpty());
        if (getKeysCount() < 1) {
            throw new JblinktreeException(
                    "In node " + id + " are no values to move.");
        }
        // copy top half to empty node
        final int startIndex = getKeysCount() / 2;
        final int length = field.getKeyCount() - startIndex;
        // TODO create field in static factory
        node.field = new FieldImpl<K, V>(length, field.getNodeDef());
        node.field.copy(field, startIndex, 0, length);
        node.setLink(getLink());

        // remove copied data from this node
        Field<K, V> field2 = new FieldImpl<K, V>(startIndex,
                field.getNodeDef());
        field2.copy(field, 0, 0, startIndex);
        field = field2;
        setLink(node.getId());
        node.field.setFlag(field.getFlag());
    }

    @Override
    public K getMaxKey() {
        if (isEmpty()) {
            return null;
        } else {
            return field.getKey(field.getKeyCount() - 1);
        }
    }

    /**
     * Override {@link System#toString()} method.
     */
    @Override
    public String toString() {
        StringBuilder buff = new StringBuilder();
        buff.append("Node{id=");
        buff.append(id);
        buff.append(", isLeafNode=");
        buff.append(isLeafNode());
        buff.append(", field=[");
        for (int i = 0; i < field.getKeyCount(); i++) {
            if (i != 0) {
                buff.append(", ");
            }

            buff.append("<");
            buff.append(field.getKey(i));
            buff.append(", ");
            buff.append(field.getValue(i));
            buff.append(">");
        }
        buff.append("], flag=");
        buff.append(field.getFlag());
        buff.append(", link=");
        buff.append(getLink());
        buff.append("}");
        return buff.toString();
    }

    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public boolean isLeafNode() {
        return M == field.getFlag();
    }

    @Override
    public Integer getCorrespondingNodeId(final K key) {
        if (isLeafNode()) {
            throw new JblinktreeException(
                    "Leaf node '" + id + "' doesn't have any child nodes.");
        }
        if (isEmpty()) {
            return getLink();
        }
        for (int i = 0; i < field.getKeyCount(); i++) {
            if (field.getNodeDef().getKeyTypeDescriptor().compare(key,
                    field.getKey(i)) <= 0) {
                // TODO re-typing should be implicit
                return (Integer) field.getValue(i);
            }
        }
        return getLink();
    }

    @Override
    public V getValueByKey(final K key) {
        Preconditions.checkNotNull(key);
        if (!isLeafNode()) {
            throw new JblinktreeException(
                    "Non-leaf node '" + id + "' doesn't have leaf value.");
        }
        for (int i = 0; i < field.getKeyCount(); i++) {
            if (key.equals(field.getKey(i))) {
                return field.getValue(i);
            }
        }
        return null;
    }

    @Override
    public List<Integer> getNodeIds() {
        final List<Integer> out = new ArrayList<Integer>();
        for (int i = 0; i < field.getKeyCount(); i++) {
            // TODO it's it should be node <K,Integer>
            out.add((Integer) field.getValue(i));
        }
        return out;
    }

    @Override
    public List<K> getKeys() {
        final List<K> out = new ArrayList<K>();
        for (int i = 0; i < field.getKeyCount(); i++) {
            out.add(field.getKey(i));
        }
        return out;
    }

    @Override
    public boolean verify() {
        if (!isLeafNode()) {
            for (int i = 0; i < field.getKeyCount(); i++) {
                if (field.getValue(i) != null && field.getValue(i).equals(id)) {
                    throw new JblinktreeException(
                            "node contains pointer to itself: " + toString());
                }
            }
        }
        return true;
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(new Object[] { id, field });
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof NodeImpl)) {
            return false;
        }
        final NodeImpl<K, V> n = (NodeImpl<K, V>) obj;
        if (equal(id, n.id) && field.equals(n.field)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * It's just copy of com.google.common.base.Objects.equals(Object).
     *
     * @param a
     * @param b
     * @return
     */
    private boolean equal(Object a, Object b) {
        return a == b || a != null && a.equals(b);
    }

    @Override
    public void writeTo(final StringBuilder buff, final String intendation) {
        buff.append(intendation);
        buff.append("node");
        buff.append(getId());
        buff.append(" [shape=record,label=\" {");
        if (isLeafNode()) {
            buff.append("L");
        } else {
            buff.append("N");
        }
        buff.append("|");
        buff.append(getId());
        buff.append("}");
        for (int i = 0; i < field.getKeyCount() - 1; i = i + 2) {
            buff.append(" | {");
            buff.append("");
            buff.append(field.getKey(i + 1));
            buff.append("| <F");
            buff.append(field.getValue(i));
            buff.append("> ");
            buff.append(field.getValue(i));
            buff.append("}");
        }
        buff.append(" | ");
        if (getLink() != null) {
            buff.append("<L");
            buff.append(getLink());
            buff.append("> ");
        }
        buff.append(getLink());
        buff.append("\"];\n");
    }

    @Override
    public byte[] getFieldBytes() {
        return field.getBytes();
    }

}
