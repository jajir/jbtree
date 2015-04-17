package com.coroptis.jblinktree;

import static org.junit.Assert.*;

import org.junit.Test;

import junit.framework.TestCase;

public class TreeTest extends TestCase {

	@Test
	public void testBasic() throws Exception {
		
		NodeStore nodeStore=new NodeStoreImpl();
		TreeManager manager = new TreeManager(nodeStore);
		
		assertNull(manager.getValue(31));
	}
	
}
