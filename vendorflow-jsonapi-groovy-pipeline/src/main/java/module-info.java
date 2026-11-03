module co.vendorflow.oss.jsonapi.groovy.pipeline {
    requires static jakarta.annotation;
    requires static lombok;
    requires static org.apache.groovy;

    requires transitive io.vavr;
    requires transitive co.vendorflow.oss.jsonapi.model;

    exports co.vendorflow.oss.jsonapi.groovy.pipeline;
    exports co.vendorflow.oss.jsonapi.groovy.pipeline.validation;
    exports co.vendorflow.oss.jsonapi.groovy.pipeline.validation.rules;
}
