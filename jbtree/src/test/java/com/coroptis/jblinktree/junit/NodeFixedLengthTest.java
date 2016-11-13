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

import com.coroptis.jblinktree.NodeBuilder;
import com.coroptis.jblinktree.NodeBuilder.NodeImpl;
import com.coroptis.jblinktree.NodeShort;
import com.coroptis.jblinktree.type.TypeDescriptorInteger;

/**
 * Junit test for {@link NodeShort}.
 *
 * @author jajir
 *
 */
public class NodeFixedLengthTest extends AbstractNodeTest {

    @Override
    protected NodeBuilder getNb() {
        return NodeBuilder.builder().setL(3)
                .setKeyTypeDescriptor(new TypeDescriptorInteger())
                .setValueTypeDescriptor(new TypeDescriptorInteger())
                .setImplementation(NodeImpl.fixedLength);
    }

}
