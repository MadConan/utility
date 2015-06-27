package net.conan.io;

import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author Conan Dombroski (dombroco)
 */
public class IOUtilTest {

    @Before
    public void setUp() throws Exception {

    }

    @Test
    public void testReadWrite() throws Exception {
        ByteArrayInputStream in = new ByteArrayInputStream("This is a test".getBytes());
        ByteArrayOutputStream out = new ByteArrayOutputStream(500);
        IOUtil.readWrite(in,out);
        TestCase.assertEquals("This is a test",out.toString());
    }

    @Test(expected = IllegalStateException.class)
    public void testReadWriteFailOnRead() throws Exception{
        InputStream in = new InputStream() {
            @Override
            public int read() throws IOException {
                throw new IOException("TEST");
            }
        };
        ByteArrayOutputStream out = new ByteArrayOutputStream(500);
        IOUtil.readWrite(in,out);
    }

    @Test(expected = IllegalStateException.class)
    public void testReadWriteFailOnWrite() throws Exception {
        InputStream in = new ByteArrayInputStream("test".getBytes());
        OutputStream out = new OutputStream() {
            @Override
            public void write(int b) throws IOException {
                throw new IOException("TEST");
            }
        };
        IOUtil.readWrite(in,out);
    }
}