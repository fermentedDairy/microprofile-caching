package org.fermented.dairy.microprofile.caching.providers;

import org.fermented.dairy.microprofile.caching.interfaces.CacheProvider;

import java.lang.ref.SoftReference;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class LocalHashMapCacheProvider implements CacheProvider {

    private static final Map<String, Map<Object, SoftReference<CacheEntry>>> CACHES = new ConcurrentHashMap<>();

    @Override
    public <T, K> Optional<T> getFromCache(K key, String cacheName, Class<T> tClass) {
        Map<Object, SoftReference<CacheEntry>> cache = getCache(cacheName);
        if (cache.containsKey(key)) {
            SoftReference<CacheEntry> reference = cache.get(key);
            if (reference != null &&
                    reference.get() != null &&
                    Objects.requireNonNull(reference.get()).expiry().isAfter(LocalDateTime.now())) {
                return Optional.of((T) reference.get().value());
            } else {
                cache.remove(key); //null value should be removed but are considered a cache miss
            }
        }
        return Optional.empty();
    }

    @Override
    public <T, K> boolean putIntoCache(K key, T value, String cacheName, long ttl) {
        Map<Object, SoftReference<CacheEntry>> cache = getCache(cacheName);
        cache.put(key, new SoftReference(
                CacheEntry.buildCacheEntry(ttl, value)
        ));
        return true;
    }

    @Override
    public <K> boolean invalidateCacheEntry(K key, String cacheName) {
        Map<Object, SoftReference<CacheEntry>> cache = getCache(cacheName);
        cache.remove(key);
        return true;
    }

    @Override
    public Collection<String> getCacheNames() {
        return CACHES.keySet();
    }

    @Override
    public Collection<Object> getKeys(String cacheName) {
        if (CACHES.containsKey(cacheName) && CACHES.get(cacheName) != null) {
            return CACHES.get(cacheName).keySet();
        }
        return Collections.emptySet();
    }

    @Override
    public void clearCache(String cacheName) {
        if (CACHES.containsKey(cacheName) && CACHES.get(cacheName) != null) {
            CACHES.get(cacheName).clear();
        }

    }

    @Override
    public String getProviderName() {
        return "LocalHashMapCache";
    }

    private Map<Object, SoftReference<CacheEntry>> getCache(String cacheName) {
        if (!CACHES.containsKey(cacheName) || CACHES.get(cacheName) == null) {
            CACHES.put(cacheName, new ConcurrentHashMap<>());
        }
        return CACHES.get(cacheName);
    }

    private record CacheEntry(LocalDateTime expiry, Object value) {
        public static CacheEntry buildCacheEntry(long ttl, Object value) {
            return new CacheEntry(LocalDateTime.now().plus(ttl, ChronoUnit.MILLIS), value);
        }
    }
}
