package net.conan.file;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.util.regex.Pattern;

/**
 * Simple filtering on file names.
 * <ul>
 *     <li>Only the simple name is checked against the filter argument.
 *     No other path information is used.</li>
 *     <li>Full matching with {@link Pattern} is used.  ie: </li>
 * </ul>
 * @author Conan Dombroski (dombroco)
 */
public class BasicFileFilter implements FileFilter, FilenameFilter{
    private final Pattern pattern;

    private final int hash;

    BasicFileFilter(String filter){
        this(Pattern.compile(Pattern.quote(filter)));
    }

    public BasicFileFilter(Pattern pattern){
        this.pattern = pattern;
        hash = getClass().hashCode() * pattern.hashCode() * 31;
    }
    public static FileFilter asFileFilter(String filter){
        return new BasicFileFilter(filter);
    }

    public static FileFilter asFileFilter(Pattern pattern){
        return new BasicFileFilter(pattern);
    }

    public static FilenameFilter asFilenameFilter(String filter){
        return new BasicFileFilter(filter);
    }

    public static FilenameFilter asFilenameFilter(Pattern pattern){
        return new BasicFileFilter(pattern);
    }

    @Override
    public boolean accept(File pathname) {
        return pattern.matcher(pathname.getName()).matches();
    }

    @Override
    public boolean accept(File dir, String name) {
        return pattern.matcher(name).matches();
    }

    @Override
    public int hashCode(){
        return hash;
    }

    @Override
    public boolean equals(Object o){
        return o != null &&
              getClass() == o.getClass() &&
              pattern.pattern().equals(((BasicFileFilter) o).pattern.pattern());
    }
}
