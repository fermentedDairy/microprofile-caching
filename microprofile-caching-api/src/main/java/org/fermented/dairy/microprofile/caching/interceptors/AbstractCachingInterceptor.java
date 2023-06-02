package org.fermented.dairy.microprofile.caching.interceptors;

import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;
import jakarta.interceptor.InvocationContext;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.fermented.dairy.microprofile.caching.annotations.Cachable;
import org.fermented.dairy.microprofile.caching.annotations.CacheKey;
import org.fermented.dairy.microprofile.caching.exceptions.NoCacheKeyException;
import org.fermented.dairy.microprofile.caching.interfaces.CacheProvider;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
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
        if (hasKeyNotNull(cacheProviderMap, providerName)) {
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

    protected Object getCacheKeyFromParams(InvocationContext invocationContext){
        Method method = invocationContext.getMethod();
        if(method.getParameterCount() == 1) { //There is only one param, use it as the cache key
            return invocationContext.getParameters()[0];
        }
        Annotation[][] paramAnnotations = method.getParameterAnnotations();
        for (int outerIndex = 0; outerIndex < paramAnnotations.length; outerIndex++){
            Annotation[] annotationsForParam = paramAnnotations[outerIndex];
            for (int innerIndex = 0; innerIndex < annotationsForParam.length; innerIndex++){
                if (annotationsForParam[innerIndex].annotationType().equals(CacheKey.class)){
                    return invocationContext.getParameters()[outerIndex];
                }
            }
        }
        throw new NoCacheKeyException("Could not identify the cache key for method %s in %s", method.getName(), method.getDeclaringClass());

    }

    protected <T> String getCacheName(Class<T> tClass){
        Cachable cachableAnnotation = tClass.getAnnotation(Cachable.class);
        if(cachableAnnotation == null || StringUtils.isBlank(cachableAnnotation.cacheName())){
            return tClass.getCanonicalName();
        }
        return cachableAnnotation.cacheName();

    }

    protected <T> long getTTL(Class<T> tClass, long defaultTTL) {
        Cachable cachableAnnotation = tClass.getAnnotation(Cachable.class);
        if(cachableAnnotation == null){
            return defaultTTL;
        }
        return cachableAnnotation.defaultTtl();
    }

    protected boolean hasKeyNotNull(Map<String, CacheProvider>  map, String key){
        return (map.containsKey(key) && map.get(key) != null);

    }

}
