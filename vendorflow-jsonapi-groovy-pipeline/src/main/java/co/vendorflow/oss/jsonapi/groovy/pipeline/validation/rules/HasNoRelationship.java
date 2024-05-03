package co.vendorflow.oss.jsonapi.groovy.pipeline.validation.rules;

import static co.vendorflow.oss.jsonapi.model.error.JsonApiErrors.unprocessableEntity;
import static java.util.Collections.emptyList;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import co.vendorflow.oss.jsonapi.model.error.JsonApiError;
import co.vendorflow.oss.jsonapi.model.resource.JsonApiRelationship;
import co.vendorflow.oss.jsonapi.model.resource.JsonApiResource;

public class HasNoRelationship extends RelationshipRule {
    public static final String CODE = "jsonapi.relationship.UNSUPPORTED_RELATIONSHIP";

    public HasNoRelationship(String rel) {
        super(rel);
    }

    @Override
    protected Collection<JsonApiError> doValidate(Optional<JsonApiRelationship> jar, JsonApiResource<?, ?> domain) {
        return jar
            .map(unsupported -> List.of(makeError()))
            .orElse(emptyList());
    }

    private JsonApiError makeError() {
        return unprocessableEntity(
                CODE,
                "an unsupported relationship was supplied",
                "the relationship " + rel + " is not supported"
        );
    }


    @Override
    public String toString() {
        return "HasNoRelationship[rel=" + rel + ']';
    }
}
