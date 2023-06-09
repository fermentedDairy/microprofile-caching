package org.fermented.dairy.microprofile.caching.interceptors;

import jakarta.interceptor.InvocationContext;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.fermented.dairy.microprofile.caching.interfaces.CacheProvider;
import org.fermented.dairy.microprofile.caching.test.entities.CachingClass;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Map;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CacheRemoveInterceptorTest {

    @Mock
    InvocationContext invocationContext;

    @Spy
    private Map<String, CacheProvider> cacheProviderMap =
            Map.of("TestCacheProvider", Mockito.mock(CacheProvider.class));

    @InjectMocks
    CacheRemoveInterceptor cacheRemoveInterceptor;

    @BeforeEach
    void setConfig() throws IllegalAccessException {
        FieldUtils.writeField(cacheRemoveInterceptor, "defaultProviderName", "LocalHashMapCache", true);
        FieldUtils.writeField(cacheRemoveInterceptor, "defaultTTL", 300000L, true);
    }

    @DisplayName("when calling the remove method with a single parameter that isn't the cached class then remove")
    @Test
    void whenCallingTheRemoveMethodWithASingleParameterThatIsntTheCachedClassThenRemoveFromProvider() throws Exception {

        Method cachingMethod = Arrays.stream(CachingClass.class.getDeclaredMethods()).filter(method -> method.getName().equals("removeCacheSingleParamNotObject")).findFirst().get();
        when(invocationContext.getMethod()).thenReturn(cachingMethod);
        when(invocationContext.getParameters()).thenReturn(new Object[]{1L});

        cacheRemoveInterceptor.doCacheRemove(invocationContext);

        verify(invocationContext).proceed();
        verify(cacheProviderMap.get("TestCacheProvider")).invalidateCacheEntry(1L, "TestCacheName");
    }

    @DisplayName("when calling the remove method with a single parameter that is the cached class then remove")
    @Test
    void whenCallingTheRemoveMethodWithASingleParameterThatIsTheCachedClassThenRemoveFromProvider() throws Exception {
        cacheRemoveInterceptor.doCacheRemove(invocationContext);

        verify(invocationContext).proceed();
        verify(cacheProviderMap.get("TestCacheProvider")).invalidateCacheEntry(1L, "cacheName");
    }

    @DisplayName("when calling the remove method with multiple parameters one of which is the cached class then remove")
    @Test
    void whenCallingTheRemoveMethodWithMultipleParametersOneOfWhichIsTheCachedClassThenRemove() throws Exception {
        cacheRemoveInterceptor.doCacheRemove(invocationContext);

        verify(invocationContext).proceed();
        verify(cacheProviderMap.get("TestCacheProvider")).invalidateCacheEntry(1L, "cacheName");
    }

    @DisplayName("when calling the remove method with multiple parameters one of which is annotated as the CacheKey then remove")
    @Test
    void whenCallingTheRemoveMethodWithMultipleParametersOneOfWhichIsAnnotatedAsTheCacheKeyThenRemove() throws Exception {
        cacheRemoveInterceptor.doCacheRemove(invocationContext);

        verify(invocationContext).proceed();
        verify(cacheProviderMap.get("TestCacheProvider")).invalidateCacheEntry(1L, "cacheName");
    }
}