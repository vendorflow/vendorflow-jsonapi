package ${tr.packageName};

@javax.annotation.Generated("co.vendorflow.oss.jsonapi.processor.TypeRegistrationProcessor")
public final class ${tr.simpleName} implements co.vendorflow.oss.jsonapi.jackson.JsonApiTypeRegistration {
  @Override public String namespace() { return ""; }
  @Override public String typeName() { return "${tr.jsonApiType}"; }
  @Override public Class<? extends co.vendorflow.oss.jsonapi.model.JsonApiResource<?, ?>> typeClass() { return ${tr.resourceFqcn}.class ; }
}
