module co.vendorflow.oss.jsonapi.jackson {
    requires static lombok;

    requires com.fasterxml.jackson.annotation;
    requires com.fasterxml.jackson.core;
    requires transitive com.fasterxml.jackson.databind;
    requires com.google.common;
    requires org.apache.commons.lang3;
    requires org.slf4j;
    requires transitive co.vendorflow.oss.jsonapi.model;

    exports co.vendorflow.oss.jsonapi.jackson;
    exports co.vendorflow.oss.jsonapi.jackson.mixin;
    exports co.vendorflow.oss.jsonapi.jackson.serdes;

    opens co.vendorflow.oss.jsonapi.jackson to com.fasterxml.jackson.databind;
    opens co.vendorflow.oss.jsonapi.jackson.mixin to com.fasterxml.jackson.databind;
    opens co.vendorflow.oss.jsonapi.jackson.serdes to com.fasterxml.jackson.databind;

    uses co.vendorflow.oss.jsonapi.jackson.JsonApiTypeRegistration;
}
