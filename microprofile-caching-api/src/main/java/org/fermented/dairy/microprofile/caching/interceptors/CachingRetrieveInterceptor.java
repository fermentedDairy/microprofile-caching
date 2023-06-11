package org.fermented.dairy.microprofile.caching.interceptors;

import jakarta.annotation.Priority;
import jakarta.enterprise.context.Dependent;
import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.Interceptor;
import jakarta.interceptor.InvocationContext;
import org.fermented.dairy.microprofile.caching.annotations.CacheRetrieve;
import org.fermented.dairy.microprofile.caching.interfaces.CacheProvider;
import lombok.extern.java.Log;

import java.util.Optional;
import java.util.logging.Level;

@Dependent
@Interceptor
@Priority(Integer.MAX_VALUE)
@CacheRetrieve
@Log
public class CachingRetrieveInterceptor extends AbstractCachingInterceptor{

    @AroundInvoke
    public Object doCacheRetrieve(InvocationContext invocationContext) throws Exception {

        Class<?> cacheClass = invocationContext.getMethod().getReturnType();
        boolean isOptional = false;
        if (Optional.class.isAssignableFrom(cacheClass))
        {
            cacheClass = invocationContext.getMethod().getAnnotation(CacheRetrieve.class).optionalWrappedClass();
            isOptional = true;
        }
        CacheProvider cacheProvider = getProvider(cacheClass);
        Object cacheKey = getCacheKeyFromParams(invocationContext, cacheClass);
        String cacheName = getCacheName(cacheClass);
        Optional<?> cachedResultOptional = cacheProvider.getFromCache(cacheKey, cacheName, cacheClass);
        if(cachedResultOptional.isPresent()) {
            log.log(Level.FINE,
                    () -> String.format("Cache hit, cache name: %s, cache key: %s", cacheName, cacheKey));
            if(isOptional){
                return cachedResultOptional;
            }

            return cachedResultOptional.get();
        }
        log.log(Level.FINE,
                () -> String.format("Cache miss, cache name: %s, cache key: %s", cacheName, cacheKey));
        Object result = invocationContext.proceed();
        if(isOptional && ((Optional<?>)result).isPresent()) {
            cacheProvider.putIntoCache(cacheKey, ((Optional<?>)result).get(), cacheName, getTTL(cacheClass, getDefaultTTL()));
        } else if (result != null){
            cacheProvider.putIntoCache(cacheKey, result, cacheName, getTTL(cacheClass, getDefaultTTL()));
        }
        return result;
    }


}
