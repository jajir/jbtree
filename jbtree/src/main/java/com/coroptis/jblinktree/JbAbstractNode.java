package com.coroptis.jblinktree;

import com.coroptis.jblinktree.type.Wrapper;

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
 * Node abstract class. Define node id and node data definition operations.
 * Implementations should resolve field related operations.
 *
 * @author jajir
 *
 * @param <K>
 *            key type
 * @param <V>
 *            value type
 */
public abstract class JbAbstractNode<K, V> implements Node<K, V> {

    /**
     * Holds node id.
     */
    private final Integer id;

    /**
     * Node data definition.
     */
    private final JbNodeDef<K, V> nodeDef;

    /**
     * Constructor that assign node id and node data definition.
     *
     * @param nodeId
     *            required node id
     * @param jbNodeDef
     *            required node data definition
     */
    protected JbAbstractNode(final Integer nodeId,
            final JbNodeDef<K, V> jbNodeDef) {
        this.id = nodeId;
        this.nodeDef = jbNodeDef;
    }

    /**
     * @return the nodeDef
     */
    @Override
    public final JbNodeDef<K, V> getNodeDef() {
        return nodeDef;
    }

    /**
     * Compare field of this object with given one.
     *
     * @param fieldA
     *            required compared field
     * @param fieldB
     *            required compared field
     * @return <code>true</code> when content of fields is same otherwise return
     *         <code>false</code>
     */
    protected final boolean fieldEquals(final byte[] fieldA,
            final byte[] fieldB) {
        if (fieldB.length == fieldA.length) {
            for (int i = 0; i < fieldB.length; i++) {
                if (fieldB[i] != fieldA[i]) {
                    return false;
                }
            }
            return true;
        } else {
            return false;
        }
    }

    @Override
    public final Wrapper<K> getMaxKey() {
        if (isEmpty()) {
            return null;
        } else {
            return Wrapper.make(getKey(getKeyCount() - 1),
                    getNodeDef().getKeyTypeDescriptor());
        }
    }

    @Override
    public final K getKey(final int position) {
        return getNodeDef().getKeyTypeDescriptor().load(getFieldBytes(),
                getNodeDef().getKeyPosition(position));
    }

    @Override
    public final V getValue(final int position) {
        return getNodeDef().getValueTypeDescriptor().load(getFieldBytes(),
                getNodeDef().getValuePosition(position));
    }

    @Override
    public final void setKey(final int position, final Wrapper<K> value) {
        getNodeDef().getKeyTypeDescriptor().save(getFieldBytes(),
                getNodeDef().getKeyPosition(position), value);
    }

    @Override
    public final void setValue(final int position, final V value) {
        getNodeDef().getValueTypeDescriptor().save(getFieldBytes(),
                getNodeDef().getValuePosition(position), value);
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
    protected final boolean equal(final Object a, final Object b) {
        return a == b || a != null && a.equals(b);
    }

    @Override
    public final Integer getId() {
        return id;
    }

    @Override
    public final boolean isEmpty() {
        return getKeyCount() == 0;
    }

    @Override
    public final int getMaxKeyIndex() {
        if (isEmpty()) {
            return Node.EMPTY_INT;
        } else {
            return getKeyCount() - 1;
        }
    }

    @Override
    public final boolean isLeafNode() {
        return FLAG_LEAF_NODE == getFlag();
    }

    @Override
    public final byte getFlag() {
        return getFieldBytes()[FLAG_BYTE_POSITION];
    }

    @Override
    public final void setFlag(final byte flag) {
        getFieldBytes()[FLAG_BYTE_POSITION] = flag;
    }

    @Override
    public final int compareKey(final int position, final Wrapper<K> key) {
        return getNodeDef().getKeyTypeDescriptor().cmp(getFieldBytes(),
                getNodeDef().getKeyPosition(position), key);
    }

    /**
     * Override {@link System#toString()} method.
     */
    @Override
    public final String toString() {
        StringBuilder buff = new StringBuilder();
        buff.append("Node{id=");
        buff.append(getId());
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

}
