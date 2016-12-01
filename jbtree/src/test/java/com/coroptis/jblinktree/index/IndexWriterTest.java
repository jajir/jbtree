package com.coroptis.jblinktree.index;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayOutputStream;
import java.nio.charset.Charset;

import org.apache.log4j.Logger;
import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.coroptis.jblinktree.type.TypeDescriptorString;

public class IndexWriterTest {

    private final Logger logger = Logger.getLogger(IndexWriterTest.class);

    private IndexWriter<String, String> indexWriter;

    private ByteTool byteTool;

    private ByteArrayOutputStream baos;

    @Test
    public void test_simple() throws Exception {
        EasyMock.replay(byteTool);
        indexWriter.add(Pair.make("hello", "world"));

        EasyMock.verify(byteTool);
        byte b[] = baos.toByteArray();
        assertEquals(0, b[0]);
        assertEquals(104, b[1]);
        logger.debug(baos.toString());
    }

    @Test
    public void test_twoValues() throws Exception {
        EasyMock.expect(byteTool.sameBytes(EasyMock.aryEq("hello".getBytes()),
                EasyMock.aryEq("hello2".getBytes()))).andReturn(5);
        EasyMock.replay(byteTool);
        indexWriter.add(Pair.make("hello", "world"));
        indexWriter.add(Pair.make("hello2", "svete"));

        EasyMock.verify(byteTool);
        byte b[] = baos.toByteArray();
        assertEquals(0, b[0]);
        assertEquals(104, b[1]);
        logger.debug(baos.toString());
    }

    @Before
    public void setup() {
        final TypeDescriptorString tds = new TypeDescriptorString(16,
                Charset.forName("ISO-8859-1"));
        final PairDescriptor<String, String> pd = new PairDescriptor<String, String>(
                tds, tds);
        byteTool = EasyMock.createMock(ByteTool.class);
        baos = new ByteArrayOutputStream();
        indexWriter = new IndexWriter<String, String>(byteTool, baos, pd);
    }

    @After
    public void tearDown() {
        indexWriter = null;
        byteTool = null;
        baos = null;
    }

}
