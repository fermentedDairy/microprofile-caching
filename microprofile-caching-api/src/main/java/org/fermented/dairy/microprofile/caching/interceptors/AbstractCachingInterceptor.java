package org.fermented.dairy.microprofile.caching.interceptors;

import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;
import lombok.Getter;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.fermented.dairy.microprofile.caching.annotations.Cachable;
import org.fermented.dairy.microprofile.caching.interfaces.CacheProvider;
import org.fermented.dairy.microprofile.caching.utils.CacheInterceptorUtils;

import java.util.Map;
import java.util.Optional;

import static org.apache.commons.lang3.StringUtils.isBlank;

@Dependent
public class AbstractCachingInterceptor {

    @Getter
    @Inject
    @ConfigProperty(name = "org.fermented.diary.caching.defaultCache", defaultValue = "LocalHashMapCache")
    private String defaultProviderName;

    @Getter
    @Inject
    @ConfigProperty(name = "org.fermented.diary.caching.defaultTTL", defaultValue = "300000")
    private Long defaultTTL;

    @Inject
    private Map<String, CacheProvider> cacheProviderMap;

    protected <T> CacheProvider getProvider(Class<T> tClass) {
        CacheProvider defaultProvider = cacheProviderMap.get(defaultProviderName);
        Optional<String> providerNameOptional = determineProviderName(tClass);
        if (providerNameOptional.isEmpty()) {
            return defaultProvider;
        }
        String providerName = providerNameOptional.get();
        if (CacheInterceptorUtils.hasKeyNotNull(cacheProviderMap, providerName)) {
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
        return Optional.of(cachableAnnotation.cacheProvider());
    }

}
