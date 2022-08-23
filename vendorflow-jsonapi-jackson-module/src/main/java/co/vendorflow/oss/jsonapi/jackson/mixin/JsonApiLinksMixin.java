package co.vendorflow.oss.jsonapi.jackson.mixin;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_EMPTY;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import co.vendorflow.oss.jsonapi.jackson.serdes.JsonApiLinksDeserializer;
import co.vendorflow.oss.jsonapi.model.links.JsonApiLink;

@JsonDeserialize(using = JsonApiLinksDeserializer.class)
@JsonInclude(value = NON_EMPTY)
public interface JsonApiLinksMixin {
    @JsonValue
    Map<String, JsonApiLink> getAll();
}
