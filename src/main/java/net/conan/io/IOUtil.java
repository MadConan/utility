package net.conan.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author Conan Dombroski (dombroco)
 */
public final class IOUtil {
    private static final int BUFFER_SIZE = 1 << 10;

    public static void readWrite(InputStream in, OutputStream out){
        int bytesRead=0;
        byte[] bytes = new byte[BUFFER_SIZE];
        try {
            while ((bytesRead = in.read(bytes)) != -1) {
                out.write(bytes,0,bytesRead);
            }
        }catch (IOException e){
            throw new IllegalStateException("Failed to transfer data from in to out.  Current bytesRead="+bytesRead,e);
        }
    }


}
