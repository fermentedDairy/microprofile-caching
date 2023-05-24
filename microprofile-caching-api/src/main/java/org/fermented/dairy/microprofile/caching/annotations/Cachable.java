package org.fermented.dairy.microprofile.caching.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Provides the
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Cachable {

    /**
     * Default Time To Live (ttl) in milliseconds. The default is 5 minutes. This can be overridden by setting the config property named with the fully qualified class name suffixed with ".cache.ttl"
     * For example:
     *<pre>
     * @Cachable(ttl = 1000L)
     *public class Entity(){
     *}
     *</pre>
     * would first check if the property
     * <pre>
     *  Entity.class.getCanonicalName() + ".cache.ttl"
     * </pre>
     * before using 1000L. Any cache entries older than 1000 ms will either be removed or considered a cache miss
     */
    long defaultTtl() default 1000*60*60*5; //5 minutes

    /**
     * The name of the cache, if left blank, will use the class name
     */
    String cacheName() default "";

    /**
     * The name of the cache provider, if left blank uses the value defined in the org.fermented.diary.caching.defaultCache property
     */
    String cacheProvider() default "";
}
