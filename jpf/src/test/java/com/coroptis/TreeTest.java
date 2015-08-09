package com.coroptis;

import java.util.Map;

import com.coroptis.jblinktree.TreeBuilder;
import com.coroptis.jblinktree.type.Types;

public class TreeTest {

    private final static int NUMBER_OF_CONCURRENT_INSERTS = 5;

    /**
     * @param args
     */
    public static void main(String[] args) throws Exception {
	test4();
    }

    private static void test4() throws Exception {
	Map<Integer, Integer> tree = TreeBuilder.builder().setL(2).setKeyType(Types.integer())
		.setValueType(Types.integer()).build();
	TreeThread tt1 = new TreeThread("tt1", tree, NUMBER_OF_CONCURRENT_INSERTS, true);
	TreeThread tt2 = new TreeThread("tt2", tree, NUMBER_OF_CONCURRENT_INSERTS, false);
	tt1.start();
	tt2.start();

	tt1.join();
	tt2.join();
	System.out.println("Test is done");
    }

}