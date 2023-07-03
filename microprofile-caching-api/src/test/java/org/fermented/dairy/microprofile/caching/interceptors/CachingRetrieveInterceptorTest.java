package org.fermented.dairy.microprofile.caching.interceptors;

import jakarta.interceptor.InvocationContext;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.fermented.dairy.microprofile.caching.interfaces.CacheProvider;
import org.fermented.dairy.microprofile.caching.test.entities.CachingClass;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Map;


@ExtendWith(MockitoExtension.class)
class CachingRetrieveInterceptorTest {

    @Mock
    private InvocationContext invocationContext;

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
}