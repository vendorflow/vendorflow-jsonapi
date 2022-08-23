package co.vendorflow.oss.jsonapi.processor;

@javax.annotation.Generated("co.vendorflow.oss.jsonapi.processor.AttributesToDtoProcessor")
public final class ${rci.registrationSimpleName} implements co.vendorflow.oss.jsonapi.jackson.JsonApiTypeRegistration {
  @Override public String namespace() { return ""; }
  @Override public String typeName() { return "${rci.jsonApiType}"; }
  @Override public Class<? extends co.vendorflow.oss.jsonapi.model.JsonApiResource<?, ?>> typeClass() { return ${rci.fqcn}.class ; }
}
