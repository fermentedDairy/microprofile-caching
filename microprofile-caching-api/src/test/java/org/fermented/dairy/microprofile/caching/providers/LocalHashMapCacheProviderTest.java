package org.fermented.dairy.microprofile.caching.providers;

import org.fermented.dairy.microprofile.caching.interfaces.CacheProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SuppressWarnings("OptionalGetWithoutIsPresent")
class LocalHashMapCacheProviderTest {

    CacheProvider localHashMapCacheProvider = new LocalHashMapCacheProvider();

    String cacheName = "testcache";

    @BeforeEach
    void clearTheCache(){
        localHashMapCacheProvider.dropAllCaches();
        assertTrue(localHashMapCacheProvider.getCacheNames().isEmpty(), "Caches not dropped");
    }

    //Missing Key put into cache, verify cache key preset, get from cache
    @DisplayName("Given a Missing key then put the getter result into the cache, verify key is present and get from cache")
    @Test
    void givenAMissingKeyThenPutTheGetterResultIntoTheCacheVerifyKeyIsPresentAndGetFromCache(){

        String actual1 = localHashMapCacheProvider.loadAndGetFromCache(
                "key1", cacheName, str -> "inserted 1", String.class, 500L

        );
        assertAll("Verify Cache Miss Insert",
                () -> assertEquals("inserted 1", actual1, "incorrect value retrieved from getter"),
                () -> assertTrue(localHashMapCacheProvider.getCacheNames().contains(cacheName), "Cache not present"),
                () -> assertTrue(localHashMapCacheProvider.getKeys(cacheName).contains("key1"), "key not present")
        );

        String actual2 = localHashMapCacheProvider.loadAndGetFromCache(
                "key1", cacheName, str -> "inserted 2", String.class, 500L

        );

        assertAll("Verify Cache Hit Fetch",
                () -> assertEquals("inserted 1", actual2, "incorrect value retrieved from cache"),
                () -> assertTrue(localHashMapCacheProvider.getCacheNames().contains(cacheName), "Cache not present"),
                () -> assertTrue(localHashMapCacheProvider.getKeys(cacheName).contains("key1"), "key not present")
        );
    }

    @DisplayName("given a present key that is expired, remove it from cache, get new value")
    @Test
    void givenAPresentKeyThatIsExpiredRemoveItFromCacheVerifyKeyIsNotPresent() throws InterruptedException {

        String actual1 = localHashMapCacheProvider.loadAndGetFromCache(
                "key1", cacheName, str -> "inserted 1", String.class, 10L

        );
        assertAll("Verify Cache Miss Insert",
                () -> assertEquals("inserted 1", actual1, "incorrect value retrieved from getter"),
                () -> assertTrue(localHashMapCacheProvider.getCacheNames().contains(cacheName), "Cache not present"),
                () -> assertTrue(localHashMapCacheProvider.getKeys(cacheName).contains("key1"), "key is present")
        );

        //TODO: find a better way to sleep the thread
        Thread.sleep(20L);//NOSONAR: java:S2925, might be a little fragile, but fine for now

        String actual2 = localHashMapCacheProvider.loadAndGetFromCache(
                "key1", cacheName, str -> "inserted 2", String.class, 500L

        );



        assertAll("Verify Cache Hit Fetch",
                () -> assertEquals("inserted 2", actual2, "incorrect value retrieved from cache"),
                () -> assertTrue(localHashMapCacheProvider.getCacheNames().contains(cacheName), "Cache not present"),
                () -> assertTrue(localHashMapCacheProvider.getKeys(cacheName).contains("key1"), "key not present")
        );

    }

    @DisplayName("given a present key that is expired, remove it from cache, verify key is not present when requesting list of keys")
    @Test
    void givenAPresentKeyThatIsExpiredRemoveItFromCacheVerifyKeyIsNotPresentWhenRequestingListOfKeys() throws InterruptedException {

        String actual1 = localHashMapCacheProvider.loadAndGetFromCache(
                "key1", cacheName, str -> "inserted 1", String.class, 10L

        );
        assertAll("Verify Cache Miss Insert",
                () -> assertEquals("inserted 1", actual1, "incorrect value retrieved from getter"),
                () -> assertTrue(localHashMapCacheProvider.getCacheNames().contains(cacheName), "Cache not present"),
                () -> assertTrue(localHashMapCacheProvider.getKeys(cacheName).contains("key1"), "key is present")
        );

        //TODO: find a better way to sleep the thread
        Thread.sleep(20L);//NOSONAR: java:S2925, might be a little fragile, but fine for now

        assertFalse(localHashMapCacheProvider.getKeys(cacheName).contains("key1")); //Needs to be in its own test for the expiry cache miss scenario

    }


