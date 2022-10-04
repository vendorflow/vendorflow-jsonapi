package co.vendorflow.oss.jsonapi.groovy.pipeline.validation;

import java.util.Collection;
import java.util.Optional;

import co.vendorflow.oss.jsonapi.model.error.JsonApiError;
import co.vendorflow.oss.jsonapi.model.resource.JsonApiRelationship;
import co.vendorflow.oss.jsonapi.model.resource.JsonApiResource;

abstract class RelationshipRule implements JsonApiValidationRule<JsonApiResource<?, ?>> {

    protected final String rel;

    protected RelationshipRule(String rel) {
        this.rel = rel;
    }

    @Override
    public final Collection<JsonApiError> validate(JsonApiResource<?, ?> domain) {
        var jar = domain.getRelationships().get(rel);
        return doValidate(jar, domain);
    }


    protected abstract Collection<JsonApiError> doValidate(Optional<JsonApiRelationship> jarOpt, JsonApiResource<?, ?> domain);
}
