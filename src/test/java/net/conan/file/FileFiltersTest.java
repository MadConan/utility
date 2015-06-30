package net.conan.file;

import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

/**
 * @author Conan Dombroski (dombroco)
 */
public class FileFiltersTest {

    File testFile;

    @Before
    public void setup() throws Exception {
        testFile = new File("./target");
    }

    @Test
    public void testUsingRegexString() throws Exception {
        String[] names = testFile.list(FileFilters.usingRegex(".*?ass.*?"));
        TestCase.assertEquals(2, names.length);
    }

    @Test
    public void testWithRegex() throws Exception {
        File[] files = testFile.listFiles(FileFilters.withRegex(".*?ass.*?"));
        TestCase.assertEquals(2, files.length);
    }

    @Test
    public void testLiteral() throws Exception {
        String[] names = testFile.list(FileFilters.literal("classes"));
        TestCase.assertEquals(1,names.length);
    }

    @Test
    public void testLiteralFilter() throws Exception {
        File[] files = testFile.listFiles(FileFilters.literalFilter("classes"));
        TestCase.assertEquals(1,files.length);
    }
}