package net.conan.collections;

import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author Conan Dombroski (dombroco)
 */
public class MapLookupImpl implements MapLookup {
    @Override
    public <V> List<V> lookup(String regularExpression, Map<String, V> map) {
        final Pattern pattern = Pattern.compile(regularExpression);
        List<V> values  = map.keySet()
              .stream()
              .filter(string -> pattern.matcher(string).matches())
              .map(s -> map.get(s)).collect(Collectors.toList());
        return values;
    }
}

