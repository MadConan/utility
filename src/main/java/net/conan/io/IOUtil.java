package net.conan.io;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.logging.Level;
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
     * @see #getContent(Path)
     * @see #getFileContent(File)
     * @param fileName Name of file to read.
     * @return List of Strings that is the file content.
     */
    public static List<String> getFileContent(String fileName){
        return getContent(Paths.get(fileName));
    }

    /**
     * Convenience method to eliminate the need for a try-catch around {@link Files#readAllLines(Path)}.
     *
     * @see #getFileContent(String)
     * @see #getContent(Path)
     * @param f File to read.
     * @return List of Strings that is the file content.
     */
    public static List<String> getFileContent(File f){
        return getContent(f.toPath());
    }

    /**
     * Convenience method to eliminate the need for a try-catch around {@link Files#readAllLines(Path)}.
     *
     * @see #getFileContent(File)
     * @see #getFileContent(String)
     * @param path Path to file
     * @return List of Strings that is the file content.
     */
    public static List<String> getContent(Path path){
        try{
            return Files.readAllLines(path);
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
    public static long writeFileToStream(File f, OutputStream out){
        long byteCount = 0;
        try(InputStream in = new BufferedInputStream(new FileInputStream(f))) {
            byteCount = IOUtil.readWrite(in,out);
        }catch (FileNotFoundException e){
            throw new IllegalStateException("File not found: " + f,e);
        }catch (IOException e){
            // This should only happen on a InputStream.close()
            // Just dump the stack and continue.
            LOG.log(Level.WARNING,"Exception during InputStream.close(): " + e.getMessage(),e);
        }
        return byteCount;
    }

    public static long writeStreamToFile(File f, InputStream in){
        long byteCount = 0;
        try(OutputStream out = new BufferedOutputStream(new FileOutputStream(f),BUFFER_SIZE)){
            byteCount = IOUtil.readWrite(in,out);
        }catch (FileNotFoundException e){
            throw new IllegalStateException("File not found: " + f,e);
        }catch (IOException e){
            // This should only happen on a InputStream.close()
            // Just dump the stack and continue.
            LOG.log(Level.WARNING,"Exception during InputStream.close(): " + e.getMessage(),e);
        }
        return byteCount;
    }
}
