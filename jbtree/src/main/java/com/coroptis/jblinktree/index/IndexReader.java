package com.coroptis.jblinktree.index;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.OutputStream;
import java.io.RandomAccessFile;

import com.coroptis.jblinktree.util.JblinktreeException;

public class IndexReader<K, V> {

    private final RandomAccessFile raf;

    public IndexReader(final File file,
            final PairDescriptor<K, V> pairDescriptor) {
        try {
            raf = new RandomAccessFile(file, "rw");
        } catch (FileNotFoundException e) {
            throw new JblinktreeException(e.getMessage(), e);
        }
    }
    
    
    
}
