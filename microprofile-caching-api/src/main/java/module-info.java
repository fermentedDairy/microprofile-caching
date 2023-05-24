module fermented.dairy.microprofile.caching.api {
    requires jakarta.interceptor;
    requires jakarta.cdi;
    requires jakarta.inject;
    requires microprofile.config.api;
    requires jakarta.annotation;
    requires org.apache.commons.lang3;
    requires lombok;
    requires java.logging;


    exports org.fermented.dairy.microprofile.caching.annotations;
}