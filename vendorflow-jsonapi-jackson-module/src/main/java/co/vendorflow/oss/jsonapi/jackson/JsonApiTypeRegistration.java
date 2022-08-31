package co.vendorflow.oss.jsonapi.jackson;

import co.vendorflow.oss.jsonapi.model.resource.JsonApiResource;

public interface JsonApiTypeRegistration {
    String namespace();
    String typeName();
    Class<? extends JsonApiResource<?, ?>> typeClass();
}