    @DisplayName("given a present key, remove it from cache, verify key is not present next fetch gets from loader")
    @Test
    void givenAPresentKeyRemoveItFromCacheVerifyKeyIsNotPresentNextFetchGetsFromLoader() {

        String actual1 = localHashMapCacheProvider.loadAndGetFromCache(
                "key1", cacheName, str -> "inserted 1", String.class, 500L

        );
        assertAll("Verify Cache Miss Insert",
                () -> assertEquals("inserted 1", actual1, "incorrect value retrieved from getter"),
                () -> assertTrue(localHashMapCacheProvider.getCacheNames().contains(cacheName), "Cache not present"),
                () -> assertTrue(localHashMapCacheProvider.getKeys(cacheName).contains("key1"), "key not present")
        );

        localHashMapCacheProvider.invalidateCacheEntry(
                "key1", cacheName
        );

        String actual2 = localHashMapCacheProvider.loadAndGetFromCache(
                "key1", cacheName, str -> "inserted 2", String.class, 500L

        );

        assertAll("Verify Cache Miss Insert",
                () -> assertEquals("inserted 2", actual2, "incorrect value retrieved from getter"),
                () -> assertTrue(localHashMapCacheProvider.getCacheNames().contains(cacheName), "Cache not present"),
                () -> assertTrue(localHashMapCacheProvider.getKeys(cacheName).contains("key1"), "key not present")
        );

    }

    //Missing key, remove is a no op, verify other keys are still present
    @DisplayName("given a missing key, verify key is not present other keys not impacted next fetch gets from loader")
    @Test
    void givenAMissingKeyRemoveItFromCacheVerifyKeyIsNotPresentNextFetchGetsFromLoader() {

        String actual1 = localHashMapCacheProvider.loadAndGetFromCache(
                "key1", cacheName, str -> "inserted 1", String.class, 500L

        );
        assertAll("Verify Cache Miss Insert",
                () -> assertEquals("inserted 1", actual1, "incorrect value retrieved from getter"),
                () -> assertTrue(localHashMapCacheProvider.getCacheNames().contains(cacheName), "Cache not present"),
                () -> assertTrue(localHashMapCacheProvider.getKeys(cacheName).contains("key1"), "key not present")
        );

        localHashMapCacheProvider.invalidateCacheEntry(
                "key2", cacheName
        );

        assertAll("Validate Cache Keys",
                () -> assertTrue(localHashMapCacheProvider.getKeys(cacheName).contains("key1"), "key not present"),
                () -> assertFalse(localHashMapCacheProvider.getKeys(cacheName).contains("key2"), "key present")
        );

        String actual2 = localHashMapCacheProvider.loadAndGetFromCache(
                "key2", cacheName, str -> "inserted 2", String.class, 500L

        );

        assertAll("Verify Cache Miss Insert",
                () -> assertEquals("inserted 2", actual2, "incorrect value retrieved from getter"),
                () -> assertTrue(localHashMapCacheProvider.getCacheNames().contains(cacheName), "Cache not present"),
                () -> assertTrue(localHashMapCacheProvider.getKeys(cacheName).contains("key2"), "key not present")
        );

        String actual3 = localHashMapCacheProvider.loadAndGetFromCache(
                "key2", cacheName, str -> "inserted 3", String.class, 500L

        );

        assertAll("Verify Cache Miss Insert",
                () -> assertEquals("inserted 2", actual3, "incorrect value retrieved from getter"),
                () -> assertTrue(localHashMapCacheProvider.getCacheNames().contains(cacheName), "Cache not present"),
                () -> assertTrue(localHashMapCacheProvider.getKeys(cacheName).contains("key2"), "key not present")
        );

    }

    //Add to cache, clear the cache
    @DisplayName("given a cache with keys, clear cache, verify cache is empty")
    @Test
    void givenACacheWithKeysClearCacheVerifyCacheIsEmpty() {

        String actual = localHashMapCacheProvider.loadAndGetFromCache(
                "key1", cacheName, str -> "inserted 1", String.class, 500L

        );
        assertAll("Verify Cache Miss Insert",
                () -> assertEquals("inserted 1", actual, "incorrect value retrieved from getter"),
                () -> assertTrue(localHashMapCacheProvider.getCacheNames().contains(cacheName), "Cache not present"),
                () -> assertTrue(localHashMapCacheProvider.getKeys(cacheName).contains("key1"), "key not present")
        );
    }

