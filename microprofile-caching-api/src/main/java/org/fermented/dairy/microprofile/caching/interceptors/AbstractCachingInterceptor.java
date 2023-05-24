package org.fermented.dairy.microprofile.caching.interceptors;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Any;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;
import lombok.Getter;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.fermented.dairy.microprofile.caching.annotations.Cachable;
import org.fermented.dairy.microprofile.caching.interfaces.CacheProvider;
import org.fermented.dairy.microprofile.caching.utils.MapUtils;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.apache.commons.lang3.StringUtils.isBlank;

@ApplicationScoped
public class AbstractCachingInterceptor {

    @Any
    @Inject
    private Instance<CacheProvider> cacheProviders;


    @Getter
    @Inject
    @ConfigProperty(name = "org.fermented.diary.caching.defaultCache", defaultValue = "LocalHashMapCache")
    private String defaultProviderName;

    @Getter
    @Inject
    @ConfigProperty(name = "org.fermented.diary.caching.defaultTTL", defaultValue = "300000")
    private Long defaultTTL;

    private Map<String, CacheProvider> cacheProviderMap;

    @PostConstruct
    public void buildCacheProviderMap() {
        cacheProviderMap = cacheProviders.stream()
                .collect(Collectors.toMap(
                        this::getCacheNameFromProvider,
                                cacheProvider -> cacheProvider
                        )
                );
    }

    protected <T> CacheProvider getProvider(Class<T> tClass) {
        CacheProvider defaultProvider = cacheProviderMap.get(defaultProviderName);
        Optional<String> providerNameOptional = determineProviderName(tClass);
        if (providerNameOptional.isEmpty()) {
            return defaultProvider;
        }
        String providerName = providerNameOptional.get();
        if (MapUtils.hasKeyNotNull(cacheProviderMap, providerName)) {
            return cacheProviderMap.get(providerName);
        } else {
            return defaultProvider;
        }

    }

    protected <T> Optional<String> determineProviderName(Class<T> tClass) {
        Cachable cachableAnnotation = tClass.getAnnotation(Cachable.class);
        if (cachableAnnotation == null || isBlank(cachableAnnotation.cacheProvider())) {
            return Optional.empty();
        }
        return Optional.of(cachableAnnotation.cacheName());
    }


    protected String getCacheNameFromProvider(CacheProvider cacheProvider) {
        org.fermented.dairy.microprofile.caching.annotations.CacheProvider cacheProviderAnnotation = cacheProvider.getClass().getAnnotation(org.fermented.dairy.microprofile.caching.annotations.CacheProvider.class);
        if (cacheProviderAnnotation == null) {
            return cacheProvider.getClass().getCanonicalName();
        } else {
            return cacheProviderAnnotation.name();
        }
    }
}
