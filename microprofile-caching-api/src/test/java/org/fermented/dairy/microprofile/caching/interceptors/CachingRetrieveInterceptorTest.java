package org.fermented.dairy.microprofile.caching.interceptors;

import jakarta.enterprise.inject.Instance;
import org.fermented.dairy.microprofile.caching.interfaces.CacheProvider;
import org.fermented.dairy.microprofile.caching.test.entities.TestCacheProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


@ExtendWith(MockitoExtension.class)
class CachingRetrieveInterceptorTest {

    @Mock
    Instance<CacheProvider> cacheProviders;

    TestCacheProvider testCacheProvider = new TestCacheProvider();

    @InjectMocks
    CachingRetrieveInterceptor cachingRetrieveInterceptor;

    @BeforeEach
    void beforeAll(){
        cachingRetrieveInterceptor.buildCacheProviderMap();
    }


}