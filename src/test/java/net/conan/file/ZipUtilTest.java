package net.conan.file;

import junit.framework.TestCase;
import net.conan.io.IOUtil;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author Conan Dombroski (dombroco)
 */
public class ZipUtilTest {

    @Before
    public void setup() throws Exception{
        try(InputStream in =
                  Thread.currentThread()
                        .getContextClassLoader()
                        .getResourceAsStream("textFiles.zip");
            OutputStream out = new FileOutputStream("textFiles.zip")){
            IOUtil.readWrite(in, out);
        }
    }

    @Test
    public void testGetEntryAsString() throws Exception {
        String content = ZipUtil.getEntryAsString("ldapinfo.txt",new File("textFiles.zip"));
        TestCase.assertEquals(content + " is not the right length", 37,content.length());
    }
}