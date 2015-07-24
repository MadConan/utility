package net.conan.collections;

import java.util.List;
import java.util.Map;

/**
 *
 * Provides a mechanism to search {@link Map} keys with
 * a given criteria and return the corresponding map values as a List.
 *
 * @author Conan Dombroski ()
 */
public interface MapLookup<S,K,V> {
    List<V> lookup(S searchCriteria, Map<K,V> map);
}
