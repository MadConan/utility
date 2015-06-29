package net.conan.collections;

import java.util.List;
import java.util.Map;

/**
 * @author Conan Dombroski (dombroco)
 */
public interface MapLookup {
    <V> List<V> lookup(String regularExpression, Map<String,V> map);
}
