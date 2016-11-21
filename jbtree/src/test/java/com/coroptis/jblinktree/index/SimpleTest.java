package com.coroptis.jblinktree.index;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.junit.Test;

import com.coroptis.jblinktree.util.JblinktreeException;

public class SimpleTest {
    
    @Test
    public void test_integration() throws Exception {
        File file = new File("");
        try {
            FileWriter fw = new FileWriter(file);
        } catch (IOException e) {
            throw new JblinktreeException(e.getMessage(), e);
        }

    }
}
