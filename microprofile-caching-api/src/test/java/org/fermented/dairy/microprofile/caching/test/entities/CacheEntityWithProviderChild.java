package org.fermented.dairy.microprofile.caching.test.entities;

import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import org.fermented.dairy.microprofile.caching.annotations.Cachable;

@EqualsAndHashCode(callSuper = true)
@Cachable(cacheName = "TestCacheNameChild", cacheProvider = "TestCacheProvider")
@SuperBuilder
public class CacheEntityWithProviderChild extends CacheEntityWithProvider{
}
