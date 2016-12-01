package com.coroptis.jblinktree.index;

import java.io.IOException;
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
        byte[] bKey = pairDescriptor.getKeyTypeDescriptor()
                .getRawBytes(pair.getKey());
        byte[] bValue = pairDescriptor.getValueTypeDescriptor()
                .getBytes(pair.getValue());
        if (previousKey == null) {
            write(bKey, bValue, 0);
            previousKey = bKey;
        } else {
            write(bKey, bValue, byteTool.sameBytes(previousKey, bKey));
        }
    }

    private void write(final byte[] bKey, final byte[] bValue, final int i) {
        byte[] out = new byte[1 + bKey.length - i
                + pairDescriptor.getValueTypeDescriptor().getMaxLength()];
        out[0] = (byte) i;
        System.arraycopy(bKey, i, out, 1, bKey.length - i);
        System.arraycopy(bValue, 0, out, 1 + bKey.length - i, bValue.length);
        try {
            outputStream.write(out);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
