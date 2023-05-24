package org.fermented.dairy.microprofile.caching.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

@Target({ElementType.PARAMETER, ElementType.FIELD})
public @interface CacheKey {

}
