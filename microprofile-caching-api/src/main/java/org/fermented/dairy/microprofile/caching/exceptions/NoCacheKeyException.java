package org.fermented.dairy.microprofile.caching.exceptions;

public class NoCacheKeyException extends CacheRuntimeException{

    public NoCacheKeyException(String message, Object... args) {
        super(message, args);
    }

}
