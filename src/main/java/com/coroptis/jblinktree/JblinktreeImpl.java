package com.coroptis.jblinktree;

import com.google.inject.Inject;

public class JblinktreeImpl implements Jblinktree {

	private final NodeStore nodeStore;

	@Inject
	public JblinktreeImpl(final NodeStore nodeStore) {
		this.nodeStore = nodeStore;
	}

	@Override
	public void put(Integer key, Integer value) {
		nodeStore.hashCode();
	}

	@Override
	public Integer get(Integer key) {
		// TODO Auto-generated method stub
		return null;
	}

}
