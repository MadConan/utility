package net.conan.file;

import net.conan.lambda.ExceptionWrapper;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
/**
 * EXPERIMENTAL
 *
 * @author Conan Dombroski ()
 */
public class DirectoryCombiner implements FileCombiner {

    private final boolean recurse;

    public DirectoryCombiner(){
        this(false);
    }

    public DirectoryCombiner(boolean recurseIntoSubDirectories){
        recurse = recurseIntoSubDirectories;
    }

    @Override
    public File combine(List<File> files, File target) {
        if(!target.exists() && !target.mkdirs()){
            throw new IllegalStateException("Can't create the directory: " + target.getAbsolutePath() +
            ".  Do you have permissions to create it?");
        }

        files.forEach(ExceptionWrapper.wrapConsumer(f -> {
            if(f.isDirectory()){
                Files.newDirectoryStream(f.toPath())
                      .forEach(ExceptionWrapper.wrapConsumer(path -> moveFile(path, target)));
            }else {
                moveFile(f.toPath(), target);
            }
        }));
        return target;
    }

    private void moveFile(Path path, File target) throws IOException{
        Path targetPath = Paths.get(target.getAbsolutePath() + File.separatorChar + path.getFileName().toString());
        Files.copy(path, targetPath, REPLACE_EXISTING);
    }
}