    @DisplayName("given a cache with keys when clearing cache then remove all keys")
    @Test
    void givenACacheWithKeysWhenClearingCacheThenRemoveAllKeys(){

        String actual1 = localHashMapCacheProvider.loadAndGetFromCache(
                "key1", cacheName, str -> "inserted 1", String.class, 500L

        );
        assertAll("Verify Cache Miss Insert",
                () -> assertEquals("inserted 1", actual1, "incorrect value retrieved from getter"),
                () -> assertTrue(localHashMapCacheProvider.getCacheNames().contains(cacheName), "Cache not present"),
                () -> assertTrue(localHashMapCacheProvider.getKeys(cacheName).contains("key1"), "key not present")
        );

        String actual2 = localHashMapCacheProvider.loadAndGetFromCache(
                "key2", cacheName, str -> "inserted 2", String.class, 500L

        );
        assertAll("Verify Cache Miss Insert",
                () -> assertEquals("inserted 2", actual2, "incorrect value retrieved from getter"),
                () -> assertTrue(localHashMapCacheProvider.getCacheNames().contains(cacheName), "Cache not present"),
                () -> assertTrue(localHashMapCacheProvider.getKeys(cacheName).contains("key1"), "key not present")
        );

        localHashMapCacheProvider.clearCache(cacheName);

        assertTrue(localHashMapCacheProvider.getKeys(cacheName).isEmpty());
    }

    @DisplayName("Given a Missing key then put the present optional getter result into the cache, verify key is present and get from cache")
    @Test
    void GivenAMissingKeyThenPutThePresentOptionalGetterResultIntoTheCacheVerifyKeyIsPresentAndGetFromCache(){
        @SuppressWarnings("rawtypes") Optional actual1 = localHashMapCacheProvider.loadAndGetFromCache(
                "key1", cacheName, str -> Optional.of("inserted 1"), Optional.class, 500L

        );
        assertAll("Verify Cache Miss Insert",
                () -> assertEquals("inserted 1", actual1.get(), "incorrect value retrieved from getter"),
                () -> assertTrue(localHashMapCacheProvider.getCacheNames().contains(cacheName), "Cache not present"),
                () -> assertTrue(localHashMapCacheProvider.getKeys(cacheName).contains("key1"), "key not present")
        );

        @SuppressWarnings("rawtypes") Optional actual2 = localHashMapCacheProvider.loadAndGetFromCache(
                "key1", cacheName, str -> Optional.of("inserted 2"), Optional.class, 500L

        );

        assertAll("Verify Cache Hit Fetch",
                () -> assertEquals("inserted 1", actual2.get(), "incorrect value retrieved from cache"),
                () -> assertTrue(localHashMapCacheProvider.getCacheNames().contains(cacheName), "Cache not present"),
                () -> assertTrue(localHashMapCacheProvider.getKeys(cacheName).contains("key1"), "key not present")
        );
    }

    @DisplayName("Given a Missing key then put the empty optional getter result into the cache, verify key is present and get from cache")
    @Test
    void GivenAMissingKeyThenPutTheEmptyOptionalGetterResultIntoTheCacheVerifyKeyIsPresentAndGetFromCache(){
        @SuppressWarnings("rawtypes") Optional actual1 = localHashMapCacheProvider.loadAndGetFromCache(
                "key1", cacheName, str -> Optional.empty(), Optional.class, 500L

        );
        assertAll("Verify Cache Missed",
                () -> assertTrue(actual1.isEmpty(), "incorrect value retrieved from getter"),
                () -> assertFalse(localHashMapCacheProvider.getKeys(cacheName).contains("key1"), "key present")
        );

        @SuppressWarnings("rawtypes") Optional actual2 = localHashMapCacheProvider.loadAndGetFromCache(
                "key1", cacheName, str -> Optional.of("inserted 2"), Optional.class, 500L

        );

        assertAll("Verify Cache Hit Fetch",
                () -> assertEquals("inserted 2", actual2.get(), "incorrect value retrieved from cache"),
                () -> assertTrue(localHashMapCacheProvider.getCacheNames().contains(cacheName), "Cache not present"),
                () -> assertTrue(localHashMapCacheProvider.getKeys(cacheName).contains("key1"), "key not present")
        );
    }

    @DisplayName("Given a Missing key then put the empty optional that should be cached getter result into the cache, verify key is present and get from cache")
    @Test
    void GivenAMissingKeyThenPutTheEmptyOptionalThatShouldBeCachedGetterResultIntoTheCacheVerifyKeyIsPresentAndGetFromCache(){
        @SuppressWarnings("rawtypes") Optional actual1 = localHashMapCacheProvider.loadAndGetFromCache(
                "key1", cacheName, str -> Optional.empty(), Optional.class, 500L, true

        );
        assertAll("Verify Cache Missed",
                () -> assertTrue(actual1.isEmpty(), "incorrect value retrieved from getter"),
                () -> assertTrue(localHashMapCacheProvider.getKeys(cacheName).contains("key1"), "key present")
        );

        @SuppressWarnings("rawtypes") Optional actual2 = localHashMapCacheProvider.loadAndGetFromCache(
                "key1", cacheName, str -> Optional.of("inserted 2"), Optional.class, 500L, true

        );

        assertAll("Verify Cache Hit Fetch",
                () -> assertTrue(actual2.isEmpty(), "incorrect value retrieved from cache"),
                () -> assertTrue(localHashMapCacheProvider.getKeys(cacheName).contains("key1"), "key not present")
        );
    }
}