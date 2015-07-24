package net.conan.file;

import net.conan.lambda.ExceptionWrapper;

import java.io.File;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.StandardOpenOption;
import java.util.List;

/**
 * Combines text files into a single file.
 *
 * @author Conan Dombroski (dombroco)
 */
public class TextFileCombiner implements FileCombiner {

    private static final String LINE_FEED = System.getProperty("line.separator");

    private final String contentSeparator;

    /**
     * Create a new TextFileCombiner using no delimter.
     *
     * @see #TextFileCombiner(String)
     */
    public TextFileCombiner(){
        this("");
    }

    /**
     * Create new TextFileCombiner with the given delimiter.
     *
     * @param contentDelimLine Delimiter used to separate individual file content within
     *                         the combined file.
     */
    public TextFileCombiner(String contentDelimLine){
        contentSeparator = contentDelimLine + LINE_FEED;
    }

    /**
     * Combine all files into one.  This is a very simplistic implementation as it does not
     * attempt to verify any content.  This method assumes all Files within the list are
     * valid text files and also assumes the system's default encoding. Mixed encodings will
     * have unpredictable results in the file merged File.
     *
     * @see File
     * @param files List of Files to merge
     * @param target merge destination
     * @return The target.
     */
    @Override
    public File combine(List<File> files, File target) {
        try{
            final ByteBuffer separatorByteBuffer = ByteBuffer.wrap(contentSeparator.getBytes());
            final FileChannel out = FileChannel.open(target.toPath(),StandardOpenOption.CREATE,StandardOpenOption.WRITE);
            files.stream().forEach(
                  ExceptionWrapper.wrapConsumer(
                        f -> {
                            FileChannel in = FileChannel.open(f.toPath(), StandardOpenOption.READ);
                            in.transferTo(0, f.length(), out);
                            out.write(separatorByteBuffer);
                            separatorByteBuffer.rewind();
                            in.close();
                        }));
        }catch (Exception e){
            throw new IllegalStateException(e);
        }
        return target;
    }
}
