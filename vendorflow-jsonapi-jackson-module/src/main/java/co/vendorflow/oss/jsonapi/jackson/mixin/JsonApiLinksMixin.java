package co.vendorflow.oss.jsonapi.jackson.mixin;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import co.vendorflow.oss.jsonapi.jackson.serdes.JsonApiLinksDeserializer;
import co.vendorflow.oss.jsonapi.model.links.JsonApiLink;

@JsonDeserialize(using = JsonApiLinksDeserializer.class)
public interface JsonApiLinksMixin {
    @JsonValue
    Map<String, JsonApiLink> getAll();
}
