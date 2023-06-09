package org.fermented.dairy.microprofile.caching.test.entities;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;
import org.fermented.dairy.microprofile.caching.annotations.Cachable;
import org.fermented.dairy.microprofile.caching.annotations.CacheKey;

@SuperBuilder
@Getter
@EqualsAndHashCode
@Cachable(cacheName = "TestCacheName", cacheProvider = "TestCacheProvider")
public class CacheEntityWithProvider {

    @CacheKey
    @NonNull
    private Long id;

    private String name;

    private String surname;
}
