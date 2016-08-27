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

    /**
     * Holds node id.
     */
    private final Integer id;

    /**
     * Byte array with key value pairs, link and flags.
     */
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
     *
     * @param nodeId
     *            required node id
     * @param nodeField
     *            required field with node byte array with data
     */
    public NodeImpl(final Integer nodeId, final Field<K, V> nodeField) {
        this.id = nodeId;
        this.field = nodeField;
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
        return getKeyCount() == 0;
    }

    @Override
    public int getKeyCount() {
        return field.getKeyCount();
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
    @Override
    public void insertToPosition(final K key, final V value,
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
    public void removeKeyValueAtPosition(final int position) {
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
        if (getKeyCount() < 1) {
            throw new JblinktreeException(
                    "In node " + id + " are no values to move.");
        }
        // copy top half to empty node
        final int startIndex = getKeyCount() / 2;
        final int length = field.getKeyCount() - startIndex;
        // TODO create field in static factory
        node.field = new FieldImpl<K, V>(length, field.getNodeDef());
        node.field.copy(field, startIndex, 0, length);
        node.setLink(getLink());

        // remove copied data from this node
        Field<K, V> field2 =
                new FieldImpl<K, V>(startIndex, field.getNodeDef());
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
    public V getValueByKey(final K key) {
        Preconditions.checkNotNull(key);
        for (int i = 0; i < field.getKeyCount(); i++) {
            if (key.equals(field.getKey(i))) {
                return field.getValue(i);
            }
        }
        return null;
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
    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof NodeImpl)) {
            return false;
        }
        final NodeImpl<K, V> n = (NodeImpl<K, V>) obj;
        return equal(id, n.id) && field.equals(n.field);
    }

    /**
     * It's just copy of com.google.common.base.Objects.equals(Object).
     *
     * @param a
     *            optional object
     * @param b
     *            optional object
     * @return return <code>true</code> when object are equals otherwise return
     *         <code>false</code>
     */
    private boolean equal(final Object a, final Object b) {
        return a == b || a != null && a.equals(b);
    }

    @Override
    public byte[] getFieldBytes() {
        return field.getBytes();
    }

    @Override
    public K getKey(final int position) {
        return field.getKey(position);
    }

    @Override
    public V getValue(final int position) {
        return field.getValue(position);
    }

    @Override
    public void setKey(final int position, final K value) {
        field.setKey(position, value);
    }

    @Override
    public void setValue(final int position, final V value) {
        field.setValue(position, value);
    }

    /**
     * @return the nodeDef
     */
    @Override
    public JbNodeDef<K, V> getNodeDef() {
        return field.getNodeDef();
    }
}
