package net.conan.collections;

import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @see net.conan.collections.MapLookup
 * @author Conan Dombroski (dombroco)
 */
public class MapLookupImpl implements MapLookup {
    @Override
    public <V> List<V> lookup(String regularExpression, Map<String, V> map) {
        final Pattern pattern = Pattern.compile(regularExpression);
        return  map.keySet()
              .stream()
              .filter(string -> pattern.matcher(string).matches())
              .map(map::get).collect(Collectors.toList());
    }
}

