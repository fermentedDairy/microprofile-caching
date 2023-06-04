package org.fermented.dairy.microprofile.caching.interceptors;

import jakarta.interceptor.InvocationContext;
import org.fermented.dairy.microprofile.caching.interfaces.CacheProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CacheRemoveInterceptorTest {

    @Mock
    InvocationContext invocationContext;

    @Spy
    private Map<String, CacheProvider> cacheProviderMap =
            Map.of("TestCacheProvider", Mockito.mock(CacheProvider.class));

    @InjectMocks
    CacheRemoveInterceptor cacheRetrieveInterceptor;

    @DisplayName("when calling the remove method with a single parameter that isn't the cached class then remove")
    @Test
    void whenCallingTheRemoveMethodWithASingleParameterThatIsntTheCachedClassThenRemoveFromProvider() throws Exception {

        cacheRetrieveInterceptor.doCacheRemove(invocationContext);

        verify(invocationContext).proceed();
        verify(cacheProviderMap.get("TestCacheProvider")).invalidateCacheEntry(1L, "cacheName");
    }

    @DisplayName("when calling the remove method with a single parameter that is the cached class then remove")
    @Test
    void whenCallingTheRemoveMethodWithASingleParameterThatIsTheCachedClassThenRemoveFromProvider() throws Exception {
        cacheRetrieveInterceptor.doCacheRemove(invocationContext);

        verify(invocationContext).proceed();
        verify(cacheProviderMap.get("TestCacheProvider")).invalidateCacheEntry(1L, "cacheName");
    }

    @DisplayName("when calling the remove method with multiple parameters one of which is the cached class then remove")
    @Test
    void whenCallingTheRemoveMethodWithMultipleParametersOneOfWhichIsTheCachedClassThenRemove() throws Exception {
        cacheRetrieveInterceptor.doCacheRemove(invocationContext);

        verify(invocationContext).proceed();
        verify(cacheProviderMap.get("TestCacheProvider")).invalidateCacheEntry(1L, "cacheName");
    }

    @DisplayName("when calling the remove method with multiple parameters one of which is annotated as the CacheKey then remove")
    @Test
    void whenCallingTheRemoveMethodWithMultipleParametersOneOfWhichIsAnnotatedAsTheCacheKeyThenRemove() throws Exception {
        cacheRetrieveInterceptor.doCacheRemove(invocationContext);

        verify(invocationContext).proceed();
        verify(cacheProviderMap.get("TestCacheProvider")).invalidateCacheEntry(1L, "cacheName");
    }
}