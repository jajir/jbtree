package com.coroptis.jblinktree.performance;

import org.openjdk.jmh.annotations.Setup;

import com.coroptis.jblinktree.TreeBuilder;
import com.coroptis.jblinktree.type.Types;

public class MapTestJbTreeMap extends AbstractMapTest {

    @Setup
    public void setUp() {
	tree = TreeBuilder.builder().setL(2).setKeyType(Types.integer())
		.setValueType(Types.integer()).build();
	super.warmUp();
    }

}
