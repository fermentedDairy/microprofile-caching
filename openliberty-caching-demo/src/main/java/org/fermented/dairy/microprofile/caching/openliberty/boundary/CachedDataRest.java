package org.fermented.dairy.microprofile.caching.openliberty.boundary;

import jakarta.inject.Inject;
import jakarta.enterprise.inject.Instance;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;

import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.fermented.dairy.microprofile.caching.interfaces.CacheProvider;
import org.fermented.dairy.microprofile.caching.openliberty.controller.CachedDataService;
import org.fermented.dairy.microprofile.caching.openliberty.entity.TestEntity;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 *
 */
@Path("/v1/service")
@Produces(MediaType.APPLICATION_JSON)
public class CachedDataRest {

    @Inject
    CachedDataService cachedDataService;

    @Inject
    Instance<CacheProvider> cacheProvider;

    @GET
    @Path("/{id}")
    public TestEntity getData(@PathParam("id") @Positive Long id) {
        return cachedDataService.getTestEntityCached(id);
    }

    @GET
    @Path("optional/{id}")
    public TestEntity getOptionalData(@PathParam("id") @Positive Long id) {
        Optional<TestEntity> optionalTestEntity = cachedDataService.getTestEntityCachedOptional(id);
        if(optionalTestEntity.isEmpty())
        {
            throw new NotFoundException();
        }
        return cachedDataService.getTestEntityCached(id);
    }

    @GET
    @Path("caches")
    public Map<String, Collection<String>> getCacheNames(){
        return cacheProvider.stream()
                .collect(Collectors.toMap(
                        CacheProvider::getProviderName,
                        CacheProvider::getCacheNames
                ));
    }

    @GET
    @Path("caches/keys")
    public Map<String, Map<String, Collection<String>>> getCacheNamesAndKeys(){
        return cacheProvider.stream()
                .collect(Collectors.toMap(
                        CacheProvider::getProviderName,
                        provider ->
                            provider.getCacheNames().stream()
                                    .collect(Collectors.toMap(
                                            Function.identity(),
                                            cacheName -> provider.getKeys(cacheName).stream()
                                                    .map(Object::toString)
                                                    .toList()
                                    ))
                ));
    }

    @PUT
    @Path("/{id}/{name}")
    public TestEntity editBypassCache(@PathParam("id") @Positive Long id,
                              @PathParam("name") @NotBlank String name) {

        return cachedDataService.alterUncached(id, name);
    }
}
