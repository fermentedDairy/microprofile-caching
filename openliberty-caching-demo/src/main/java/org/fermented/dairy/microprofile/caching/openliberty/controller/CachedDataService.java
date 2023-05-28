package org.fermented.dairy.microprofile.caching.openliberty.controller;

import org.fermented.dairy.microprofile.caching.annotations.CacheKey;
import org.fermented.dairy.microprofile.caching.annotations.CacheRetrieve;
import org.fermented.dairy.microprofile.caching.openliberty.entity.TestEntity;

import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class CachedDataService {

    private static final Map<Long, TestEntity> ENTITIES_MAP = Stream.of(
            TestEntity.builder()
                    .id(1L)
                    .name("first")
                    .surname("1st")
            .build(),
            TestEntity.builder()
                    .id(2L)
                    .name("second")
                    .surname("2nd").build(),
            TestEntity.builder()
                    .id(3L)
                    .name("third")
                    .surname("3rd").build()
    ).collect(Collectors.toMap(
            TestEntity::getId,
            Function.identity()
    ));

    /**
     * return the entity mapped to ID 1 (cached)
     * @param id, the ID of the entity
     * @return
     */
    @CacheRetrieve
    public TestEntity getTestEntityCached(@CacheKey Long id) {
        if(!ENTITIES_MAP.containsKey(id) || ENTITIES_MAP.get(id) == null){
            return null;
        }
        return ENTITIES_MAP.get(id).toBuilder().build();
    }

    /**
     * return the entity mapped to ID 1 (cached)
     * @param id, the ID of the entity
     * @return
     */
    @CacheRetrieve
    public Optional<TestEntity> getTestEntityCachedOptional(@CacheKey Long id) {
        if(!ENTITIES_MAP.containsKey(id) || ENTITIES_MAP.get(id) == null){
            return Optional.empty();
        }
        return Optional.of(ENTITIES_MAP.get(id).toBuilder().build());
    }

    /**
     * Alter and return the entity mapped to the id
     * @param id, The ID of the entity
     * @param name, The new name provided to the entity
     * @return
     */
    public TestEntity alterUncached(Long id, String name){
        TestEntity entity = ENTITIES_MAP.get(id);
        entity.setName(name);
        return entity;
    }
}
