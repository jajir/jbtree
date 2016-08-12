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

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.coroptis.jblinktree.JblinktreeException;
import com.coroptis.jblinktree.store.ValueFileStorage;
import com.coroptis.jblinktree.store.ValueFileStorageImpl;
import com.coroptis.jblinktree.type.TypeDescriptor;
import com.coroptis.jblinktree.type.TypeDescriptorString;
import com.google.common.io.Files;

/**
 * Simple tests for {@link ValueFileStorage}.
 *
 * @author jiroutj
 *
 */
public class ValueFileStorageTest {

    private ValueFileStorage<String> valueStorage;

    private File tempDirectory;

    @Test
    public void test_read_and_write() throws Exception {
        valueStorage.store(0, "Ahoj lidi!");

        assertEquals("Ahoj lidi!", valueStorage.load(0));
    }

    @Test(expected = JblinktreeException.class)
    public void test_read_invalid_valueId() throws Exception {
        valueStorage.load(10);
    }

    @Before
    public void setup() throws IOException {
        tempDirectory = Files.createTempDir();
        TypeDescriptor<String> td = new TypeDescriptorString(20,
                Charset.forName("ISO-8859-1"));
        valueStorage = new ValueFileStorageImpl<String>(new File(
                tempDirectory.getAbsolutePath() + File.separator + "value.str"),
                td);
    }

    @After
    public void tearDown() {
        tempDirectory = null;
        valueStorage.close();
        valueStorage = null;
    }
}
