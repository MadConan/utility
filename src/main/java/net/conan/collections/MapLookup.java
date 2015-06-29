package net.conan.collections;

import java.util.List;
import java.util.Map;

/**
 *
 * Provides a mechanism to search {@link Map Map&lt;String&gt;} keys with
 * regular expression and return the corresponding map values as a List.
 *
 * @author Conan Dombroski (dombroco)
 */
public interface MapLookup {
    <V> List<V> lookup(String regularExpression, Map<String,V> map);
}
