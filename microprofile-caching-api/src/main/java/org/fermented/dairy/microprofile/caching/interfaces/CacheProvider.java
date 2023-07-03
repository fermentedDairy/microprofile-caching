package org.fermented.dairy.microprofile.caching.interfaces;

import java.util.Collection;
import java.util.function.Function;
public interface CacheProvider {

    /**
     *
     * @param key The cache key
     * @param cacheName The CacheName
     * @param getter The Function that loads the data based on the cache key
     * @param tClass The class of the returned object
     * @param ttl The time to live (in ms). cache entry expires after ttl ms
     * @param cacheOptionalEmpties If getter Function returns an Empty Optional that gets cached if true, doesn't put if cotherwise,
     * @return the object either fetched from the cache or loaded by the getter
     * @param <T> Cached object type
     * @param <K> Key type
     */
    <T, K>  T loadAndGetFromCache(K key, String cacheName, Function<K, T> getter, Class<T> tClass, long ttl, boolean cacheOptionalEmpties);

    /**
     * See {@link #loadAndGetFromCache(Object, String, Function, Class, long, boolean) loadAndGetFromCache}. This will not cache Optionals loaded by the getter if they are empty.
     *
     * @param key The cache key
     * @param cacheName The CacheName
     * @param getter The Function that loads the data based on the cache key
     * @param tClass The class of the returned object
     * @param ttl The time to live (in ms). cache entry expires after ttl ms
     * @return the object either fetched from the cache or loaded by the getter
     * @param <T> Cached object type
     * @param <K> Key type
     */
    default <T, K>  T loadAndGetFromCache(K key, String cacheName, Function<K, T> getter, Class<T> tClass, long ttl) {
        return loadAndGetFromCache(key, cacheName, getter, tClass, ttl, false);
    }

    /**
     *
     * @param key the key of the cached object to invalidate
     * @param cacheName the name of the cache
     * @param <K> the key type
     */
    <K> void invalidateCacheEntry(K key, String cacheName);

    default <T, K> T replaceCacheEntry(K key, String cacheName, Function<K, T> getter, Class<T> tClass, long ttl, boolean cacheOptionalEmpties){
        invalidateCacheEntry(key, cacheName);
        return loadAndGetFromCache(key, cacheName, getter, tClass, ttl, cacheOptionalEmpties);
    }

    /**
     * Gets all cache names
     * @return Collection of cache names
     */
    Collection<String> getCacheNames();

    /**
     * Gets all cache keys in a cache
     * @param cacheName The name of the cache
     * @return a collection of all keys in the cache
     */
    Collection<Object> getKeys(String cacheName);

    /**
     * Remove all cache entries in a cache
     * @param cacheName The name of the cache to clear
     */
    void clearCache(String cacheName);

    /**
     *  Drops all caches
     */
    void dropAllCaches();

    /**
     * The name of the provider
     * @return The name of the cache provider
     */
    String getProviderName();


}
