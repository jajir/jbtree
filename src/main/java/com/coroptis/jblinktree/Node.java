package com.coroptis.jblinktree;

import java.util.concurrent.locks.Lock;

public interface Node {
	
	Lock getLock();
	
	void setLock(Lock lock);
	
}
