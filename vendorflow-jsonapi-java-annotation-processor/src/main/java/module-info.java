module co.vendorflow.oss.jsonapi.processor {
    requires java.compiler;

    requires co.vendorflow.oss.jsonapi.jackson;
    requires co.vendorflow.oss.jsonapi.processor.support;
    requires freemarker;

    opens co.vendorflow.oss.jsonapi.processor to freemarker;
    opens co.vendorflow.oss.jsonapi.processor.templates to freemarker;

    provides javax.annotation.processing.Processor with
        co.vendorflow.oss.jsonapi.processor.AttributesToDtoProcessor,
        co.vendorflow.oss.jsonapi.processor.TypeRegistrationProcessor;
}
