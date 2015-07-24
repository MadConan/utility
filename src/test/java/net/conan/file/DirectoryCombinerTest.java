package net.conan.file;

import junit.framework.TestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * @author Conan Dombroski (dombroco)
 */
@SuppressWarnings("all")
public class DirectoryCombinerTest {

    final List<String> strings = Arrays.asList(
          "./target/DirectoryCombinerTest",
          "./target/DirectoryCombinerTest/dir1",
          "./target/DirectoryCombinerTest/dir2",
          "./target/DirectoryCombinerTest/dir2/subDir1",
          "./target/DirectoryCombinerTest/dir1/file.txt",
          "./target/DirectoryCombinerTest/dir2/subDir1/file.txt",
          "./target/DirectoryCombinerTest/dir2/bar.bin"
    );

    File target;

    @Before
    public void setup() throws Exception {
        strings.forEach(s -> {
            File f = new File(s);
            if(f.exists()){
                return;
            }
            if (s.lastIndexOf('.') > 1) {
                createTempFile(new File(s));
            } else {
                f.mkdirs();
            }
        });
    }

    @After
    public void tearDown() throws Exception {
        if(target != null) {
            FileUtil.deleteRecursively(target.toPath());
        }
        FileUtil.deleteRecursively(new File(strings.get(0)).toPath());
    }

    @Test
    public void testCombine() throws Exception {
        target = new File("./target/DirectoryCombinerTest-target");
        FileCombiner combiner = new DirectoryCombiner();
        File f = combiner.combine(Arrays.asList(new File(strings.get(1)),new File(strings.get(2))), target);
        TestCase.assertEquals("File wasn't a directory",true,f.isDirectory());
        Stream<Path> pathStream = StreamSupport.stream(Files.newDirectoryStream(f.toPath()).spliterator(),false);
        long dirCount = pathStream.filter(p -> Files.isDirectory(p)).count();
        TestCase.assertEquals("Number of directories was incorrect",1,dirCount);
        pathStream = StreamSupport.stream(Files.newDirectoryStream(f.toPath()).spliterator(),false);
        long filesCount = pathStream.filter(p -> !Files.isDirectory(p)).count();
        TestCase.assertEquals("Number of files was incorrect", 2, filesCount);
    }

    @Test
    public void testCombineRecursively() throws Exception {
        target = new File("./target/DCT-recursive-target");
        FileCombiner combiner = new DirectoryCombiner(true);
        File f = combiner.combine(Arrays.asList(new File(strings.get(1)),new File(strings.get(2))), target);
        TestCase.assertEquals("File wasn't a directory",true,f.isDirectory());
        Stream<Path> pathStream = StreamSupport.stream(Files.newDirectoryStream(f.toPath()).spliterator(),false);
        long dirCount = pathStream.filter(p -> Files.isDirectory(p)).count();
        TestCase.assertEquals("Number of directories was incorrect",1,dirCount);
        pathStream = StreamSupport.stream(Files.newDirectoryStream(f.toPath()).spliterator(),false);
        long filesCount = pathStream.filter(p -> !Files.isDirectory(p)).count();
        TestCase.assertEquals("Number of files was incorrect",2,filesCount);

    }

    private void createTempFile(File f) {
        f.getParentFile().mkdirs();
        try(PrintWriter out = new PrintWriter(new FileWriter(f))){
            Random r = new Random();
            out.println("This is a temp file for testing\nTextFileCombiner.java\nRandom number = " + r.nextInt() +
                  "\nRandom long=" + r.nextLong());
        }catch (Exception e){
            throw new IllegalStateException(e);
        }
    }
}