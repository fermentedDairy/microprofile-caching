module fermented.dairy.microprofile.caching {
    requires jakarta.interceptor;
    requires jakarta.cdi;
    requires jakarta.inject;
    requires jakarta.annotation;
    requires microprofile.config.api;
    requires org.apache.commons.lang3;
    requires lombok;
    requires java.logging;

    exports org.fermented.dairy.microprofile.caching.annotations;
    exports org.fermented.dairy.microprofile.caching.interfaces;
    exports org.fermented.dairy.microprofile.caching.exceptions;
    exports org.fermented.dairy.microprofile.caching.providers;

    opens org.fermented.dairy.microprofile.caching.interceptors to org.apache.commons.lang3;
}