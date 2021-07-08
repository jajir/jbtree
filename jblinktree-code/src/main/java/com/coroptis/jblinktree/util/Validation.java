package com.coroptis.jblinktree.util;

/**
 * This methods are stoled from guava. Main reason is to avoid runtime
 * dependency to guava library.
 * 
 * @author jan
 *
 */
public class Validation {

    public static void checkState(boolean expression) {
        if (!expression) {
            throw new IllegalStateException();
        }
    }

    public static void checkArgument(boolean expression) {
        if (!expression) {
            throw new IllegalArgumentException();
        }
    }
}
