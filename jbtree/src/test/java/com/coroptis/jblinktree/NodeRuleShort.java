package com.coroptis.jblinktree;

import com.coroptis.jblinktree.type.TypeDescriptorInteger;

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

public class NodeRuleShort extends AbstractNodeRule {

    JbNodeDefImpl.Initializator<Integer, Integer> init;

    public NodeRuleShort(final Integer l) {
        super(l);
    }

    @Override
    public Node<Integer, Integer> makeNode(Integer idNode, byte[] field,
            JbNodeDef<Integer, Integer> jbNodeDef) {
        return new NodeShort<Integer, Integer>(idNode, field, jbNodeDef);
    }

    @Override
    public Node<Integer, Integer> makeNode(Integer nodeId, boolean isLeafNode,
            JbNodeDef<Integer, Integer> jbNodeDef) {
        return new NodeShort<Integer, Integer>(nodeId, isLeafNode, jbNodeDef);
    }

    @Override
    protected void setup() {
        tdi = new TypeDescriptorInteger();
        init = new JbNodeDefImpl.InitializatorShort<Integer, Integer>();
        final JbNodeDef<Integer, Integer> leafNodeDescriptor =
                new JbNodeDefImpl<Integer, Integer>(l, tdi, tdi, tdi, init);
        final JbNodeDef<Integer, Integer> nonLeafNodeDescriptor =
                new JbNodeDefImpl<Integer, Integer>(l, tdi, tdi, tdi, init);
        treeData = new JbTreeDataImpl<Integer, Integer>(0, l,
                leafNodeDescriptor, nonLeafNodeDescriptor);
    }

    @Override
    protected JbNodeDef<Integer, Integer> getNodeDef(Integer ll) {
        return new JbNodeDefImpl<Integer, Integer>(ll, tdi, tdi, tdi, init);
    }
    
    @Override
    protected byte[] convert(Integer[] fieldInt) {
        byte fieldByte[] = new byte[fieldInt.length * 4 + 1];
        for (int i = 0; i < fieldInt.length; i++) {
            tdi.save(fieldByte, i * 4 + 1, fieldInt[i]);
        }
        return fieldByte;
    }

}
