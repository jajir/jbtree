package com.coroptis.jblinktree.junit;

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
