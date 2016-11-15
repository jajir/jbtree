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
import com.coroptis.jblinktree.util.JblinktreeException;
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
 * First value P0 at index 0 have special meaning, when it's
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
public final class NodeShort<K, V> extends JbAbstractNode<K, V> {

    /**
     * Byte array with node data.
     */
    private byte[] field;

    /**
     * Number of key value pairs stored in node.
     */
    private int keyCount;

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
    public NodeShort(final Integer nodeId, final boolean isLeafNode,
            final JbNodeDef<K, V> jbNodeDef) {
        super(nodeId, jbNodeDef);
        this.field = new byte[jbNodeDef.getFieldActualLength(0)];
        if (isLeafNode) {
            setFlag(FLAG_LEAF_NODE);
        } else {
            setFlag(FLAG_NON_LEAF_NODE);
        }
        setLink(Node.EMPTY_INT);
        updateKeyCount();
    }

    /**
     *
     *
     * @param nodeId
     *            required node id
     * @param sourceField
     *            required node node data field
     * @param jbNodeDef
     *            required node data definition
     */
    public NodeShort(final Integer nodeId, final byte[] sourceField,
            final JbNodeDef<K, V> jbNodeDef) {
        super(nodeId, jbNodeDef);
        this.field = new byte[sourceField.length];
        System.arraycopy(sourceField, 0, this.field, 0, this.field.length);
        if (getFlag() != Node.FLAG_LEAF_NODE && !(getNodeDef()
                .getValueTypeDescriptor() instanceof TypeDescriptorInteger)) {
            throw new JblinktreeException(
                    "Non-leaf node doesn't have value of type integer, it's "
                            + getNodeDef().getValueTypeDescriptor().getClass()
                                    .getName());
        }
        updateKeyCount();
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
        return field.length
                - getNodeDef().getLinkTypeDescriptor().getMaxLength();
    }

    @Override
    public int getKeyCount() {
        return keyCount;
    }

    /**
     * Update key count property. This method could be in getter. It's separated
     * because of speed.
     */
    private void updateKeyCount() {
        keyCount = (field.length - JbNodeDef.FLAGS_LENGTH
                - getNodeDef().getLinkTypeDescriptor().getMaxLength())
                / getNodeDef().getKeyAndValueSize();
    }

    @Override
    public void insertAtPosition(final Wrapper<K> key, final V value,
            final int targetIndex) {
        byte[] tmp =
                new byte[getNodeDef().getFieldActualLength(getKeyCount() + 1)];
        copyFlagAndLink(field, tmp);
        if (targetIndex > 0) {
            copy(field, 0, tmp, 0, targetIndex);
        }
        if (getKeyCount() > targetIndex) {
            copy(field, targetIndex, tmp, targetIndex + 1,
                    getKeyCount() - targetIndex);
        }
        field = tmp;
        setKey(targetIndex, key);
        setValue(targetIndex, value);
        updateKeyCount();
    }

    @Override
    public void removeAtPosition(final int position) {
        byte[] tmp =
                new byte[getNodeDef().getFieldActualLength(getKeyCount() - 1)];
        copyFlagAndLink(field, tmp);
        if (position > 0) {
            copy(field, 0, tmp, 0, position);
        }
        if (getKeyCount() - position - 1 > 0) {
            copy(field, position + 1, tmp, position,
                    getKeyCount() - position - 1);
        }
        field = tmp;
        updateKeyCount();
    }

    /**
     * Copy selected position from one byte array to another one.
     * <p>
     * Positions are positions of key value pairs in node not position of byte.
     * </p>
     *
     * @param from
     *            required source field
     * @param srcPos1
     *            required where copying starts
     * @param to
     *            required target field
     * @param destPos1
     *            required target node position
     * @param length
     *            required, how many key value pairs will be copied
     */
    private void copy(final byte[] from, final int srcPos1, final byte[] to,
            final int destPos1, final int length) {
        System.arraycopy(from, getNodeDef().getValuePosition(srcPos1), to,
                getNodeDef().getValuePosition(destPos1),
                length * getNodeDef().getKeyAndValueSize());
    }

    /**
     * Copy flag and link value from one byte field to another one.
     *
     * @param from
     *            required from field
     * @param to
     *            required to field
     */
    private void copyFlagAndLink(final byte[] from, final byte[] to) {
        to[FLAG_BYTE_POSITION] = from[FLAG_BYTE_POSITION];
        for (int i = 1; i <= getNodeDef().getLinkTypeDescriptor()
                .getMaxLength(); i++) {
            to[to.length - i] = from[from.length - i];
        }
    }

    @Override
    public void moveTopHalfOfDataTo(final Node<K, V> nodea) {
        final NodeShort<K, V> node = (NodeShort<K, V>) nodea;
        Preconditions.checkArgument(node.isEmpty());
        if (getKeyCount() < 1) {
            throw new JblinktreeException(
                    "In node " + getId() + " are no values to move.");
        }
        final int startIndex = getKeyCount() / 2;
        final int length = getKeyCount() - startIndex;

        // copy top half to empty node
        node.field = new byte[getNodeDef().getFieldActualLength(length)];
        copy(field, startIndex, node.field, 0, length);
        copyFlagAndLink(field, node.field);
        node.updateKeyCount();

        // remove copied data from this node
        byte[] tmp = new byte[getNodeDef().getFieldActualLength(startIndex)];
        copyFlagAndLink(field, tmp);
        copy(field, 0, tmp, 0, startIndex);
        field = tmp;
        setLink(node.getId());
        updateKeyCount();
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
        if (!(obj instanceof NodeShort)) {
            return false;
        }
        final NodeShort<K, V> n = (NodeShort<K, V>) obj;
        return equal(getId(), n.getId()) && fieldEquals(n.field, field);
    }

    @Override
    public byte[] getFieldBytes() {
        return field;
    }

}
