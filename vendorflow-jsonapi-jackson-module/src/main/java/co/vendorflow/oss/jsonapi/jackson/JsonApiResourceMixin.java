package co.vendorflow.oss.jsonapi.jackson;

import com.fasterxml.jackson.databind.annotation.JsonTypeIdResolver;

@JsonTypeIdResolver(JsonApiTypeIdResolver.class)
public abstract class JsonApiResourceMixin {
}
