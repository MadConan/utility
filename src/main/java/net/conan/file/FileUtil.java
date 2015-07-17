package net.conan.file;

import net.conan.lambda.ExceptionWrapper;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * @author Conan Dombroski (dombroco)
 */
public class FileUtil {
    public static void deleteRecursively(Path path){
        if(Files.isDirectory(path)){
            ExceptionWrapper.wrapSupplier(() -> Files.list(path)).get().forEach(FileUtil::deleteRecursively);
        }
        ExceptionWrapper.wrapConsumer(Files::delete).accept(path);
    }

    public static long countAll(File f){
        if(!f.isDirectory()){
            return 0;
        }
        File[] files = f.listFiles();

        if(files == null || files.length < 1){
            return 0;
        }

        long sum = files.length;
        for(File file : files){
            sum+=countAll(file);
        }
        return sum;
    }
}
