package net.conan.file;

import java.io.File;
import java.util.List;

/**
 * @author Conan Dombroski (dombroco)
 */
public interface FileCombiner {

    File combine(List<File> files, File target);
}
