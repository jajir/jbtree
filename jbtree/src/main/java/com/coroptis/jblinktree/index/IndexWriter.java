package com.coroptis.jblinktree.index;

import java.io.OutputStream;

public class IndexWriter<K, V> {

    private final ByteTool byteTool;

    private final OutputStream outputStream;

    private final PairDescriptor<K, V> pairDescriptor;

    private byte[] previousKey;

    public IndexWriter(final ByteTool byteTool, final OutputStream outputStream,
            final PairDescriptor<K, V> pairDescriptor) {
        this.byteTool = byteTool;
        this.outputStream = outputStream;
        this.pairDescriptor = pairDescriptor;
    }

    public void add(final Pair<K, V> pair) {
        if (previousKey == null) {
            pairDescriptor.getKeyTypeDescriptor().getMaxLength();
        } else {
            byte[] b = pairDescriptor.getKeyTypeDescriptor()
                    .getBytes(pair.getKey());
            int i = byteTool.sameBytes(previousKey, b);
            byte[] out = new byte[1 + b.length - i
                    + pairDescriptor.getValueTypeDescriptor().getMaxLength()];
            out[0] = (byte) i;
            System.arraycopy(b, i, out, 1, b.length - i);
        }
    }

    private void writeKey(K key) {

    }

}
