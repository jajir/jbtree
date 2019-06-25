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
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.nio.charset.Charset;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.coroptis.jblinktree.JbNodeDef;
import com.coroptis.jblinktree.JbNodeDefImpl;
import com.coroptis.jblinktree.JbTreeData;
import com.coroptis.jblinktree.JbTreeDataImpl;
import com.coroptis.jblinktree.Node;
import com.coroptis.jblinktree.NodeShort;
import com.coroptis.jblinktree.NodeUtilRule;
import com.coroptis.jblinktree.type.TypeDescriptor;
import com.coroptis.jblinktree.type.TypeDescriptorInteger;
import com.coroptis.jblinktree.type.TypeDescriptorString;
import com.coroptis.jblinktree.type.Wrapper;

/**
 * Test node with storing strings.
 *
 * @author jajir
 *
 */
public class NodeStringTest {

    private Logger logger = LoggerFactory.getLogger(NodeStringTest.class);

    private Node<String, String> node;

    private TypeDescriptor<String> sd;

    private TypeDescriptor<Integer> intDescriptor;

    @Rule
    public NodeUtilRule nodeUtil = new NodeUtilRule();

    @Test
    public void test_insert_leaf_one() throws Exception {
        node.insertAtPosition(Wrapper.make("ahoj", sd), "lidi", 0);

        verifyNode(node, new String[][] { { "ahoj", "lidi" } }, true,
                Node.EMPTY_INT);
    }

    @Test
    public void test_insert_leaf_second_bigger() throws Exception {
        node.insertAtPosition(Wrapper.make("ahoj", sd), "lidi", 0);
        node.insertAtPosition(Wrapper.make("flying", sd), "pig", 1);

        verifyNode(node,
                new String[][] { { "ahoj", "lidi" }, { "flying", "pig" } },
                true, Node.EMPTY_INT);
    }

    @Test
    public void test_insert_leaf_second_smaller() throws Exception {
        node.insertAtPosition(Wrapper.make("aaa taxi", sd), "is fast", 0);
        node.insertAtPosition(Wrapper.make("ahoj", sd), "lidi", 1);

        verifyNode(node, new String[][] { { "aaa taxi", "is fast" },
                { "ahoj", "lidi" } }, true, Node.EMPTY_INT);
    }

    /**
     * Verify that node have following basic attributes:
     * <ul>
     * <li>number of keys is correct</li>
     * <li>values of keys are correct</li>
     * <li>values stored in node are correct</li>
     * <li>next key is correct</li>
     * <li>distinguish between leaf and non-leaf node is correct</li>
     * </ul>
     *
     * @param pairs
     *            required key value pairs stored in node
     * @param isLeafNode
     *            required info if it's leaf node
     * @param expectedNodeLink
     *            required value of expectect link
     */
    private void verifyNode(final Node<String, String> n,
            final String[][] pairs, final boolean isLeafNode,
            final Integer expectedNodeLink) {
        logger.debug(n.toString());

        assertEquals("Expected number of key is invalid", pairs.length,
                n.getKeyCount());
        assertEquals("isLeafNode value is invalid", isLeafNode, n.isLeafNode());
        List<String> keys = nodeUtil.getKeys(n);
        int cx = 0;
        for (String[] pair : pairs) {
            final String key = pair[0];
            final String value = pair[1];
            assertTrue("keys should contains key " + pair[0],
                    keys.contains(pair[0]));
            assertEquals(key, n.getKey(cx));
            if (isLeafNode) {
                assertEquals(value, n.getValue(cx));
            } else {
                fail("it's not leaf node.");
            }
            cx++;
        }
        assertEquals("Node link is invalid", expectedNodeLink, n.getLink());
        if (pairs.length > 0) {
            final String expectedMaxKey = pairs[pairs.length - 1][0];
            assertEquals("Max key value is invalid", expectedMaxKey,
                    n.getMaxKey().getValue());
        }
    }

    @Before
    public void setUp() throws Exception {
        sd = new TypeDescriptorString(10, Charset.forName("UTF-8"));
        intDescriptor = new TypeDescriptorInteger();
        final JbNodeDefImpl.Initializator<String, String> init1 = new JbNodeDefImpl.InitializatorShort<String, String>();
        final JbNodeDef<String, String> leafNodeDescriptor = new JbNodeDefImpl<String, String>(
                5, sd, sd, intDescriptor, init1);
        
        final JbNodeDefImpl.Initializator<String, Integer> init2 = new JbNodeDefImpl.InitializatorShort<String, Integer>();
        final JbNodeDef<String, Integer> nonLeafNodeDescriptor = new JbNodeDefImpl<String, Integer>(
                5, sd, intDescriptor, intDescriptor, init2);

        JbTreeData<String, String> td = new JbTreeDataImpl<String, String>(0, 5,
                leafNodeDescriptor, nonLeafNodeDescriptor);
        node = new NodeShort<String, String>(0, true,
                td.getLeafNodeDescriptor());
    }

    @After
    public void tearDown() throws Exception {
        node = null;
        sd = null;
        intDescriptor = null;
    }
}
