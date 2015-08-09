package com.coroptis.jblinktree.performance;

import java.util.concurrent.ConcurrentHashMap;

import org.openjdk.jmh.annotations.Setup;

public class MapTestConcurrentHashMap extends AbstractMapTest {

    @Setup
    public void setUp() {
	tree = new ConcurrentHashMap<Integer, Integer>();
	super.warmUp();
    }

}
