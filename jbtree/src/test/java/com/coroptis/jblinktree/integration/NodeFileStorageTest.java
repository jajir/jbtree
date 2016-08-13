package com.coroptis.jblinktree.integration;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.coroptis.jblinktree.JbTreeData;
import com.coroptis.jblinktree.JbTreeDataImpl;
import com.coroptis.jblinktree.JblinktreeException;
import com.coroptis.jblinktree.Node;
import com.coroptis.jblinktree.NodeBuilder;
import com.coroptis.jblinktree.NodeBuilderImpl;
import com.coroptis.jblinktree.store.NodeFileStorageImpl;
import com.coroptis.jblinktree.store.ValueFileStorage;
import com.coroptis.jblinktree.store.ValueFileStorageImpl;
import com.coroptis.jblinktree.type.TypeDescriptor;
import com.coroptis.jblinktree.type.TypeDescriptorInteger;
import com.coroptis.jblinktree.type.TypeDescriptorString;
import com.google.common.io.Files;

public class NodeFileStorageTest {

    private NodeFileStorageImpl<String, String> storage;

    private File tempDirectory;

    private NodeBuilder<String, String> nodeBuilder;

    @Test
    public void test_read_and_write() throws Exception {
        for(int i=0;i<10;i++){
            storage.store(createNode(i));
        }
        
        for(int i=0;i<10;i++){
            Node<String, String> n = storage.load(i);
            System.out.println(n);
        }
    }

    @Test(expected = JblinktreeException.class)
    public void test_read_invalid_valueId() throws Exception {
    }

    Node<String, String> createNode(final Integer i) {
        Node<String, String> n = nodeBuilder.makeEmptyLeafNode(i);
        n.insert("Anoj lidi!" + i, "Anoj lidi!" + i);
        return n;
    }

    @Before
    public void setup() throws IOException {
        tempDirectory = Files.createTempDir();
        TypeDescriptor<String> tdKey = new TypeDescriptorString(10,
                Charset.forName("ISO-8859-1"));
        TypeDescriptor<String> tdValue = new TypeDescriptorString(7,
                Charset.forName("ISO-8859-1"));
        TypeDescriptor<Integer> tdLink = new TypeDescriptorInteger();
        JbTreeData<String, String> treeData = new JbTreeDataImpl<String, String>(
                0, 2, tdKey, tdValue, tdLink);
        nodeBuilder = new NodeBuilderImpl<String, String>(treeData);
        storage = new NodeFileStorageImpl<String, String>(treeData, nodeBuilder,
                tempDirectory.getAbsolutePath());
    }

    @After
    public void tearDown() {
        tempDirectory = null;
        storage.close();
        storage = null;
    }

}
