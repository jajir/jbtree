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

import java.util.Map;
import java.util.TreeMap;

import com.coroptis.jblinktree.type.TypeDescriptor;
import com.coroptis.jblinktree.type.TypeDescriptorInteger;
import com.coroptis.jblinktree.type.Wrapper;

public class NodeBuilder {

    public enum NodeImpl {
        fixedLength, variableLength;
    };

    public static NodeBuilder builder() {
        return new NodeBuilder();
    }

    private int l;

    private NodeImpl implentation;

    private Integer nodeId;

    private Integer linkId;

    private boolean isLeafNode;

    private TypeDescriptor<?> keyTypeDescriptor;

    private TypeDescriptor<?> valueTypeDescriptor;

    private Map<Object, Object> keyValuePairs;

    private NodeBuilder() {
        isLeafNode = true;
        keyValuePairs = new TreeMap<Object, Object>();
    }

    public NodeBuilder setImplementation(NodeImpl implentation) {
        this.implentation = implentation;
        return this;
    }

    public NodeBuilder setLeafNode(boolean isLeafNode) {
        this.isLeafNode = isLeafNode;
        return this;
    }

    public NodeBuilder setNodeId(Integer nodeId) {
        this.nodeId = nodeId;
        return this;
    }

    public NodeBuilder setLinkId(Integer linkId) {
        this.linkId = linkId;
        return this;
    }

    public NodeBuilder setKeyTypeDescriptor(
            TypeDescriptor<?> keyTypeDescriptor) {
        this.keyTypeDescriptor = keyTypeDescriptor;
        return this;
    }

    public NodeBuilder setValueTypeDescriptor(
            TypeDescriptor<?> valueTypeDescriptor) {
        this.valueTypeDescriptor = valueTypeDescriptor;
        return this;
    }

    public NodeBuilder addKeyValuePair(Object key, Object value) {
        keyValuePairs.put(key, value);
        return this;
    }

    public NodeBuilder setL(int l) {
        this.l = l;
        return this;
    }

    @SuppressWarnings("unchecked")
    public <K, V> Node<K, V> build() {
        final Node<K, V> n;
        TypeDescriptor<K> ktd = (TypeDescriptor<K>) keyTypeDescriptor;
        TypeDescriptor<V> vtd = (TypeDescriptor<V>) valueTypeDescriptor;
        TypeDescriptor<Integer> tdi = new TypeDescriptorInteger();

        if (implentation == NodeImpl.fixedLength) {
            final JbNodeDefImpl.Initializator<K, V> init =
                    new JbNodeDefImpl.InitializatorFixedLength<K, V>();
            final JbNodeDef<K, V> jbNodeDef =
                    new JbNodeDefImpl<K, V>(l, ktd, vtd, tdi, init);
            n = new NodeFixedLength<K, V>(nodeId, isLeafNode, jbNodeDef);
        } else {
            final JbNodeDefImpl.Initializator<K, V> init =
                    new JbNodeDefImpl.InitializatorShort<K, V>();
            final JbNodeDef<K, V> jbNodeDef =
                    new JbNodeDefImpl<K, V>(l, ktd, vtd, tdi, init);
            n = new NodeShort<K, V>(nodeId, isLeafNode, jbNodeDef);
        }
        int counter = 0;
        
        for (Map.Entry<Object, Object> entry : keyValuePairs.entrySet()) {
            K key = (K) entry.getKey();
            V value = (V) entry.getValue();
            n.insertAtPosition(Wrapper.make(key, ktd), value, counter);
            counter++;
        }
        n.setLink(linkId);
        return n;
    }

}
