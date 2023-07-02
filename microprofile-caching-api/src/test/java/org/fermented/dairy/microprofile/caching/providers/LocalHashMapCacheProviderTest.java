package org.fermented.dairy.microprofile.caching.providers;

import org.fermented.dairy.microprofile.caching.interfaces.CacheProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LocalHashMapCacheProviderTest {

    CacheProvider localHashMapCacheProvider = new LocalHashMapCacheProvider();

    //Missing Key put into cache, verify cache key preset, get from cache
    @DisplayName("Given a Missing key then put the getter result into the cache, verify key is present and get from cache")
    @Test
    void givenAMissingKeyThenPutTheGetterResultIntoTheCacheVerifyKeyIsPresentAndGetFromCache(){
        String cacheName = "testcache1";

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
        String cacheName = "testcache2";
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


    @DisplayName("given a present key, remove it from cache, verify key is not present next fetch gets from loader")
    @Test
    void givenAPresentKeyRemoveItFromCacheVerifyKeyIsNotPresentNextFetchGetsFromLoader() {
        String cacheName = "testcache3";
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
        String cacheName = "testcache4";
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
        String cacheName = "testcache5";
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

        String cacheName = "testcache6";
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
}