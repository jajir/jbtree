package com.coroptis.jblinktree.index;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ByteToolTest {

    private ByteTool byteTool;

    private void test(final String s1, final String s2,
            final int assertLength) {
        byte[] b1 = s1.getBytes();
        byte[] b2 = s2.getBytes();

        assertEquals(
                "Strings '" + s1 + "' and '" + s2 + "' have length",
                assertLength, byteTool.sameBytes(b1, b2));
    }

    @Test
    public void test_basic() throws Exception {
        test("ahoj", "ahojKarle", 4);
    }

    @Test
    public void test_boundaries() throws Exception {
        test("", "ahoj", 0);
        test("ahoj", "", 0);
    }

    @Test
    public void test_same() throws Exception {
        
        test("ahoj", "ahoj", 4);
    }

    @Before
    public void setup() {
        byteTool = new ByteToolImpl();
    }

    @After
    public void tearDown() {
        byteTool = null;
    }

}
