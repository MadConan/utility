package net.conan.io;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.logging.Logger;

/**
 * Convenience class for common IO operations.  Eliminates boilerplate try-catch
 * blocks.
 *
 * @author Conan Dombroski
 */
public final class IOUtil {
    private static final int BUFFER_SIZE = 1 << 10;
    private static final Logger LOG = Logger.getLogger("net.conan.io.IOUtil");

    /**
     * Read all content from in and write it to out.
     * @throws IllegalStateException if either of the underlying streams throws an
     * IOException
     * @param in InputStream to read from
     * @param out OutputStream to write to.
     * @return total number of bytes read/written
     *
     */
    public static long readWrite(InputStream in, OutputStream out){
        int bytesRead = 0;
        long totalRead = 0;
        byte[] bytes = new byte[BUFFER_SIZE];
        try {
            while ((bytesRead = in.read(bytes)) != -1) {
                out.write(bytes,0,bytesRead);
                totalRead+=bytesRead;
            }
        }catch (IOException e){
            throw new IllegalStateException("Failed to transfer data from in to out.  Current bytesRead="+bytesRead,e);
        }
        return totalRead;
    }

    /**
     * Convenience method to eliminate the need for a try-catch around {@link Files#readAllLines(Path)}.
     *
     * @param fileName Name of file to read.
     * @return List of Strings that is the file content.
     */
    public static List<String> getFileContent(String fileName){
        try{
            return Files.readAllLines(Paths.get(fileName));
        }catch (IOException e){
            throw new IllegalStateException(e);
        }
    }

    /**
     * Write all content of the File argument to the OutputStream argument
     *
     * @param f File to read
     * @param out OutputStream to write file content to
     * @return total number of bytes read/written
     */
    public static long writeFile(File f, OutputStream out){
        long byteCount = 0;
        try(InputStream in = new BufferedInputStream(new FileInputStream(f))) {
            byteCount = IOUtil.readWrite(in,out);
        }catch (FileNotFoundException e){
            throw new IllegalStateException("Failed to create or close input stream for " + f,e);
        }catch (IOException e){
            // This should only happen on a InputStream.close()
            // Just dump the stack and continue.
            LOG.warning("Exception during InputStream.close(): " + e.getMessage());
        }
        return byteCount;
    }
}
