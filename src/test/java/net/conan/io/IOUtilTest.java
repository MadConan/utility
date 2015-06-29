package net.conan.io;

import junit.framework.TestCase;
import org.junit.After;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.List;

/**
 * @author Conan Dombroski (dombroco)
 */
public class IOUtilTest {

    File testFile;

    @After
    public void tearDown() throws Exception{
        if(testFile != null){
            testFile.delete();
        }
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

    @Test
    public void testGetFileContent() throws Exception {
        testFile = new File("test.test");

        Files.write(testFile.toPath(), "This is a test".getBytes(), StandardOpenOption.CREATE);
        List<String> contents = IOUtil.getFileContent("test.test");
        TestCase.assertEquals("This is a test", contents.get(0));
    }

    @Test
    public void testWriteFile() throws Exception {
        OutputStream out = new ByteArrayOutputStream(1024);
        testFile = new File("testWriteFile.txt");

        Files.write(testFile.toPath(), "This is a test".getBytes(), StandardOpenOption.CREATE);
        IOUtil.writeFile(testFile, out);
        TestCase.assertEquals("This is a test", out.toString());
    }
}