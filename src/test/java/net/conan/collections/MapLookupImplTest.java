package net.conan.collections;

import junit.framework.TestCase;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Conan Dombroski (dombroco)
 */
public class MapLookupImplTest {

    @Test
    public void testLookup() throws Exception {
        Map<String,List<String>> map = new HashMap<>();
        map.put("Food List", Arrays.asList("apple","orange","steak","kiwi"));
        map.put("Auto Makers",Arrays.asList("BMW","Porsche","Ferrari"));
        map.put("Software Companies",Arrays.asList("Microsoft","IBM","Vertafore"));
        MapLookupImpl lookup = new MapLookupImpl();
        List<List<String>> lists = lookup.lookup("\\w+\\s\\w+i\\w+",map);
        TestCase.assertEquals(lists.size(),2);
        TestCase.assertTrue(map.get("Food List").containsAll(lists.get(0)) ||
                            map.get("Software Companies").containsAll(lists.get(0)));
        TestCase.assertTrue(map.get("Food List").containsAll(lists.get(1)) ||
              map.get("Software Companies").containsAll(lists.get(1)));
    }

    @Test
    public void testLookupNotFound() throws Exception {
        Map<String,List<String>> map = new HashMap<>();
        map.put("Food List", Arrays.asList("apple","orange","steak","kiwi"));
        map.put("Auto Makers",Arrays.asList("BMW","Porsche","Ferrari"));
        map.put("Software Companies",Arrays.asList("Microsoft","IBM","Vertafore"));

        MapLookupImpl lookup = new MapLookupImpl();
        List<List<String>> lists = lookup.lookup("fooooooooo",map);
        TestCase.assertEquals(lists.size(),0);
    }

    @Test
    public void testLookupSystemProps() throws Exception {
        Map<String,String> map = (Map) System.getProperties();

        List<String> values = (new MapLookupImpl()).lookup("user.*",map);
        TestCase.assertEquals(true,values.size() > 0);
    }
}