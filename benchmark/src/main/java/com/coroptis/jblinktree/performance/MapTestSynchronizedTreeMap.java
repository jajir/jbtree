package com.coroptis.jblinktree.performance;

import java.util.Collections;
import java.util.TreeMap;

import org.openjdk.jmh.annotations.Setup;

public class MapTestSynchronizedTreeMap extends AbstractMapTest {
    
    @Setup
    public void setUp() {
	tree = Collections.synchronizedMap(new TreeMap<Integer, Integer>());
	super.warmUp();
    }

}
