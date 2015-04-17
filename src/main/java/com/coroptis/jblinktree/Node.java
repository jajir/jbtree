package com.coroptis.jblinktree;

import java.util.concurrent.locks.Lock;

public interface Node {

	Lock getLock();

	boolean isLeafNode();

	Integer getCorrespondingNodeId(Integer key);

	Integer getValue(Integer key);
}
