package org.fermented.dairy.microprofile.caching.exceptions;

public class CacheRuntimeException extends RuntimeException{
    public CacheRuntimeException(String message, Object... args){
        super(String.format(message, args));
    }

    public CacheRuntimeException(Throwable causedBy, String message, Object... args){
        super(String.format(message, args), causedBy);
    }
}
