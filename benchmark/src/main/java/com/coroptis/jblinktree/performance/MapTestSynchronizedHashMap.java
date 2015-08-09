package com.coroptis.jblinktree.performance;

import java.util.Collections;
import java.util.HashMap;

import org.openjdk.jmh.annotations.Setup;

public class MapTestSynchronizedHashMap extends AbstractMapTest {

    @Setup
    public void setUp() {
	tree = Collections.synchronizedMap(new HashMap<Integer, Integer>());
	super.warmUp();
    }

}
