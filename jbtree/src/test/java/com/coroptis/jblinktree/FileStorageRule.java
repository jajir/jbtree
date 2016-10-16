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
import java.io.File;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.coroptis.jblinktree.junit.FileStorageWriteTest;
import com.coroptis.jblinktree.store.KeyIntFileStorage;
import com.coroptis.jblinktree.store.NodeFileStorage;
import com.coroptis.jblinktree.type.TypeDescriptor;
import com.coroptis.jblinktree.type.TypeDescriptorInteger;
import com.google.common.io.Files;

/**
 * Prepare file storage instance for test.
 *
 * @author jajir
 *
 */
public class FileStorageRule implements TestRule {

    private final Logger logger =
            LoggerFactory.getLogger(FileStorageWriteTest.class);

    private final static String FILE_NAME = "test.bin";

    private File tempDirectory;

    private JbTreeData<Integer, Integer> treeData;

    private JbNodeBuilder<Integer, Integer> nodeBuilder;

    private NodeFileStorage<Integer, Integer> fileStorage;

    private TypeDescriptor<Integer> intDescriptor;

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

    public File getTempFile() {
        return new File(
                tempDirectory.getAbsolutePath() + File.separator + FILE_NAME);
    }

    public JbNodeDef<Integer, Integer> getNodeDef() {
        return new JbNodeDefImpl<Integer, Integer>(2, intDescriptor,
                intDescriptor, intDescriptor);
    }

    private void setup() {
        tempDirectory = Files.createTempDir();
        logger.debug("templ file: " + tempDirectory.getAbsolutePath());
        intDescriptor = new TypeDescriptorInteger();
        treeData = new JbTreeDataImpl<Integer, Integer>(0, 5, intDescriptor,
                intDescriptor, intDescriptor);
        nodeBuilder = new JbNodeBuilderShort<Integer, Integer>(treeData);
        fileStorage = new KeyIntFileStorage<Integer>(getTempFile(),
                treeData.getNonLeafNodeDescriptor(), nodeBuilder);
    }

    public void tearDown() {
        intDescriptor = null;
        fileStorage = null;
        nodeBuilder = null;
        treeData = null;
        tempDirectory.delete();
        tempDirectory = null;
    }

    public NodeFileStorage<Integer, Integer> getFileStorage() {
        return fileStorage;
    }

    public TypeDescriptor<Integer> getIntDescriptor() {
        return intDescriptor;
    }

}
