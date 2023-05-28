package org.fermented.dairy.microprofile.caching.interceptors;

import jakarta.interceptor.InvocationContext;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.fermented.dairy.microprofile.caching.interfaces.CacheProvider;
import org.fermented.dairy.microprofile.caching.test.entities.CachingClass;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class CachingRetrieveInterceptorTest {

    @Mock
    InvocationContext invocationContext;

    @Spy
    private Map<String, CacheProvider> cacheProviderMap =
            Map.of("TestCacheProvider", Mockito.mock(CacheProvider.class));

    @InjectMocks
    CachingRetrieveInterceptor cachingRetrieveInterceptor;

    CachingClass cachingClass = new CachingClass();

    @BeforeEach
    void setConfig() throws IllegalAccessException {
        FieldUtils.writeField(cachingRetrieveInterceptor, "defaultProviderName", "LocalHashMapCache", true);
        FieldUtils.writeField(cachingRetrieveInterceptor, "defaultTTL", 300000L, true);
    }

    @DisplayName("given a key that is not in the cache then insert non null result into cache")
    @Test
    void givenAKeyThatIsNotInTheCacheThenInsertNonNullResultIntoCache() throws Exception {
        Method cachingMethod = Arrays.stream(CachingClass.class.getDeclaredMethods()).filter(method -> method.getName().equals("getCachedSingleParam")).findFirst().get();
        when(invocationContext.getMethod()).thenReturn(cachingMethod);
        when(invocationContext.getParameters()).thenReturn(new Object[]{1L});

        when(cacheProviderMap.get("TestCacheProvider").getFromCache(1L, "", CachingClass.class)).thenReturn(Optional.empty());
        when(invocationContext.proceed()).thenReturn(cachingClass.getCachedSingleParam(1L));
        Object result = cachingRetrieveInterceptor.doCacheRetrieve(invocationContext);

        verify(cacheProviderMap.get("TestCacheProvider")).putIntoCache(1L, result, "TestCacheName", 300000L);

    }
}