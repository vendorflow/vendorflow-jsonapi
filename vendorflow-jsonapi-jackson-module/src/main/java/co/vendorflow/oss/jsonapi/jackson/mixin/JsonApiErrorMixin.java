package co.vendorflow.oss.jsonapi.jackson.mixin;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_EMPTY;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import co.vendorflow.oss.jsonapi.jackson.serdes.JsonApiErrorDeserializer;

@JsonInclude(NON_EMPTY)
@JsonDeserialize(using = JsonApiErrorDeserializer.class)
public interface JsonApiErrorMixin extends JsonApiJacksonMixin {
    @JsonIgnore
    Integer getStatus();

    @JsonProperty("status")
    String getStatusAsString();
}
