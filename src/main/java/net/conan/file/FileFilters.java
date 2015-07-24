package net.conan.file;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.util.regex.Pattern;

/**
 * <p>Convenience class for creating a {@link FilenameFilter} or
 * {@link FileFilter} using a String or {@link Pattern}.</p>
 *
 * <p>All methods do full matching on the {@link File#getName() file name}</p>
 * @author Conan Dombroski ()
 */
public class FileFilters {

    public static FilenameFilter usingRegex(String regex){
        return FileFilters.usingRegex(Pattern.compile(regex));
    }

    public static FilenameFilter usingRegex(final Pattern pattern){
        return (dir, name) -> pattern.matcher(name).matches();
    }

    public static FileFilter withRegex(String regex){
        return FileFilters.withRegex(Pattern.compile(regex));
    }

    public static FileFilter withRegex(final Pattern pattern){
        return file -> pattern.matcher(file.getName()).matches();
    }

    public static FilenameFilter literal(String s){
        return (dir, name) -> name.equals(s);
    }

    public static FileFilter literalFilter(String s){
        return file -> file.getName().equals(s);
    }
}
