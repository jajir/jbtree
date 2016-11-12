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

import java.util.Arrays;

import com.coroptis.jblinktree.type.TypeDescriptorInteger;
import com.coroptis.jblinktree.type.Wrapper;
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
 * <td>key value pair count</td>
 * <td>flag</td>
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
 * <td>5</td>
 * <td>6</td>
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
 * Second value P0 at index 0 have special meaning, when it's
 * {@link Node#FLAG_LEAF_NODE} than this node is leaf node. In all other cases
 * is non-leaf node.
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
public final class NodeFixedLength<K, V> extends JbAbstractNode<K, V> {

    /**
     * Byte array with node data.
     */
    private byte[] field;

    /**
     * Create and initialize node.
     *
     * @param nodeId
     *            required node id, node will be referred with this id.
     * @param isLeafNode
     *            required value, when it's <code>true</code> than it's leaf
     *            node otherwise it's non-leaf node.
     * @param jbNodeDef
     *            required tree definition
     */
    public NodeFixedLength(final Integer nodeId, final boolean isLeafNode,
            final JbNodeDef<K, V> jbNodeDef) {
        super(nodeId, jbNodeDef);
        this.field = new byte[jbNodeDef.getFieldMaxLength() + 1];
        if (isLeafNode) {
            setFlag(FLAG_LEAF_NODE);
        } else {
            setFlag(FLAG_NON_LEAF_NODE);
        }
        setLink(Node.EMPTY_INT);
        setKeyCount(0);
    }

    /**
     * Node constructor.
     *
     * @param nodeId
     *            required node id
     * @param sourceField
     *            required node node data field
     * @param jbNodeDef
     *            required node data definition
     */
    public NodeFixedLength(final Integer nodeId, final byte[] sourceField,
            final JbNodeDef<K, V> jbNodeDef) {
        super(nodeId, jbNodeDef);
        this.field = new byte[1 + getNodeDef().getFieldMaxLength()];
        System.arraycopy(sourceField, 0, field, 1, sourceField.length);
        setKeyCount((sourceField.length - 1
                - getNodeDef().getLinkTypeDescriptor().getMaxLength())
                / getNodeDef().getKeyAndValueSize());
        if (getFlag() != Node.FLAG_LEAF_NODE && !(getNodeDef()
                .getValueTypeDescriptor() instanceof TypeDescriptorInteger)) {
            throw new JblinktreeException(
                    "Non-leaf node doesn't have value of type integer, it's "
                            + getNodeDef().getValueTypeDescriptor().getClass()
                                    .getName());
        }
    }

    @Override
    public Integer getLink() {
        return getNodeDef().getLinkTypeDescriptor().load(field,
                getPositionOfLink());
    }

    @Override
    public void setLink(final Integer link) {
        getNodeDef().getLinkTypeDescriptor().save(field, getPositionOfLink(),
                link);
    }

    /**
     * Return position of link to next node in byte array.
     *
     * @return position of link to next node
     */
    private int getPositionOfLink() {
        return getKeyCount() * getNodeDef().getKeyAndValueSize() + 2;
    }

    @Override
    public int getKeyCount() {
        return field[POSITION_OF_PAIRS_COUNT];
    }

    /**
     * Allows to set number of key value pairs.
     *
     * @param keyCount
     *            required new key value pars count
     */
    public void setKeyCount(final int keyCount) {
        field[POSITION_OF_PAIRS_COUNT] = (byte) keyCount;
    }

    @Override
    public void insertAtPosition(final Wrapper<K> key, final V value,
            final int targetIndex) {
        // move link
        for (int i = 0; i < getNodeDef().getLinkTypeDescriptor()
                .getMaxLength(); i++) {
            final int from =
                    getKeyCount() * getNodeDef().getKeyAndValueSize() + 2 + i;
            final int to = from + getNodeDef().getKeyAndValueSize();
            field[to] = field[from];
        }

        final int keyValuePairsToMove = getKeyCount() - targetIndex;
        if (keyValuePairsToMove > 0) {
            for (int i = getNodeDef().getKeyAndValueSize()
                    * keyValuePairsToMove; i > 0; i--) {
                final int from =
                        i + targetIndex * getNodeDef().getKeyAndValueSize() + 2;
                final int to = from + getNodeDef().getKeyAndValueSize();
                field[to] = field[from];
            }
        }

        setKey(targetIndex, key);
        setValue(targetIndex, value);
        setKeyCount(getKeyCount() + 1);
    }

    @Override
    public void removeAtPosition(final int targetIndex) {
        final int keyValuePairsToMove = getKeyCount() - 1 - targetIndex;
        if (keyValuePairsToMove > 0) {
            for (int i = 0; i < getNodeDef().getKeyAndValueSize()
                    * keyValuePairsToMove; i++) {
                final int to =
                        i + targetIndex * getNodeDef().getKeyAndValueSize() + 2;
                final int from = to + getNodeDef().getKeyAndValueSize();
                field[to] = field[from];
            }
        }

        // move link
        for (int i = 0; i < getNodeDef().getLinkTypeDescriptor()
                .getMaxLength(); i++) {
            final int to =
                    (getKeyCount() - 1) * getNodeDef().getKeyAndValueSize() + 2
                            + i;
            final int from = to + getNodeDef().getKeyAndValueSize();
            field[to] = field[from];
        }

        setKeyCount(getKeyCount() - 1);
    }

    @Override
    public void moveTopHalfOfDataTo(final Node<K, V> nodea) {
        final NodeFixedLength<K, V> node = (NodeFixedLength<K, V>) nodea;
        Preconditions.checkArgument(node.isEmpty());
        if (getKeyCount() < 1) {
            throw new JblinktreeException(
                    "In node " + getId() + " are no values to move.");
        }
        final int startIndex = getKeyCount() / 2;
        final int length = getKeyCount() - startIndex;

        System.arraycopy(field,
                2 + startIndex * getNodeDef().getKeyAndValueSize(), node.field,
                2, length * getNodeDef().getKeyAndValueSize());

        node.setKeyCount(length);
        node.setLink(getLink());
        node.setFlag(getFlag());
        setKeyCount(startIndex);

        setLink(node.getId());
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(new Object[] {
                /**
                 * Just comment to split field
                 */
                getId(), field });
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof NodeFixedLength)) {
            return false;
        }
        final NodeFixedLength<K, V> n = (NodeFixedLength<K, V>) obj;
        return equal(getId(), n.getId()) && fieldEquals(n.field, field);
    }

    @Override
    public byte[] getFieldBytes() {
        return field;
    }

}
