package co.vendorflow.oss.jsonapi.jackson.mixin;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_EMPTY;

import com.fasterxml.jackson.annotation.JsonInclude;

import co.vendorflow.oss.jsonapi.model.links.JsonApiLinks;

public interface HasJsonApiLinksMixin {
    @JsonInclude(NON_EMPTY)
    JsonApiLinks getLinks();
}
