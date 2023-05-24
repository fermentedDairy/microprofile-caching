package org.fermented.dairy.microprofile.caching.interceptors;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.Interceptor;
import jakarta.interceptor.InvocationContext;
import org.fermented.dairy.microprofile.caching.annotations.CacheRetrieve;
import org.fermented.dairy.microprofile.caching.interfaces.CacheProvider;
import lombok.extern.java.Log;

import java.util.Optional;
import java.util.logging.Level;

import static org.fermented.dairy.microprofile.caching.utils.CacheInterceptorUtils.getCacheKeyFromParams;
import static org.fermented.dairy.microprofile.caching.utils.CacheInterceptorUtils.getCacheName;
import static org.fermented.dairy.microprofile.caching.utils.CacheInterceptorUtils.getTTL;

@ApplicationScoped
@Interceptor
@CacheRetrieve
@Log
public class CachingRetrieveInterceptor extends AbstractCachingInterceptor{

    @AroundInvoke
    public Object doCacheRetrieve(InvocationContext invocationContext) throws Exception {

        Class<?> cacheClass = invocationContext.getMethod().getReturnType();
        CacheProvider cacheProvider = getProvider(cacheClass);
        Object cacheKey = getCacheKeyFromParams(invocationContext);
        String cacheName = getCacheName(cacheClass);
        Optional<?> cachedResultOptional = cacheProvider.getFromCache(cacheKey, cacheName, cacheClass);
        if(cachedResultOptional.isPresent()) {
            log.log(Level.FINE,
                    () -> String.format("Cache hit, cache name: %s, cache key: %s", cacheName, cacheKey));
            return cachedResultOptional.get();
        }
        log.log(Level.FINE,
                () -> String.format("Cache miss, cache name: %s, cache key: %s", cacheName, cacheKey));
        Object result = invocationContext.proceed();
        cacheProvider.putIntoCache(cacheKey, result, cacheName, getTTL(cacheClass, getDefaultTTL()));
        return result;
    }


}