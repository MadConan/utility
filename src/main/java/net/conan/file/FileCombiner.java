package net.conan.file;

import java.io.File;
import java.util.List;

/**
 * <p>A FileCombiner should attempt to combine {@link File} instances into a new, single
 * File.</p>
 * <p>The behavior will be both implementation and platform specific, as the underlying
 * operating system has the ultimate say in how this can be accomplished.</p>
 *
 * @author Conan Dombroski
 */
public interface FileCombiner {

    /**
     * <p>Combine all files in the list into a {@link File} that is the target argument.
     * Implementations may require that the target already exists.  In many cases, it is
     * expected that the returned value be the target, but it is not required.</p>
     *
     * @param files List of Files to merge
     * @param target merge destination
     * @return The result of the merge.  May or may not be the same instance as target.
     */
    File combine(List<File> files, File target);
}
