module co.vendorflow.oss.jsonapi.groovy.spring.webmvc {
    requires static lombok;
    requires static org.apache.groovy;

    requires transitive co.vendorflow.oss.jsonapi.groovy.pipeline;
    requires transitive spring.web;

    exports co.vendorflow.oss.jsonapi.groovy.spring.webmvc;
}
