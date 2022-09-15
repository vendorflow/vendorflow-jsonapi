package ${tr.packageName};

import javax.annotation.Generated;

import co.vendorflow.oss.jsonapi.jackson.JsonApiTypeRegistration;
import co.vendorflow.oss.jsonapi.model.resource.JsonApiResource;

@Generated("co.vendorflow.oss.jsonapi.processor.TypeRegistrationProcessor")
public final class ${tr.simpleName} implements JsonApiTypeRegistration {
  @Override public String namespace() { return "${tr.namespace}"; }
  @Override public String typeName() { return "${tr.jsonApiType}"; }
  @Override public Class<? extends JsonApiResource<?, ?>> typeClass() { return ${tr.resourceFqcn}.class ; }
}
