package com.coroptis.jblinktree.index;

public class ByteToolImpl implements ByteTool {
    
    @Override
    public int sameBytes(final byte[] b1, final byte[] b2) {
        final int min = Math.min(b1.length, b2.length);
        for (int i = 0; i < min; i++) {
            if (b1[i] != b2[i]) {
                return i;
            }
        }
        return min;
    }
    
}
