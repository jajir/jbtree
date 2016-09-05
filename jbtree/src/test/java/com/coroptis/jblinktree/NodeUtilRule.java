package com.coroptis.jblinktree;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

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

import java.util.ArrayList;
import java.util.List;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;
import org.omg.PortableServer.POAPackage.NoServant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.coroptis.jblinktree.junit.NodeTest;

public class NodeUtilRule implements TestRule {

    private Logger logger = LoggerFactory.getLogger(NodeUtilRule.class);

    private JbNodeService nodeService = new JbNodeServiceImpl();

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

    private void setup() {
    }

    private void tearDown() {
    }

    public <K, V> List<K> getKeys(final Node<K, V> node) {
        final List<K> out = new ArrayList<K>();
        for (int i = 0; i < node.getKeyCount(); i++) {
            out.add(node.getKey(i));
        }
        return out;
    }

    public <K> List<Integer> getNodeIds(final Node<K, Integer> node) {
        return nodeService.getNodeIds(node);
    }

    public <K, S> void writeTo(Node<K, S> node, StringBuilder buff,
            String intendation) {
        nodeService.writeTo(node, buff, intendation);
    }

    public <K, V> V getValueByKey(Node<K, V> node, K key) {
        return (V) nodeService.getValueByKey(node, key);
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
     * @param n
     *            required verified node
     * @param pairs
     *            required key value pairs stored in node
     * @param isLeafNode
     *            required info if it's leaf node
     * @param expectedNodeLink
     *            required value of expectect link
     */
    public void verifyNode(final Node<Integer, Integer> n,
            final Integer[][] pairs, final boolean isLeafNode,
            final Integer expectedNodeLink, final Integer expectedNodeId) {
        logger.debug(n.toString());

        assertEquals("Expected number of key is invalid", pairs.length,
                n.getKeyCount());
        assertEquals("isLeafNode value is invalid", isLeafNode, n.isLeafNode());
        assertEquals("nodeId is invalid", expectedNodeId, n.getId());
        List<Integer> keys = getKeys(n);
        for (Integer[] pair : pairs) {
            final Integer key = pair[0];
            final Integer value = pair[1];
            assertTrue("keys should contains key " + pair[0],
                    keys.contains(pair[0]));
            assertEquals(value, getValueByKey(n, key));
        }
        assertEquals("Node link is invalid", expectedNodeLink, n.getLink());
        if (pairs.length > 0) {
            final Integer expectedMaxKey = pairs[pairs.length - 1][0];
            assertEquals("Max key value is invalid", expectedMaxKey,
                    n.getMaxKey().getValue());
        }
    }

}
