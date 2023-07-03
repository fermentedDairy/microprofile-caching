package org.fermented.dairy.microprofile.caching.interceptors;

import jakarta.annotation.Priority;
import jakarta.enterprise.context.Dependent;
import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.Interceptor;
import jakarta.interceptor.InvocationContext;
import org.fermented.dairy.microprofile.caching.annotations.CacheRetrieve;
import lombok.extern.java.Log;

@Dependent
@Interceptor
@Priority(Integer.MAX_VALUE)
@CacheRetrieve
@Log
public class CachingRetrieveInterceptor extends AbstractCachingInterceptor{

    @AroundInvoke
    public Object doCacheRetrieve(InvocationContext invocationContext) throws Exception {
        return invocationContext.proceed();

    }


}
