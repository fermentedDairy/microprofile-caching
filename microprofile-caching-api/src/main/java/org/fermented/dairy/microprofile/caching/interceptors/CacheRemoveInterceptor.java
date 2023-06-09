package org.fermented.dairy.microprofile.caching.interceptors;

import jakarta.annotation.Priority;
import jakarta.enterprise.context.Dependent;
import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.Interceptor;
import jakarta.interceptor.InvocationContext;
import lombok.extern.java.Log;
import org.fermented.dairy.microprofile.caching.annotations.CacheRemove;
import org.fermented.dairy.microprofile.caching.interfaces.CacheProvider;

@Dependent
@Interceptor
@Priority(Integer.MAX_VALUE)
@CacheRemove
@Log
public class CacheRemoveInterceptor extends AbstractCachingInterceptor{

    @AroundInvoke
    public Object doCacheRemove(InvocationContext invocationContext) throws Exception {
        Object result = invocationContext.proceed();

        CacheRemove cacheRemove = invocationContext.getMethod().getAnnotation(CacheRemove.class);
        CacheProvider cacheProvider = getProvider(cacheRemove.cacheClass());
        String cacheName = getCacheName(cacheRemove.cacheClass());
        Object cacheKey = getCacheKeyFromParams(invocationContext);
        cacheProvider.invalidateCacheEntry(cacheKey, cacheName);
        return result;
    }
}
