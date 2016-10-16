package com.coroptis.jblinktree.junit;

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

import org.junit.Rule;

import com.coroptis.jblinktree.AbstractNodeRule;
import com.coroptis.jblinktree.Node;
import com.coroptis.jblinktree.NodeFixedLength;
import com.coroptis.jblinktree.NodeRuleFixedLength;
import com.coroptis.jblinktree.NodeShort;

/**
 * Junit test for {@link NodeShort}.
 *
 * @author jajir
 *
 */
public class NodeFixedLengthTest extends AbstractNodeTest {

    @Rule
    public AbstractNodeRule nr = new NodeRuleFixedLength(2);

    @Override
    protected Node<Integer, Integer> createNode() {
        return new NodeFixedLength<Integer, Integer>(0, true,
                nr.getTreeData().getLeafNodeDescriptor());
    }

    @Override
    protected AbstractNodeRule getNr() {
        return nr;
    }

}
