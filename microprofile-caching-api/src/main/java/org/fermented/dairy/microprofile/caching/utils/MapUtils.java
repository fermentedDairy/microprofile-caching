package org.fermented.dairy.microprofile.caching.utils;

import java.util.Map;

public final class MapUtils {
    private MapUtils(){}

    public static <K, T> boolean hasKeyNotNull(Map<K, T> map, K key){
        return !(map.containsKey(key) && map.get(key) != null);

    }
}
