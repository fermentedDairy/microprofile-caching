package org.fermented.dairy.microprofile.caching.providers;

import org.fermented.dairy.microprofile.caching.test.entities.CacheEntityWithProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


class LocalHashMapCacheProviderTest {

    LocalHashMapCacheProvider provider = new LocalHashMapCacheProvider();

    static final String CACHE_NAME = "testCacheName";

    static final Long FIVE_MINUTES_IN_MILLIS = 300000L;

    @DisplayName("given a key and value that aren't in the cache then add to cache and validate after retrieve")
    @Test
    void givenAKeyAndValueThatArentInTheCacheAddToCacheAndValidateRetrieve() {
        CacheEntityWithProvider entity = CacheEntityWithProvider.builder()
                .id(1L)
                .name("first")
                .surname("1st")
                .build();
        assertTrue(provider.getFromCache(entity.getId(), CACHE_NAME, CacheEntityWithProvider.class).isEmpty());
        provider.putIntoCache(entity.getId(), entity, CACHE_NAME, FIVE_MINUTES_IN_MILLIS);
        Optional<CacheEntityWithProvider> actual = provider.getFromCache(entity.getId(), CACHE_NAME, CacheEntityWithProvider.class);
        assertAll(
                "Validating Cache Record",
                () -> assertTrue(actual.isPresent(), "cached entity is not present"),
                () -> assertEquals(entity, actual.get(), "cached entity is not equal to inserted entity")
        );
    }

    @DisplayName("given a key and value that aren't in the cache then add to cache, remove then validate remove")
    @Test
    void givenAKeyAndValueThatAreInTheCacheAddToCacheRemoveAndValidateRemove() {
        CacheEntityWithProvider entity = CacheEntityWithProvider.builder()
                .id(2L)
                .name("second")
                .surname("2nd")
                .build();
        assertTrue(provider.getFromCache(entity.getId(), CACHE_NAME, CacheEntityWithProvider.class).isEmpty());
        provider.putIntoCache(entity.getId(), entity, CACHE_NAME, FIVE_MINUTES_IN_MILLIS);
        assertTrue(provider.getFromCache(
                        entity.getId(),
                        CACHE_NAME,
                        CacheEntityWithProvider.class).isPresent(),
                "cached entity is not present");
        provider.invalidateCacheEntry(entity.getId(), CACHE_NAME);
        assertTrue(provider.getFromCache(
                        entity.getId(),
                        CACHE_NAME,
                        CacheEntityWithProvider.class).isEmpty(),
                "cached entity is present");

    }

    @DisplayName("given a key and value that aren't in the cache then add to cache and retrieve after expiry, validate removal from cache")
    @Test
    void givenAKeyAndValueThatArentInTheCacheThenAddToCacheAndRetrieveAfterExpiryValidateRemovalFromCache() throws InterruptedException {
        CacheEntityWithProvider entity = CacheEntityWithProvider.builder()
                .id(3L)
                .name("third")
                .surname("3rd")
                .build();
        assertTrue(provider.getFromCache(entity.getId(), CACHE_NAME, CacheEntityWithProvider.class).isEmpty());
        provider.putIntoCache(entity.getId(), entity, CACHE_NAME, 5);
        assertTrue(provider.getFromCache(
                        entity.getId(),
                        CACHE_NAME,
                        CacheEntityWithProvider.class).isPresent(),
                "cached entity is not present");
        //TODO: better way to pause for 10 ms for cache expiry
        Thread.sleep(10);//NOSONAR: java:S29225, will fis this later
        assertTrue(provider.getFromCache(
                        entity.getId(),
                        CACHE_NAME,
                        CacheEntityWithProvider.class).isEmpty(),
                "cached entity is present");
    }
}