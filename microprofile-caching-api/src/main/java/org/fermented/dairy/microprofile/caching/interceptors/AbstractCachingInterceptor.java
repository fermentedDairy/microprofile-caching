package org.fermented.dairy.microprofile.caching.interceptors;

import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;
import jakarta.interceptor.InvocationContext;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.fermented.dairy.microprofile.caching.annotations.Cachable;
import org.fermented.dairy.microprofile.caching.annotations.CacheKey;
import org.fermented.dairy.microprofile.caching.exceptions.CacheRuntimeException;
import org.fermented.dairy.microprofile.caching.exceptions.NoCacheKeyException;
import org.fermented.dairy.microprofile.caching.interfaces.CacheProvider;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
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

    protected Object getCacheKeyFromParams(InvocationContext invocationContext, Class<?> cacheClass){
        Method method = invocationContext.getMethod();
        Object[] parameters =  invocationContext.getParameters();

        if(method.getParameterCount() == 1) { //There is only one param, use it as the cache key
            Object param = invocationContext.getParameters()[0];
            if (param.getClass().equals(cacheClass)) {//Get the cache key from inside the cached class
                return getCacheKeyFromObject(parameters[0], cacheClass);
            }
            return parameters[0];
        }

        for(int i = 0; i < method.getParameterCount(); i++){//Get the cache key from inside the cached class
            Parameter param = method.getParameters()[i];
            if(param.getType().equals(cacheClass)) {
                return getCacheKeyFromObject(invocationContext.getParameters()[i], cacheClass);
            }
        }

        Annotation[][] paramAnnotations = method.getParameterAnnotations();
        for (int outerIndex = 0; outerIndex < paramAnnotations.length; outerIndex++){
            Annotation[] annotationsForParam = paramAnnotations[outerIndex];
            for (int innerIndex = 0; innerIndex < annotationsForParam.length; innerIndex++){
                if (annotationsForParam[innerIndex].annotationType().equals(CacheKey.class)){
                    return parameters[outerIndex];
                }
            }
        }
        throw new NoCacheKeyException("Could not identify the cache key for method %s in %s", method.getName(), method.getDeclaringClass());

    }

    private Object getCacheKeyFromObject(Object parameter, Class<?> cacheClass) {

        Optional<Field> optionalAnnotatedField = Arrays.stream(cacheClass.getDeclaredFields()).filter(
                field -> field.isAnnotationPresent(CacheKey.class)
        ).findFirst();

        if (optionalAnnotatedField.isPresent())
        {
            Field field = optionalAnnotatedField.get();
            try {
                Method getterMethod = cacheClass.getDeclaredMethod("get" + StringUtils.capitalize(field.getName()));
                return getterMethod.invoke(parameter);
            } catch (NoSuchMethodException e) {
                throw new CacheRuntimeException(e, "Field %s does not have a getter named %s", field.getName(), "get" + StringUtils.capitalize(field.getName()));
            } catch (InvocationTargetException | IllegalAccessException e) {
                throw new CacheRuntimeException(e, "Could not invoke method %s", "get" + StringUtils.capitalize(field.getName()));
            }
        } else if (!cacheClass.getSuperclass().equals(Object.class)){
            return getCacheKeyFromObject(parameter, cacheClass.getSuperclass());
        }

        throw new NoCacheKeyException("No cache key found in %s (Missing CacheKey annotation)", parameter.getClass().getCanonicalName());

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
