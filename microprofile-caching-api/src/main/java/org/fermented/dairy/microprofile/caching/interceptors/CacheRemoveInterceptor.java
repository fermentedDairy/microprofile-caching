package org.fermented.dairy.microprofile.caching.interceptors;

import jakarta.annotation.Priority;
import jakarta.enterprise.context.Dependent;
import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.Interceptor;
import jakarta.interceptor.InvocationContext;
import lombok.extern.java.Log;
import org.fermented.dairy.microprofile.caching.annotations.CacheRemove;

import java.util.Arrays;
import java.util.Optional;

@Dependent
@Interceptor
@Priority(Integer.MAX_VALUE)
@CacheRemove
@Log
public class CacheRemoveInterceptor extends AbstractCachingInterceptor{

    @AroundInvoke
    public Object doCacheRemove(InvocationContext invocationContext) throws Exception {
        Object result = invocationContext.proceed();

        return result;
    }
}
