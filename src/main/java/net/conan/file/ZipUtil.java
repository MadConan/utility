package net.conan.file;

import net.conan.io.IOUtil;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * @author Conan Dombroski (dombroco)
 */
public class ZipUtil {
    public static String getEntryAsString(String entryName, File archive) throws IOException{
        ZipFile zipFile = new ZipFile(archive);
        ZipEntry entry = zipFile.getEntry(entryName);
        String returnValue;
        try(InputStream in = zipFile.getInputStream(entry);
            ByteArrayOutputStream out = new ByteArrayOutputStream((int)entry.getSize())){

            IOUtil.readWrite(in,out);
            returnValue = out.toString();
        }
        return returnValue;
    }
}
