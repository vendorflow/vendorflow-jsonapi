package co.vendorflow.oss.jsonapi.jackson.mixin;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_EMPTY;
import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;
import static com.fasterxml.jackson.annotation.JsonTypeInfo.Id.NAME;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonTypeIdResolver;

import co.vendorflow.oss.jsonapi.jackson.JsonApiTypeIdResolver;
import co.vendorflow.oss.jsonapi.model.resource.JsonApiRelationships;

@JsonTypeInfo(use = NAME, property = "type", visible = true)
@JsonTypeIdResolver(JsonApiTypeIdResolver.class)
public interface JsonApiResourceMixin<A> extends JsonApiJacksonMixin {
    @JsonIgnore
    String getType();

    @JsonInclude(NON_NULL)
    A getAttributes();

    @JsonInclude(NON_EMPTY)
    JsonApiRelationships getRelationships();
}
