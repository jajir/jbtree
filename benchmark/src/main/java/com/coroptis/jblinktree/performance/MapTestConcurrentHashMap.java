package com.coroptis.jblinktree.performance;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MapTestConcurrentHashMap extends AbstractMapTest {

    @Override
    protected Map<Integer, Integer> initialize() {
	return new ConcurrentHashMap<Integer, Integer>();
    }

}
