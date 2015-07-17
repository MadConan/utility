package net.conan.file;

import junit.framework.TestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * @author Conan Dombroski (dombroco)
 */
public class FileUtilTest {
    final List<String> strings = Arrays.asList(
          "./target/FileUtilTest",
          "./target/FileUtilTest/dir1",
          "./target/FileUtilTest/dir2",
          "./target/FileUtilTest/dir2/subDir1",
          "./target/FileUtilTest/dir1/file.txt",
          "./target/FileUtilTest/dir2/subDir1/file.txt",
          "./target/FileUtilTest/dir2/bar.bin"
    );

    File target;

    @Before
    public void setup() throws Exception {

    }

    @After
    public void tearDown() throws Exception {
        if(target != null){
            FileUtil.deleteRecursively(target.toPath());
        }
    }

    @Test
    public void testDeleteRecursively() throws Exception {
        List<String> newList = createStructure("testDeleteRecursively");

        target = new File(newList.get(0));
        FileUtil.deleteRecursively(target.toPath());
        TestCase.assertTrue("target still exists", !target.exists());
        target = null;
    }

    @Test
    public void testCountAll() throws Exception {
        List<String> newList = createStructure("testCountAll");
        target = new File(newList.get(0));
        long val = FileUtil.countAll(target);
        TestCase.assertEquals("Wrong number: " + val,newList.size()-1,val);
    }

    private void createTempFile(File f) {
        f.getParentFile().mkdirs();
        try(PrintWriter out = new PrintWriter(new FileWriter(f))){
            Random r = new Random();
            out.println("This is a temp file for testing\nFileUtilTest.java\nRandom number = " + r.nextInt() +
                  "\nRandom long=" + r.nextLong());
        }catch (Exception e){
            throw new IllegalStateException(e);
        }
    }

    private List<String> createStructure(String sub){
        List<String> newList = strings.stream()
              .map(s -> s.replace("FileUtilTest", sub))
              .collect(Collectors.toList());
        newList.forEach(s -> {
            File f = new File(s);
            if (f.exists()) {
                return;
            }
            if (s.lastIndexOf('.') > 1) {
                createTempFile(new File(s));
            } else {
                f.mkdirs();
            }
        });
        return newList;
    }
}