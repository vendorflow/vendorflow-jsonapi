package co.vendorflow.oss.jsonapi.jackson.mixin;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_EMPTY;
import static com.fasterxml.jackson.annotation.JsonTypeInfo.Id.NAME;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonTypeIdResolver;

import co.vendorflow.oss.jsonapi.jackson.JsonApiTypeIdResolver;

@JsonTypeInfo(use = NAME, property = "type", visible = true)
@JsonInclude(value = NON_EMPTY)
@JsonTypeIdResolver(JsonApiTypeIdResolver.class)
public interface JsonApiResourceMixin {
    @JsonIgnore
    String getType();
}
