package org.fermented.dairy.microprofile.caching.openliberty.entity;

import lombok.Data;
import lombok.Builder;
import org.fermented.dairy.microprofile.caching.annotations.Cachable;
import org.fermented.dairy.microprofile.caching.annotations.CacheKey;

@Cachable
@Data
@Builder(toBuilder = true)
public class TestEntity {

    @CacheKey
    private Long id;

    private String name;

    private String surname;
}
