package org.fermented.dairy.microprofile.caching.test.entities;

import org.fermented.dairy.microprofile.caching.annotations.CacheKey;
import org.fermented.dairy.microprofile.caching.annotations.CacheRemove;
import org.fermented.dairy.microprofile.caching.annotations.CacheRetrieve;
import java.util.Optional;

public class CachingClass {

    @CacheRetrieve
    public CacheEntityWithProvider getCachedSingleParam(Long id){
        return CacheEntityWithProvider.builder()
                .id(id)
                .name("TestName")
                .surname("TestSurname")
                .build();
    }

    @CacheRetrieve(optionalWrappedClass = CacheEntityWithProvider.class)
    public Optional<CacheEntityWithProvider> getOptionalCachedSingleParam(Long id){
        return Optional.of(
                CacheEntityWithProvider.builder()
                        .id(id)
                        .name("TestName")
                        .surname("TestSurname")
                        .build()
        );
    }

    @CacheRetrieve(optionalWrappedClass = CacheEntityWithProvider.class)
    public Optional<CacheEntityWithProvider> getOptionalCachedMultiParam(String dummyParam, @CacheKey Long id){
        return Optional.of(
                CacheEntityWithProvider.builder()
                        .id(id)
                        .name("TestName")
                        .surname("TestSurname")
                        .build()
        );
    }

    @CacheRetrieve(optionalWrappedClass = CacheEntityWithProvider.class)
    public Optional<CacheEntityWithProvider> getOptionalCachedMultiParamMissingKey(String dummyParam, Long id){
        return Optional.of(
                CacheEntityWithProvider.builder()
                        .id(id)
                        .name("TestName")
                        .surname("TestSurname")
                        .build()
        );
    }

    @CacheRemove(cacheClass = CacheEntityWithProvider.class)
    public void removeCacheSingleParamNotObject(Long id)
    {

    }

    @CacheRemove(cacheClass = CacheEntityWithProvider.class)
    public void removeCacheMultiParamNotObjectAnnotated(String dummy, @CacheKey Long id)
    {

    }

    @CacheRemove(cacheClass = CacheEntityWithProvider.class)
    public void removeCacheMultiParamObjectAnnotated(Long id, CacheEntityWithProvider cacheEntityWithProvider)
    {

    }

    @CacheRemove(cacheClass = CacheEntityWithProvider.class)
    public void removeCacheSingleParamObject(CacheEntityWithProvider cacheEntityWithProvider)
    {

    }

    @CacheRemove(cacheClass = CacheEntityWithProviderChild.class)
    public void removeCacheSingleParamObjectChild(CacheEntityWithProviderChild cacheEntityWithProvider)
    {

    }

    @CacheRemove(cacheClass = CacheEntityWithProviderNoKey.class)
    public void removeCacheSingleParamObjectNoKey(CacheEntityWithProviderNoKey cacheEntityWithProvider)
    {

    }
}
