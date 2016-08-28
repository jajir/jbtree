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

import java.util.ArrayList;
import java.util.List;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;
import org.omg.PortableServer.POAPackage.NoServant;

public class NodeUtilRule implements TestRule {

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

}
