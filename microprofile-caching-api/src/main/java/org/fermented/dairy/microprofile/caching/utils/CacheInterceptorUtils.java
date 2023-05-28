package org.fermented.dairy.microprofile.caching.utils;

import jakarta.interceptor.InvocationContext;
import org.apache.commons.lang3.StringUtils;
import org.fermented.dairy.microprofile.caching.exceptions.NoCacheKeyException;
import org.fermented.dairy.microprofile.caching.annotations.CacheKey;
import org.fermented.dairy.microprofile.caching.annotations.Cachable;
import org.fermented.dairy.microprofile.caching.interfaces.CacheProvider;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Map;

public final class CacheInterceptorUtils {
    private CacheInterceptorUtils(){}

    public static Object getCacheKeyFromParams(InvocationContext invocationContext){
        Method method = invocationContext.getMethod();
        if(method.getParameterCount() == 1) { //There is only one param, use it as the cache key
            return invocationContext.getParameters()[0];
        }
        Annotation[][] paramAnnotations = method.getParameterAnnotations();
        for (int outerIndex = 0; outerIndex < paramAnnotations.length; outerIndex++){
            Annotation[] annotationsForParam = paramAnnotations[outerIndex];
            for (int innerIndex = 0; innerIndex < annotationsForParam.length; innerIndex++){
                if (annotationsForParam[innerIndex] instanceof CacheKey){
                    return invocationContext.getParameters()[outerIndex];
                }
            }
        }
        throw new NoCacheKeyException("Could not identify the cache key for method % in %", method.getName(), method.getDeclaringClass());

    }

    public static <T> String getCacheName(Class<T> tClass){
        Cachable cachableAnnotation = tClass.getAnnotation(Cachable.class);
        if(cachableAnnotation == null || StringUtils.isBlank(cachableAnnotation.cacheName())){
            return tClass.getCanonicalName();
        }
        return cachableAnnotation.cacheName();

    }

    public static <T> long getTTL(Class<T> tClass, long defaultTTL) {
        Cachable cachableAnnotation = tClass.getAnnotation(Cachable.class);
        if(cachableAnnotation == null){
            return defaultTTL;
        }
        return cachableAnnotation.defaultTtl();
    }

    public static boolean hasKeyNotNull(Map<String, CacheProvider>  map, String key){
        return (map.containsKey(key) && map.get(key) != null);

    }

}