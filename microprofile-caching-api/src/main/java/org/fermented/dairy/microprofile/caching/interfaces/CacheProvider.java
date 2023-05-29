package org.fermented.dairy.microprofile.caching.interfaces;

import java.util.Optional;
import java.util.Collection;

public interface CacheProvider {

    <T, K>  Optional<T> getFromCache(K key, String cacheName, Class<T> tClass);

    <T, K> boolean putIntoCache(K key, T value, String cacheName, long ttl);

    <K> boolean invalidateCacheEntry(K key, String cacheName);

    Collection<String> getCacheNames();

    Collection<Object> getKeys(String cacheName);

    String getProviderName();
}
