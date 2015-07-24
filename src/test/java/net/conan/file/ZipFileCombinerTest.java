package net.conan.file;

import junit.framework.TestCase;
import net.conan.file.ZipFileCombiner.CollisionStrategy;
import net.conan.io.IOUtil;
import net.conan.lambda.ExceptionWrapper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

/**
 * @author Conan Dombroski ()
 */
@SuppressWarnings("all")
public class ZipFileCombinerTest {

    File archiveOne = new File("./target/zipCombinerTest-one.zip");
    File archiveTwo = new File("./target/zipCombinerTest-two.zip");
    File archiveThree = new File("./target/zfct1.jar");
    File archiveFour = new File("./target/zfct2.jar");

    List<File> archiveOneFiles = Arrays.asList(new File("./target/temp1.txt"),new File("./target/temp2.txt"));
    List<File> archiveTwoFiles = Arrays.asList(new File("./target/temp3.txt"),new File("./target/temp4.txt"));

    @Before
    public void setUp() throws Exception {

        archiveOneFiles.forEach(ExceptionWrapper.wrapConsumer(this::createTempFile));
        archiveTwoFiles.forEach(ExceptionWrapper.wrapConsumer(this::createTempFile));

        createJarArchive(archiveThree, "zfcTestArchive2.jar");
        createJarArchive(archiveFour, "zfcTestArchive.jar");

        createArchive(archiveOne, archiveOneFiles);
        createArchive(archiveTwo, archiveTwoFiles);
    }

    @After
    public void tearDown() throws Exception{
        archiveOne.delete();
        archiveTwo.delete();
        archiveThree.delete();
        archiveFour.delete();
        archiveOneFiles.forEach(File::delete);
        archiveTwoFiles.forEach(File::delete);
    }

    @Test
    public void testCombine() throws Exception {
        File target = new File("./target/testCombine-target.zip");
        try {
            FileCombiner combiner = new ZipFileCombiner();
            File f = combiner.combine(Arrays.asList(archiveOne, archiveTwo), target);
            TestCase.assertTrue(f.length() > 0);
            ZipFile file = new ZipFile(f);
            ZipEntry entry = file.getEntry("temp1.txt");
            TestCase.assertNotNull("No entry found for 'temp1.txt'", entry);
            TestCase.assertTrue(entry.getSize() > 0);
        }finally {
            target.delete();
        }
    }

    @Test
    public void testRenameAndAddStrategy() throws Exception {
        File target = new File("./target/testRenameAndAddStrategy-target.zip");
        try {
            FileCombiner combiner = new ZipFileCombiner();
            File f = combiner.combine(Arrays.asList(archiveThree, archiveFour), target);
            TestCase.assertTrue("File was too small: " + f.length(), f.length() > 1 << 5);
            ZipFile zipFile = new ZipFile(f);
            final int[] count = {0};
            zipFile.stream().forEach(entry -> {
                if (entry.getName().matches("META-INF/.*?MANIFEST.*?")) {
                    count[0]++;
                }
            });
            TestCase.assertEquals(2, count[0]);
        }finally {
            target.delete();
        }
    }

    @Test
    public void testUseFirstStrategy() throws Exception {
        File target = new File("./target/testUseFirstStrategy-target.zip");
        try {
            FileCombiner combiner = new ZipFileCombiner(CollisionStrategy.USE_FIRST);
            File f = combiner.combine(Arrays.asList(archiveThree, archiveFour), target);
            TestCase.assertTrue("File was too small: " + f.length(), f.length() > 1 << 5);
            ZipFile zipFile = new ZipFile(f);
            final int[] count = {0};
            zipFile.stream().forEach(entry -> {
                if(entry.getName().matches("META-INF/.*?MANIFEST.*?")){
                    count[0]++;
                }
            });
            TestCase.assertEquals(1, count[0]);
        }finally {
            target.delete();
        }
    }

    @Test(expected = IllegalStateException.class)
    public void testFailStrategy() throws Exception {
        File target = new File("./target/testFailStrategy-target.zip");
        try {
            FileCombiner combiner = new ZipFileCombiner(CollisionStrategy.FAIL);
            File f = combiner.combine(Arrays.asList(archiveThree, archiveFour), target);
            TestCase.assertTrue("File was too small: " + f.length(), f.length() > 1 << 5);
            ZipFile zipFile = new ZipFile(f);
            final int[] count = {0};
            zipFile.stream().forEach(entry -> {
                if(entry.getName().matches("META-INF/.*?MANIFEST.*?")){
                    count[0]++;
                }
            });
            TestCase.assertEquals(0, count[0]);
        }finally {
            target.delete();
        }
    }

    @Test(expected = IllegalStateException.class)
    public void testFileNotAZipFile() throws Exception {

        File f1 = new File("./target/testFileNotAZipFile.txt");
        File f2 = new File("./target/testFileNotZip-target.zip");
        try {
            createTempFile(f1);
            FileCombiner fc = new ZipFileCombiner();
            fc.combine(Arrays.asList(f1), f2);
        }finally {
            f1.delete();
            f2.delete();
        }
    }

    private void createTempFile(File f) throws Exception{
        try(PrintWriter out = new PrintWriter(new FileWriter(f))){
            out.println("This is a temp file for testing\nZipFileCombiner.java");
        }
    }

    private void createArchive(File archive, List<File> files) throws Exception{
        try(ZipOutputStream out = new ZipOutputStream(new FileOutputStream(archive))){
            files.forEach(ExceptionWrapper.wrapConsumer(this::createTempFile));
            files.forEach(ExceptionWrapper.wrapConsumer(f -> this.addFileToArchive(f, out)));
        }
    }

    private void addFileToArchive(File f, ZipOutputStream out) throws Exception{
        try(InputStream in = new BufferedInputStream(new FileInputStream(f))){
            ZipEntry entry = new ZipEntry(f.getName());
            out.putNextEntry(entry);
            IOUtil.readWrite(in, out);
        }
    }

    private void createJarArchive(File target, String resource) throws Exception{
        try(InputStream in =
                  Thread.currentThread()
                        .getContextClassLoader()
                        .getResourceAsStream(resource);
            OutputStream out = new FileOutputStream(target)){
            IOUtil.readWrite(in,out);
        }
    }

    // THE FOLLOWING FIELD AND METHOD EXIST AS EXAMPLE CODE ONLY
    static final int BUFFER = 2048;
    private void zip (String argv[]) {
        try {
            BufferedInputStream origin = null;
            FileOutputStream dest = new
                  FileOutputStream("c:\\zip\\myfigs.zip");
            ZipOutputStream out = new ZipOutputStream(new
                  BufferedOutputStream(dest));

            byte data[] = new byte[BUFFER];

            File f = new File(".");
            String files[] = f.list();

            for (int i=0; i<files.length; i++) {
                System.out.println("Adding: "+files[i]);
                FileInputStream fi = new
                      FileInputStream(files[i]);
                origin = new
                      BufferedInputStream(fi, BUFFER);
                ZipEntry entry = new ZipEntry(files[i]);
                out.putNextEntry(entry);
                int count;
                while((count = origin.read(data, 0,
                      BUFFER)) != -1) {
                    out.write(data, 0, count);
                }
                origin.close();
            }
            out.close();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
    // END EXAMPLE CODE
}