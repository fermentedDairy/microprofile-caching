package org.fermented.dairy.microprofile.caching.providers;

import org.fermented.dairy.microprofile.caching.interfaces.CacheProvider;

import java.lang.ref.SoftReference;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
@org.fermented.dairy.microprofile.caching.annotations.CacheProvider(name = "LocalHashMapCache")
public class LocalHashMapCacheProvider implements CacheProvider {

    private static final Map<String, Map<Object, SoftReference<CacheEntry>>> CACHES = new ConcurrentHashMap<>();

    @Override
    public <T, K> Optional<T> getFromCache(K key, String cacheName, Class<T> tClass) {
        if (CACHES.containsKey(cacheName) && CACHES.get(cacheName) != null) {
            Map<Object, SoftReference<CacheEntry>> cache = CACHES.get(cacheName);
            if (cache.containsKey(key)) {
                SoftReference<CacheEntry> reference = cache.get(key);
                if ( reference != null &&
                        reference.get() != null &&
                        Objects.requireNonNull(reference.get()).expiry().isAfter(LocalDateTime.now())) {
                    return Optional.of((T) reference.get().value());
                } else {
                    cache.remove(key); //null value should be removed but are considered a cache miss
                }
            }
        } else {
            CACHES.put(cacheName, new ConcurrentHashMap<>());
        }
        return Optional.empty();
    }

    @Override
    public <T, K> boolean putIntoCache(K key, T value, String cacheName, long ttl) {
        Map<Object, SoftReference<CacheEntry>> cache = CACHES.get(cacheName);
        cache.put(key, new SoftReference(
                CacheEntry.buildCacheEntry(ttl, value)
        ));
        return true;
    }

    @Override
    public <K> boolean invalidateCacheEntry(K key, String cacheName) {
        Map<Object, SoftReference<CacheEntry>> cache = CACHES.get(cacheName);
        cache.remove(key);
        return true;
    }

    private record CacheEntry(LocalDateTime expiry, Object value) {
        public static CacheEntry buildCacheEntry(long ttl, Object value) {
            return new CacheEntry(LocalDateTime.now().plus(ttl, ChronoUnit.MILLIS), value);
        }
    }
}
