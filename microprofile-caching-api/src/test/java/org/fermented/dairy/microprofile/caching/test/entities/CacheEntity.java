package org.fermented.dairy.microprofile.caching.test.entities;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import org.fermented.dairy.microprofile.caching.annotations.Cachable;
import org.fermented.dairy.microprofile.caching.annotations.CacheKey;

@Builder
@Getter
@EqualsAndHashCode
@Cachable(cacheName = "TestCacheName", cacheProvider = "TestCacheProvider")
public class CacheEntity {

    @CacheKey
    @NonNull
    private Long id;

    private String name;

    private String surname;
}
