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

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import com.coroptis.jblinktree.type.TypeDescriptorInteger;

public abstract class AbstractNodeRule implements TestRule {

    protected final Integer l;

    protected TypeDescriptorInteger tdi;

    protected JbTreeData<Integer, Integer> treeData;

    public AbstractNodeRule(final Integer l) {
        this.l = l;
    }

    public abstract Node<Integer, Integer> makeNode(final Integer idNode,
            final byte[] field, final JbNodeDef<Integer, Integer> jbNodeDef);

    public abstract Node<Integer, Integer> makeNode(final Integer nodeId,
            final boolean isLeafNode,
            final JbNodeDef<Integer, Integer> jbNodeDef);

    @Override
    public Statement apply(final Statement base, Description description) {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                setup();
                base.evaluate();
                tearDown();
            }
        };
    }

    protected abstract void setup();

    protected abstract JbNodeDef<Integer, Integer> getNodeDef(Integer ll);

    private void tearDown() {
        tdi = null;
        treeData = null;
    }

    public TypeDescriptorInteger getTdi() {
        return tdi;
    }

    protected abstract byte[] convert(final Integer[] fieldInt);

    /**
     * Construct node and fill byte field.
     *
     * @param idNode
     *            required node id, node will be referred with this id.
     * @param fieldInt
     *            required Integer array representing node content.
     * @return created {@link NodeShort}
     */
    public Node<Integer, Integer> makeNodeFromIntegers(final Integer idNode,
            final Integer fieldInt[]) {
        return makeNode(idNode, convert(fieldInt),
                treeData.getLeafNodeDescriptor());
    }

    public Node<Integer, Integer> makeNodeFromIntegers(final Integer ll,
            final Integer idNode, final Integer fieldInt[]) {
        return makeNode(idNode, convert(fieldInt), getNodeDef(ll));
    }

    public JbTreeData<Integer, Integer> getTreeData() {
        return treeData;
    }

    public Integer getL() {
        return l;
    }

}
