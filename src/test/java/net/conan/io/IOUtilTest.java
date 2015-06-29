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
 * @author Conan Dombroski
 */
@SuppressWarnings("all")
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
        ByteArrayInputStream in = new ByteArrayInputStream(testString().getBytes());
        ByteArrayOutputStream out = new ByteArrayOutputStream(500);
        long count = IOUtil.readWrite(in,out);
        TestCase.assertEquals(testString(),out.toString());
        TestCase.assertEquals(testString().getBytes().length,count);
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
        InputStream in = new ByteArrayInputStream(testString().getBytes());
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
        Files.write(testFile.toPath(),testString().getBytes(), StandardOpenOption.CREATE);
        List<String> contents = IOUtil.getFileContent("test.test");
        TestCase.assertEquals(testString(), contents.get(0));
    }

    @Test
    public void testWriteFile() throws Exception {
        OutputStream out = new ByteArrayOutputStream(1024);
        testFile = new File("testWriteFile.txt");

        Files.write(testFile.toPath(),testString().getBytes(), StandardOpenOption.CREATE);
        long count = IOUtil.writeFileToStream(testFile, out);
        TestCase.assertEquals(testString(), out.toString());
        TestCase.assertEquals(testString().getBytes().length,count);
    }

    @Test
    public void testWriteStreamToFile() throws Exception{
        testFile = new File("testWriteStreamToFile.test");
        InputStream in = new ByteArrayInputStream(testString().getBytes());
        long count = IOUtil.writeStreamToFile(testFile,in);
        List<String> content = Files.readAllLines(testFile.toPath());
        TestCase.assertEquals(testString(),content.get(0));
        TestCase.assertEquals(testString().getBytes().length,count);
    }

    @Test(expected = IllegalStateException.class)
    public void testGetFileContentBadFileName() throws Exception {
        IOUtil.getFileContent("great koogaly moogaly");
    }

    @Test(expected = IllegalStateException.class)
    public void testGetFileContentBadFile() throws Exception {
        IOUtil.getFileContent(new File("no file 999 "));
    }

    @Test
    public void testWriteFileToStreamFailOnClose() throws Exception {
        testFile = new File("testwftsfoc.test");

        Files.write(testFile.toPath(), testString().getBytes(), StandardOpenOption.CREATE);
        OutputStream out = new OutputStream() {
            @Override
            public void write(int b) throws IOException {
            }
            @Override
            public void close() throws IOException{
                throw new IOException("TEST");
            }
        };
        long count = IOUtil.writeFileToStream(testFile,out);
        TestCase.assertEquals(testString().getBytes().length,count);
    }

    @Test
    public void testWriteStreamToFileFailOnClose() throws Exception {
        testFile = new File("testwstffoc.test");
        InputStream in = new InputStream(){
            int readCount = 0;

            @Override
            public int read() throws IOException {
                if(readCount >= 99)
                    return -1;
                readCount++;
                return 'x';
            }

            @Override
            public void close() throws IOException {
                throw new IOException("TEST");
            }
        };
        long count = IOUtil.writeStreamToFile(testFile,in);
        TestCase.assertEquals(99,count);
    }

    private String testString(){
        return "This is a test";
    }

}