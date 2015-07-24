package net.conan.file;

import junit.framework.TestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * @author Conan Dombroski ()
 */
public class TextFileCombinerTest {

    @Before
    public void setUp() throws Exception {

    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void testCombine() throws Exception {
        long totalLength = 0;

        List<File> files = new ArrayList<>();
        for(int i=0; i<10; i++){
            File f = File.createTempFile("tempFile" + i, "txt");
            files.add(f);
            createTempFile(f);
            totalLength+=f.length();
        }

        File target = new File("./target/testCombine-target.txt");
        FileCombiner fc = new TextFileCombiner();
        File result = fc.combine(files, target);
        TestCase.assertTrue("The target does not exist.", target.exists());
        TestCase.assertTrue("The result length was not correct.  TotalLength=" + totalLength
              + ", resultLength=" + result.length(), result.length() > totalLength);
        target.delete();
    }

    @Test
    public void testCombineWithCustomDelim() throws Exception {
        long totalLength = 0;

        List<File> files = new ArrayList<>();
        for(int i=0; i<10; i++){
            File f = File.createTempFile("tempFile" + i, "txt");
            files.add(f);
            createTempFile(f);
            totalLength+=f.length();
        }

        File target = new File("./target/testCombineWithCustomDelim-target.txt");
        String delim = "---------TEST";
        FileCombiner fc = new TextFileCombiner(delim);
        File result = fc.combine(files, target);
        TestCase.assertTrue("The target does not exist.", target.exists());
        TestCase.assertTrue("The result length was not correct.  TotalLength=" + totalLength
              + ", resultLength=" + result.length(), result.length() > totalLength);
        List<String> lines = Files.readAllLines(target.toPath());
        long count = lines.stream().filter(s -> s.equals(delim)).count();
        TestCase.assertEquals(10,count);
        target.delete();
    }

    @Test(expected = IllegalStateException.class)
    public void testFileNotFound() throws Exception {
        FileCombiner fc = new TextFileCombiner();
        File target = new File("./target/testFileNotFound-target.txt");
        try {
            fc.combine(Arrays.asList(new File("bogus.txt")), target);
        }finally {
            target.delete();
        }
    }

    private void createTempFile(File f) throws Exception{
        try(PrintWriter out = new PrintWriter(new FileWriter(f))){
            Random r = new Random();
            out.println("This is a temp file for testing\nTextFileCombiner.java\nRandom number = " + r.nextInt() +
            "\nRandom long=" + r.nextLong());
        }
    }
}