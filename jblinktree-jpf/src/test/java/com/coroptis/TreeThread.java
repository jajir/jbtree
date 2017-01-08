package com.coroptis;

import java.util.Map;

import com.coroptis.jblinktree.util.JblinktreeException;

public class TreeThread extends Thread {

    private final Map<Integer, Integer> jbTree;

    private final int cycles;

    private final boolean insert;

    public TreeThread(final String name, final Map<Integer, Integer> jbTree, final int cycles,
	    final boolean insert) {
	super(name);
	this.jbTree = jbTree;
	this.cycles = cycles;
	this.insert = insert;
    }

    @Override
    public void run() {
	try {
	    for (int i = 0; i < cycles; i++) {
		if (insert) {
		    jbTree.put(i, i);
		} else {
		    jbTree.remove(i);
		}
	    }
	} catch (JblinktreeException e) {
	    e.printStackTrace();
	}
    }
}
