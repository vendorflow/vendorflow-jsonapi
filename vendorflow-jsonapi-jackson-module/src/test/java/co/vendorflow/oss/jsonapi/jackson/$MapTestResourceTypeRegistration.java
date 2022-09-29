package co.vendorflow.oss.jsonapi.jackson;

import co.vendorflow.oss.jsonapi.model.resource.JsonApiResource;

// hand-written because the annotation processor depends on this module
public final class $MapTestResourceTypeRegistration implements JsonApiTypeRegistration {
  @Override public String namespace() { return ""; }
  @Override public String typeName() { return MapTestResource.TYPE; }
  @Override public Class<? extends JsonApiResource<?, ?>> typeClass() { return MapTestResource.class; }
}
