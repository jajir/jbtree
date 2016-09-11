package com.coroptis.jblinktree.performance.cache;

import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OperationsPerInvocation;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;

import com.coroptis.jblinktree.JbNodeBuilder;
import com.coroptis.jblinktree.JbNodeDef;
import com.coroptis.jblinktree.JbNodeDefImpl;
import com.coroptis.jblinktree.Node;
import com.coroptis.jblinktree.NodeImpl;
import com.coroptis.jblinktree.store.CacheListener;
import com.coroptis.jblinktree.store.CacheLru;
import com.coroptis.jblinktree.type.TypeDescriptorInteger;
import com.coroptis.jblinktree.type.Wrapper;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
public class CacheLruBenchmark {

    private CacheLru<Integer, Integer> cache;

    private int cx = 0;

    private final Node<Integer, Integer> node;

    public CacheLruBenchmark() {
        TypeDescriptorInteger tdi = new TypeDescriptorInteger();
        JbNodeDef<Integer, Integer> nd =
                new JbNodeDefImpl<Integer, Integer>(12, tdi, tdi, tdi);
        node = new NodeImpl<Integer, Integer>(54, false, nd);
        JbNodeBuilder<Integer, Integer> nodeBuilder =
                new JbNodeBuilder<Integer, Integer>() {

                    @Override
                    public Node<Integer, Integer> makeEmptyLeafNode(
                            Integer idNode) {
                        // TODO Auto-generated method stub
                        return null;
                    }

                    @Override
                    public Node<Integer, Integer> makeEmptyNonLeafNode(
                            Integer idNode) {
                        // TODO Auto-generated method stub
                        return null;
                    }

                    @Override
                    public <T> Node<Integer, T> makeNode(Integer idNode,
                            byte[] field) {
                        return (Node<Integer, T>) node;
                    }

                    @Override
                    public <T> Node<Integer, T> makeNode(Integer idNode,
                            byte[] field, JbNodeDef<Integer, T> jbNodeDef) {
                        // TODO Auto-generated method stub
                        return null;
                    }

                    @Override
                    public Node<Integer, Integer> makeNonLeafNode(
                            Integer idNode, Integer value1,
                            Wrapper<Integer> key1, Integer value2,
                            Wrapper<Integer> key2) {
                        // TODO Auto-generated method stub
                        return null;
                    }
                };
        CacheListener<Integer, Integer> cacheListener =
                new CacheListener<Integer, Integer>() {

                    @Override
                    public void onUnload(Node<Integer, Integer> node,
                            boolean wasChanged) {
                        /**
                         * Do nothing.
                         */
                    }

                    @Override
                    public Node<Integer, Integer> onLoad(Integer nodeId) {
                        return node;
                    }
                };
        cache = new CacheLru<Integer, Integer>(nodeBuilder, 1000 * 1000 * 1000,
                cacheListener);
    }

    @Benchmark
    @OperationsPerInvocation(1)
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    public void simpleTest() {
        cache.get(cx);
        cx++;
    }

    @Setup
    public void setup() {
        System.out.println("setup counter: " + cx);
    }

    @TearDown
    public void tearDown() {
        System.out.println("tear down counter: " + cx);
    }

}
