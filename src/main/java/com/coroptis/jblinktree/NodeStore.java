package com.coroptis.jblinktree;

public interface NodeStore {

	void lockNode(Integer nodeId);
	
	void unlockNode(Integer nodeId);
	
	Node get(Integer nodeId);
	
	void writeNode(Node node);
	
	void put(Integer idNode, Node node);
	
	int size();
	
}
