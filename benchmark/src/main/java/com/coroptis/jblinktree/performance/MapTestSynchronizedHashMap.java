package com.coroptis.jblinktree.performance;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class MapTestSynchronizedHashMap extends AbstractMapTest {

    @Override
    protected Map<Integer, Integer> initialize() {
	return Collections.synchronizedMap(new HashMap<Integer, Integer>());
    }

    @Override
    protected String mapName() {
	return "synchronized_hash_map";
    }
    
}
