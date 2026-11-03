module co.vendorflow.oss.jsonapi.processor.support {
    requires static lombok;

    requires org.apache.commons.lang3;
    requires transitive co.vendorflow.oss.jsonapi.model;

    exports co.vendorflow.oss.jsonapi.processor.support;
}
