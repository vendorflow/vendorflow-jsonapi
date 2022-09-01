package co.vendorflow.oss.jsonapi.jackson.mixin;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_EMPTY;

import com.fasterxml.jackson.annotation.JsonInclude;

public interface HasJsonApiMetaMixin<M> {
    @JsonInclude(NON_EMPTY)
    M getMeta();
}
