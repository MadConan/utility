package net.conan.file;

import net.conan.io.IOUtil;
import net.conan.lambda.ExceptionWrapper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

/**
 * <p>Combines archives in the standard ZLIB format into a single archive.  For example, if
 * there are two archives, a1.zip and a2.zip, and a1.zip has the entries</p>
 * <pre>    file1.txt
 *  foo/file2.txt</pre>
 * <p>and a2.zip has the entries</p>
 * <pre>    file3.txt
 *  foo/file4.txt</pre>
 * <p>will result in an archive with the entries</p>
 * <pre>    file1.txt
 *  file3.txt
 *  foo/file2.txt
 *  foo/file4.txt</pre>
 *
 * <p>In the case of collisions, three possible strategies are implemented as an enum:
 *  <ul><li><code>FAIL:</code>  Stop processing (throwing IllegalStateException) if a collision occurs.</li>
 *  <li><code>USE_FIRST:</code>  Any duplicates are ignored.  Only the first entry found with the name is used.</li>
 *  <li><code>RENAME_AND_ADD:</code>  An attempt is made to rename the entry based on the archive name and
 *                     entry name.</li></ul>
 *  These strategies only apply to non-directories.  Collisions for directory entries are ignored.
 *  It is assumed that combining two archives with the same directory entries would be equivalent
 *  to having created a single archive from those directories in the first place.
 * </p>
 * <p>No attempt is made to determine version or age of any entry, so the behavior described here may not fit with
 * all use cases.</p>
 *
 * @see java.util.zip.ZipFile
 * @see File
 * @author Conan Dombroski 
 */
public class ZipFileCombiner implements FileCombiner {

    public enum CollisionStrategy {
        FAIL((bean, combiner) -> {
            throw new IllegalStateException("Collision detected. Entry " + bean.entry() +
                  " exists.  Current source: " + bean.source());
        }),
        USE_FIRST((bean, combiner) -> {}),
        RENAME_AND_ADD((bean, combiner) -> {
            if(bean.entry().isDirectory()){
                return;
            }

            String[] sourceNameParts = bean.source().getName().split("/");
            String sourceName = sourceNameParts[sourceNameParts.length-1];
            String[] entryNameParts = bean.entry().getName().split("/");
            String entryName = entryNameParts[entryNameParts.length-1];
            entryNameParts[entryNameParts.length-1] = sourceName + entryName;
            String newEntryName = Arrays.asList(entryNameParts).stream().collect(Collectors.joining("/"));

            // Have to manually "clone" the entry.  This sucks.
            ZipEntry entry = new ZipEntry(newEntryName);
            entry.setTime(bean.entry().getTime());
            entry.setComment(bean.entry().getComment());
            entry.setCompressedSize(bean.entry().getCompressedSize());
            entry.setCrc(bean.entry().getCrc());
            entry.setCreationTime(bean.entry().getCreationTime());
            entry.setMethod(bean.entry().getMethod());
            entry.setExtra(bean.entry().getExtra());
            entry.setLastAccessTime(bean.entry().getLastAccessTime());
            entry.setLastModifiedTime(bean.entry().getLastModifiedTime());
            try (InputStream in = bean.source().getInputStream(bean.entry())){
                combiner.copyEntryFromSourceToTarget(in,entry,bean.zipOutputStream());
            } catch (IOException e) {
                throw new IllegalStateException(e);
            }
        });

        BiConsumer<CombinerBean,ZipFileCombiner> biConsumer;

        CollisionStrategy(BiConsumer<CombinerBean,ZipFileCombiner> consumer){
            biConsumer = consumer;
        }

        void apply(CombinerBean bean, ZipFileCombiner combiner){
            biConsumer.accept(bean,combiner);
        }
    }

    private static final Logger LOGGER = Logger.getLogger(ZipFileCombiner.class.getCanonicalName());

    private final CollisionStrategy strategy;

    public ZipFileCombiner(){
        this(CollisionStrategy.RENAME_AND_ADD);
    }

    public ZipFileCombiner(CollisionStrategy cs){
        strategy = cs;
    }

    @Override
    public File combine(List<File> files, File target) {
        // This Set is used to detect duplicate entries.
        final Set<String> entryNames = new TreeSet<>();

        try(ZipOutputStream out = new ZipOutputStream(new FileOutputStream(target))){
            files.stream().forEach(f -> addFile(f, out, entryNames));
        }catch (IOException e){
            // Only on close event, so ignore it
            e.printStackTrace();
        }
        return target;
    }

    private void addFile(File f, final ZipOutputStream out, final Set<String> entryNames){
        ZipFile source = getZipFileFromFile(f);
        // Get this:  I had to create the 'consumer' variable external from the 'forEach(..)' call
        // because it wouldn't compile when I inlined it, but it DOES compile this way.  Go figure.
        Consumer<ZipEntry> consumer = ExceptionWrapper.wrapConsumer(e -> addEntryContent(out, source, e, entryNames));
        source.stream().forEach(consumer);
    }

    private void addEntryContent(final ZipOutputStream out, final ZipFile source, final ZipEntry entry, final Set<String> entryNames) throws IOException {
        if(!entryNames.add(entry.getName())){
            // Assuming duplicate directory entries across archives are OK, so skip if directory
            if(!entry.isDirectory()) {
                LOGGER.warning(entry.getName() + " has already been added. Applying strategy: " + strategy);
                strategy.apply(new CombinerBean(out, entry, source), this);
            }
        }else {
            try (InputStream in = source.getInputStream(entry)) {
                copyEntryFromSourceToTarget(in, entry, out);
            }
        }
    }

    private void copyEntryFromSourceToTarget(final InputStream in, final ZipEntry targetEntry, final ZipOutputStream out) throws IOException {
        out.putNextEntry(targetEntry);
        IOUtil.readWrite(in, out);
        out.closeEntry();
    }

    private ZipFile getZipFileFromFile(File f){
        return ExceptionWrapper.wrapSupplier(() -> new ZipFile(f)).get();
    }

    /*
    CombinerBean is used in the CollisionStrategy.  It only serves to clean up code such that long arg lists
    aren't necessary.
     */
    private static class CombinerBean{
        private final ZipEntry e;
        private final ZipFile src;
        private final ZipOutputStream out;

        public CombinerBean(ZipOutputStream zo, ZipEntry ze, ZipFile source){
            e = ze; src = source; out = zo;
        }

        public ZipEntry entry(){ return e; }
        public ZipFile  source(){ return src; }
        public ZipOutputStream zipOutputStream(){ return out; }
    }
}
