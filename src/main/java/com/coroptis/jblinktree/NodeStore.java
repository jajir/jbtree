package com.coroptis.jblinktree;

public interface NodeStore {

	void lockNode(Integer nodeId);
	
	void unlockNode(Integer nodeId);
	
	Node readNode(Integer nodeId);
	
	void writeNode(Node node);
	
}
