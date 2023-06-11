package org.fermented.dairy.microprofile.caching.test.entities;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import org.fermented.dairy.microprofile.caching.annotations.Cachable;

@Builder
@Getter
@EqualsAndHashCode
@Cachable(cacheName = "TestCacheName", cacheProvider = "TestCacheProvider")
public class CacheEntityWithProviderNoKey {

    @NonNull
    private Long id;

    private String name;

    private String surname;
}
