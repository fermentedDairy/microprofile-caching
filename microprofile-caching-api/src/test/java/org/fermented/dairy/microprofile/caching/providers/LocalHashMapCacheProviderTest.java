package org.fermented.dairy.microprofile.caching.providers;

import org.fermented.dairy.microprofile.caching.test.entities.CacheEntityWithProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;


class LocalHashMapCacheProviderTest {

    LocalHashMapCacheProvider provider = new LocalHashMapCacheProvider();

    static final String CACHE_NAME = "testCacheName";

    static final String CACHE_NAME_2 = "testCacheName2";

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
        assertTrue(provider.getFromCache(entity.getId(), CACHE_NAME, CacheEntityWithProvider.class).isEmpty(),
                "Cached entity is present");
        provider.putIntoCache(entity.getId(), entity, CACHE_NAME, 10);
        assertTrue(provider.getFromCache(
                        entity.getId(),
                        CACHE_NAME,
                        CacheEntityWithProvider.class).isPresent(),
                "cached entity is not present");
        //TODO: better way to pause for 15 ms for cache expiry
        Thread.sleep(15);//NOSONAR: java:S29225, will fix this later
        assertTrue(provider.getFromCache(
                        entity.getId(),
                        CACHE_NAME,
                        CacheEntityWithProvider.class).isEmpty(),
                "cached entity is present");
    }

    @DisplayName("given existing caches then return the list of cache names when fetched")
    @Test
    void givenExistingCachesThenReturnTheListOfCacheNamesWhenFetched(){
        CacheEntityWithProvider entity = CacheEntityWithProvider.builder()
                .id(3L)
                .name("third")
                .surname("3rd")
                .build();
        provider.putIntoCache(entity.getId(), entity, CACHE_NAME, 5);

        entity = CacheEntityWithProvider.builder()
                .id(4L)
                .name("Fourth")
                .surname("4th")
                .build();
        provider.putIntoCache(entity.getId(), entity, CACHE_NAME_2, 5);

        assertArrayEquals(new String[]{CACHE_NAME, CACHE_NAME_2}, provider.getCacheNames().stream().sorted().toList().toArray(new String[0]));
    }

    @DisplayName("given existing caches then return keys associated with the caches when fetched")
    @Test
    void givenExistingCachesThenReturnKeysAssociatedWithTheCachesWhenFetched(){
        CacheEntityWithProvider entity = CacheEntityWithProvider.builder()
                .id(3L)
                .name("third")
                .surname("3rd")
                .build();
        provider.putIntoCache(entity.getId(), entity, CACHE_NAME, 5);

        entity = CacheEntityWithProvider.builder()
                .id(4L)
                .name("Fourth")
                .surname("4th")
                .build();
        provider.putIntoCache(entity.getId(), entity, CACHE_NAME_2, 5);

        assertAll("Validating keys",
                () -> assertFalse(provider.getKeys(CACHE_NAME_2).isEmpty()),
                () -> assertEquals(4L, provider.getKeys(CACHE_NAME_2).stream().findFirst().get()),
                () -> assertFalse(provider.getKeys(CACHE_NAME).isEmpty()),
                () -> assertTrue(provider.getKeys(CACHE_NAME).contains(3L))
        );
    }

    @DisplayName("given existing caches then clear them when cleared")
    @Test
    void givenExistingCachesThenClearThemWhenCleared(){
        CacheEntityWithProvider entity = CacheEntityWithProvider.builder()
                .id(3L)
                .name("third")
                .surname("3rd")
                .build();
        provider.putIntoCache(entity.getId(), entity, CACHE_NAME, 5);

        entity = CacheEntityWithProvider.builder()
                .id(4L)
                .name("Fourth")
                .surname("4th")
                .build();
        provider.putIntoCache(entity.getId(), entity, CACHE_NAME_2, 5);

        provider.getCacheNames().stream().forEach(
                cacheName -> {
                    assertFalse(provider.getKeys(cacheName).isEmpty());
                    provider.clearCache(cacheName);
                    assertTrue(provider.getKeys(cacheName).isEmpty());
                }
        );
    }
}