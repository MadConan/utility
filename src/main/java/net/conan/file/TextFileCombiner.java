package net.conan.file;

import net.conan.lambda.ExceptionWrapper;

import java.io.File;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.StandardOpenOption;
import java.util.List;

/**
 * @author Conan Dombroski (dombroco)
 */
public class TextFileCombiner implements FileCombiner {

    private static final String LINE_FEED = System.getProperty("line.separator");

    private final String contentSeparator;

    public TextFileCombiner(){
        this("");
    }

    public TextFileCombiner(String contentDelimLine){
        contentSeparator = contentDelimLine + LINE_FEED;
    }

    @Override
    public File combine(List<File> files, File target) {
        try{
            final byte[] contentSeparationBytes = contentSeparator.getBytes();
            final FileChannel out = FileChannel.open(target.toPath(),StandardOpenOption.CREATE,StandardOpenOption.WRITE);
            files.stream().forEach(
                  ExceptionWrapper.wrapConsumer(
                        f -> {
                            FileChannel.open(f.toPath(), StandardOpenOption.READ)
                                  .transferTo(0, f.length(), out);
                            out.write(ByteBuffer.wrap(contentSeparationBytes));
                        }));
        }catch (Exception e){
            throw new IllegalStateException(e);
        }
        return target;
    }
}
