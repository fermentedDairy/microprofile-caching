package org.fermented.dairy.microprofile.caching.providers;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Instance;
import jakarta.enterprise.inject.Produces;
import jakarta.enterprise.inject.spi.CDI;
import org.fermented.dairy.microprofile.caching.interfaces.CacheProvider;

import java.util.Map;
import java.util.stream.Collectors;

@ApplicationScoped
public class CacheProviderProvider {


    @Produces
    public Map<String, CacheProvider> buildCacheProviderMap() {
        Instance<CacheProvider> cacheProviders = CDI.current().select(CacheProvider.class);
        return cacheProviders.stream()
                .collect(Collectors.toMap(
                                CacheProvider::getProviderName,
                                cacheProvider -> cacheProvider
                        )
                );
    }
}
