package com.coroptis.jblinktree;

import java.util.ArrayList;
import java.util.List;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

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

}
