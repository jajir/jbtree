package com.coroptis.jblinktree.performance;

import java.util.Map;

import com.coroptis.jblinktree.TreeBuilder;
import com.coroptis.jblinktree.type.Types;

public class MapTestJbTreeMap extends AbstractMapTest {

    @Override
    protected Map<Integer, Integer> initialize() {
	return TreeBuilder.builder().setL(2).setKeyType(Types.integer())
		.setValueType(Types.integer()).build();
    }

}
