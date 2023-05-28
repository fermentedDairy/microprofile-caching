package org.fermented.dairy.microprofile.caching.test.entities;

import org.fermented.dairy.microprofile.caching.annotations.CacheRetrieve;

public class CachingClass {

    @CacheRetrieve
    public CacheEntity getCachedSingleParam(Long Id){
        return CacheEntity.builder()
                .id(1L)
                .name("TestName")
                .surname("TestSurname")
                .build();
    }
}
