package net.conan.file;

import junit.framework.TestCase;
import org.junit.Test;

import java.io.File;
import java.util.regex.Pattern;

/**
 * @author Conan Dombroski ()
 */
public class BasicFileFilterTest {

    @Test
    public void testAcceptListStringPlainString() throws Exception {
        File f = new File("./target/");
        String[] names =  f.list(BasicFileFilter.asFilenameFilter("classes"));
        TestCase.assertEquals(true,names.length == 1);
    }

    @Test
    public void testAcceptListStringREString() throws Exception {
        File f = new File("./target/");
        String[] names =  f.list(BasicFileFilter.asFilenameFilter(Pattern.compile(".+?asses")));
        TestCase.assertEquals(true,names.length == 2);
    }

    @Test
    public void testAcceptListFilesREString() throws Exception {
        File f = new File("./target/");
        File[] names =  f.listFiles(BasicFileFilter.asFileFilter(Pattern.compile(".+?asses")));
        TestCase.assertEquals(true,names.length == 2);
    }

    @Test
    public void testAcceptListFilesPlainString() throws Exception {
        File f = new File("./target/");
        File[] names =  f.listFiles(BasicFileFilter.asFileFilter("classes"));
        TestCase.assertEquals(true,names.length == 1);
    }

    @Test
    public void testEquals() throws Exception {
        BasicFileFilter fileFilter1 = new BasicFileFilter("test");
        BasicFileFilter fileFilter2 = new BasicFileFilter("test");

        TestCase.assertEquals(true,fileFilter1.equals(fileFilter2));

        fileFilter2 = new BasicFileFilter("\\w+st");

        TestCase.assertFalse(fileFilter1.equals(fileFilter2));

    }
}