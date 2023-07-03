package org.fermented.dairy.microprofile.caching.providers;

import lombok.Getter;
import org.fermented.dairy.microprofile.caching.exceptions.CacheRuntimeException;
import org.fermented.dairy.microprofile.caching.interfaces.CacheProvider;

import java.lang.ref.SoftReference;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Function;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class LocalHashMapCacheProvider implements CacheProvider {

    private static final long CACHE_FETCH_TIMEOUT = 10000L; //10 s

    private static final Map<String, Map<Object, CacheEntry>> CACHES = new ConcurrentHashMap<>();

    @Override
    public <T, K> T loadAndGetFromCache(final K key,
                                        final String cacheName,
                                        final Function<K, T> getter,
                                        final Class<T> tClass,
                                        final long ttl,
                                        final boolean cacheOptionalEmpties) {
        if (ttl < 0) {
            throw new CacheRuntimeException("TTL cannot be negative");
        }

        CacheEntry value = getCache(cacheName)
                .get(key);

        if (value == null) { //cache miss, add fetched value
            value = getNewCacheEntry(key, cacheName, getter, ttl, cacheOptionalEmpties);
            return value.getValue(tClass);
        }

        LocalDateTime cacheFetchTimeout = LocalDateTime.now().plus(CACHE_FETCH_TIMEOUT, ChronoUnit.MILLIS);

        while (value.isLocked()) {
            if (cacheFetchTimeout.isBefore(LocalDateTime.now())) {
                throw new CacheRuntimeException("cache value lock not released within timeout");
            }
        }

        if (value.getValue() == null ||
                value.getExpiry().isBefore(LocalDateTime.now())) { //cache miss, Soft reference has been cleaned up or cache is expired
            updateCacheValue(key, cacheName, getter, ttl, value, cacheOptionalEmpties);
            return value.getValue(tClass);
        }
        return value.getValue(tClass); //Cache hit, return cached value
    }

    private <T, K> void updateCacheValue(K key, String cacheName, Function<K, T> getter, long ttl, CacheEntry value, boolean cacheOptionalEmpties) {
        try {
            value.lock();
            T result = getter.apply(key);
            if((result instanceof @SuppressWarnings("rawtypes")Optional optionalResult) && optionalResult.isEmpty() && !cacheOptionalEmpties) {
                getCache(cacheName).remove(key);
            }
            value.setValue(result);
            value.updateTTL(ttl);
        } finally {
            value.unlock();
        }
    }

    private <T, K> CacheEntry getNewCacheEntry(final K key,
                                               final String cacheName,
                                               final Function<K, T> getter,
                                               final long ttl,
                                               final boolean cacheOptionalEmpties) {
        CacheEntry value = new CacheEntry();
        try {
            getCache(cacheName).put(key, value);
            value.lock();
            T result = getter.apply(key);
            if(result instanceof Optional<?> optionalResult && optionalResult.isEmpty() && !cacheOptionalEmpties) {
                getCache(cacheName).remove(key);
            }
            value.setValue(result);
            value.updateTTL(ttl);
        } finally {
            value.unlock();
        }
        return value;
    }

    @Override
    public <K> void invalidateCacheEntry(final K key, final String cacheName) {
        getCache(cacheName).remove(key);

    }

    @Override
    public Collection<String> getCacheNames() {
        return CACHES.keySet();
    }

    @Override
    public Collection<Object> getKeys(final String cacheName) {
        Map<Object, CacheEntry> cache = getCache(cacheName);
        cache.entrySet().stream()
                .filter(entry -> !entry.getValue().isLocked())//Being updated anyway
                .filter(entry -> entry.getValue().getExpiry().isBefore(LocalDateTime.now()))
                .forEach(entry -> cache.remove(entry.getKey()));
        return getCache(cacheName).keySet();
    }

    @Override
    public void clearCache(final String cacheName) {
        getCache(cacheName).clear();
    }

    @Override
    public String getProviderName() {
        return "LocalHashMapCache";
    }


    private Map<Object, CacheEntry> getCache(final String cacheName) {
        if (!CACHES.containsKey(cacheName) || CACHES.get(cacheName) == null) {//NOSONAR: javaS3824: compile issue otherwise
            CACHES.put(cacheName, new ConcurrentHashMap<>());
        }
        return CACHES.get(cacheName);
    }

    @Override
    public void dropAllCaches(){
        CACHES.clear();
    }

    @Getter
    private static class CacheEntry extends ReentrantLock {

        CacheEntry() {
            expiry = LocalDateTime.now();
        }

        private LocalDateTime expiry;

        @SuppressWarnings({"java:S1948", // There is no plan to serialize this object, ignore that this field is not serializable
                "rawtypes" //Can't generify this right now
        })
        private SoftReference value;

        public void setValue(final Object value) {
            this.value = new SoftReference<>(value);
        }

        @SuppressWarnings({"unchecked", "DataFlowIssue"})
        public <T> T getValue(final Class<T> tClass) {
            if (value != null && value.get() != null) {
                if (tClass.equals(value.get().getClass())) {
                    return (T) value.get();
                } else {
                    throw new CacheRuntimeException("Cached Object is of the incorrect type");
                }
            }
            return null;
        }

        public void updateTTL(final long ttl) {
            expiry = LocalDateTime.now().plus(ttl, ChronoUnit.MILLIS);
        }
    }
}
