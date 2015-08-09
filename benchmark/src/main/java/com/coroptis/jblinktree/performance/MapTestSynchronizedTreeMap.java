package com.coroptis.jblinktree.performance;

import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

public class MapTestSynchronizedTreeMap extends AbstractMapTest {

    @Override
    protected Map<Integer, Integer> initialize() {
	return Collections.synchronizedMap(new TreeMap<Integer, Integer>());
    }

    @Override
    protected String mapName() {
	return "synchronized_tree_map";
    }

}
