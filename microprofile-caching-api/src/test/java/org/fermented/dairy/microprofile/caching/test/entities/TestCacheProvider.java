package org.fermented.dairy.microprofile.caching.test.entities;

import lombok.Getter;
import org.fermented.dairy.microprofile.caching.interfaces.CacheProvider;
import org.mockito.Mockito;

import java.util.Optional;

@Getter
@org.fermented.dairy.microprofile.caching.annotations.CacheProvider(name = "TestCacheProvider")
public class TestCacheProvider implements CacheProvider {

    private final CacheProvider mockProvider = Mockito.mock(CacheProvider.class);

    @Override
    public <T, K> Optional<T> getFromCache(K key, String cacheName, Class<T> tClass) {
        return mockProvider.getFromCache(key, cacheName, tClass);
    }

    @Override
    public <T, K> boolean putIntoCache(K key, T value, String cacheName, long ttl) {
        return mockProvider.putIntoCache(key, value, cacheName, ttl);
    }

    @Override
    public <K> boolean invalidateCacheEntry(K key, String cacheName) {
        return mockProvider.invalidateCacheEntry(key, cacheName);
    }
}
