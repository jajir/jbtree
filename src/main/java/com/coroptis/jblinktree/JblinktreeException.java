package com.coroptis.jblinktree;

/**
 * Generic exception thrown by tree.
 * 
 * @author jajir
 *
 */
public class JblinktreeException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public JblinktreeException(String message, Throwable cause) {
		super(message, cause);
	}

	public JblinktreeException(String message) {
		super(message);
	}
}
