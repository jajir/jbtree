package com.coroptis.jblinktree;

import com.google.inject.AbstractModule;

public class JbtreeModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(NodeStore.class).to(NodeStoreImpl.class);
		bind(Jblinktree.class).to(JblinktreeImpl.class);
	}

}
