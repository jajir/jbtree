package com.coroptis.jblinktree;

import java.util.Arrays;

import com.coroptis.jblinktree.type.TypeDescriptorInteger;
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
     * Byte array with node data.
     */
    private byte[] field;

    /**
     * Node data definition.
     */
    private final JbNodeDef<K, V> nodeDef;

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
    public NodeImpl(final Integer nodeId, final boolean isLeafNode,
            final JbNodeDef<K, V> jbNodeDef) {
        this.id = nodeId;
        this.nodeDef = jbNodeDef;
        this.field = new byte[jbNodeDef.getFieldActualLength(0)];
        if (isLeafNode) {
            setFlag(M);
        } else {
            setFlag((byte) -3);
        }
        setLink(Node.EMPTY_INT);
    }

    /**
     *
     *
     * @param nodeId
     *            required node id
     * @param nodeField
     *            required field with node byte array with data
     */
    public NodeImpl(final Integer nodeId, final byte[] sourceField,
            final JbNodeDef<K, V> jbNodeDef) {
        this.id = nodeId;
        this.nodeDef = jbNodeDef;
        this.field = new byte[sourceField.length];
        System.arraycopy(sourceField, 0, this.field, 0, this.field.length);
        if (getFlag() != Node.M && !(nodeDef
                .getValueTypeDescriptor() instanceof TypeDescriptorInteger)) {
            throw new JblinktreeException(
                    "Non-leaf node doesn't have value of type integer, it's "
                            + nodeDef.getValueTypeDescriptor().getClass()
                                    .getName());
        }
    }

    @Override
    public Integer getLink() {
        return nodeDef.getLinkTypeDescriptor().load(field, getPositionOfLink());
    }

    @Override
    public void setLink(final Integer link) {
        nodeDef.getLinkTypeDescriptor().save(field, getPositionOfLink(), link);
    }

    /**
     * Return position of link to next node in byte array.
     *
     * @return position of link to next node
     */
    private int getPositionOfLink() {
        return field.length - nodeDef.getLinkTypeDescriptor().getMaxLength();
    }

    @Override
    public boolean isEmpty() {
        return getKeyCount() == 0;
    }

    @Override
    public int getKeyCount() {
        return (field.length - JbNodeDef.FLAGS_LENGTH
                - nodeDef.getLinkTypeDescriptor().getMaxLength())
                / nodeDef.getKeyAndValueSize();
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
        byte[] tmp = new byte[nodeDef.getFieldActualLength(getKeyCount() + 1)];
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
    }

    @Override
    public void removeKeyValueAtPosition(final int position) {
        byte[] tmp = new byte[nodeDef.getFieldActualLength(getKeyCount() - 1)];
        copyFlagAndLink(field, tmp);
        if (position > 0) {
            copy(field, 0, tmp, 0, position);
        }
        if (getKeyCount() - position - 1 > 0) {
            copy(field, position + 1, tmp, position,
                    getKeyCount() - position - 1);
        }
        field = tmp;
    }

    private void copy(byte[] from, final int srcPos1, byte[] to,
            final int destPos1, final int length) {
        System.arraycopy(from, nodeDef.getValuePosition(srcPos1), to,
                nodeDef.getValuePosition(destPos1),
                length * nodeDef.getKeyAndValueSize());
    }

    private void copyFlagAndLink(byte[] from, byte[] to) {
        to[0] = from[0];
        to[to.length - 4] = from[from.length - 4];
        to[to.length - 3] = from[from.length - 3];
        to[to.length - 2] = from[from.length - 2];
        to[to.length - 1] = from[from.length - 1];
    }

    @Override
    public void moveTopHalfOfDataTo(final Node<K, V> nodea) {
        final NodeImpl<K, V> node = (NodeImpl<K, V>) nodea;
        Preconditions.checkArgument(node.isEmpty());
        if (getKeyCount() < 1) {
            throw new JblinktreeException(
                    "In node " + id + " are no values to move.");
        }
        final int startIndex = getKeyCount() / 2;
        final int length = getKeyCount() - startIndex;

        // copy top half to empty node
        node.field = new byte[nodeDef.getFieldActualLength(length)];
        copy(field, startIndex, node.field, 0, length);
        copyFlagAndLink(field, node.field);

        // remove copied data from this node
        byte[] tmp = new byte[nodeDef.getFieldActualLength(startIndex)];
        copyFlagAndLink(field, tmp);
        copy(field, 0, tmp, 0, startIndex);
        field = tmp;
    }

    @Override
    public K getMaxKey() {
        if (isEmpty()) {
            return null;
        } else {
            return getKey(getKeyCount() - 1);
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
        for (int i = 0; i < getKeyCount(); i++) {
            if (i != 0) {
                buff.append(", ");
            }

            buff.append("<");
            buff.append(getKey(i));
            buff.append(", ");
            buff.append(getValue(i));
            buff.append(">");
        }
        buff.append("], flag=");
        buff.append(getFlag());
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
        return M == getFlag();
    }

    @Override
    public boolean verify() {
        if (!isLeafNode()) {
            for (int i = 0; i < getKeyCount(); i++) {
                if (getValue(i) != null && getValue(i).equals(id)) {
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
        return equal(id, n.id) && fieldEquals(n.field);
    }

    private boolean fieldEquals(final byte[] nField) {
        if (field.length == nField.length) {
            for (int i = 0; i < field.length; i++) {
                if (field[i] != nField[i]) {
                    return false;
                }
            }
            return true;
        } else {
            return false;
        }
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
        return field;
    }

    @Override
    public K getKey(final int position) {
        return nodeDef.getKeyTypeDescriptor().load(field,
                nodeDef.getKeyPosition(position));
    }

    @Override
    public V getValue(final int position) {
        return nodeDef.getValueTypeDescriptor().load(field,
                nodeDef.getValuePosition(position));
    }

    @Override
    public void setKey(final int position, final K value) {
        nodeDef.getKeyTypeDescriptor().save(field,
                nodeDef.getKeyPosition(position), value);
    }

    @Override
    public void setValue(final int position, final V value) {
        nodeDef.getValueTypeDescriptor().save(field,
                nodeDef.getValuePosition(position), value);
    }

    /**
     * @return the nodeDef
     */
    @Override
    public JbNodeDef<K, V> getNodeDef() {
        return nodeDef;
    }

    @Override
    public byte getFlag() {
        return field[0];
    }

    @Override
    public void setFlag(final byte flag) {
        this.field[0] = flag;
    }
}
