package net.conan.file;

import net.conan.io.IOUtil;
import net.conan.lambda.ExceptionWrapper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

/**
 * @author Conan Dombroski (dombroco)
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

            String sourceName = bean.source().getName();
            int lastIndexOfSlash = sourceName.lastIndexOf('/');
            if(lastIndexOfSlash == sourceName.length() - 1){
                int nextToLastIndex = sourceName.lastIndexOf('/',sourceName.length() - 1);
                if(nextToLastIndex != -1){
                    sourceName = sourceName.substring(nextToLastIndex + 1, sourceName.length()-1);
                }
            }else if(lastIndexOfSlash != -1){
                sourceName = sourceName.substring(lastIndexOfSlash + 1);
            }

            String newName;
            String entryName = bean.entry().getName();
            if(entryName.charAt(entryName.length() - 1) == '/'){
                newName = entryName.substring(0,entryName.length()-1) + sourceName + '/';
            }else{
                newName = entryName + sourceName;
            }

            ZipEntry entry = new ZipEntry(newName);
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
        final Set<String> entryNames = new TreeSet<>();

        try(ZipOutputStream out = new ZipOutputStream(new FileOutputStream(target))){
            files.stream().forEach(f -> addFile(f, out, entryNames));
        }catch (IOException e){
            // Only on close event, so ignore it
            e.printStackTrace();
        }catch (RuntimeException e){
            target.delete();
            throw e;
        }
        return target;
    }

    private void addFile(File f, final ZipOutputStream out, final Set<String> entryNames){
        ZipFile source = getZipFileFromFile(f);
        Consumer<ZipEntry> consumer = ExceptionWrapper.wrapConsumer(e -> addEntryContent(out, source, e, entryNames));
        source.stream().forEach(consumer);
    }

    private void addEntryContent(final ZipOutputStream out, final ZipFile source, final ZipEntry entry, final Set<String> entryNames) throws IOException {
        if(!entryNames.add(entry.getName())){
            LOGGER.warning(entry.getName() + " has already been added. Applying strategy: " + strategy);
            strategy.apply(new CombinerBean(out,entry,source),this);
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
