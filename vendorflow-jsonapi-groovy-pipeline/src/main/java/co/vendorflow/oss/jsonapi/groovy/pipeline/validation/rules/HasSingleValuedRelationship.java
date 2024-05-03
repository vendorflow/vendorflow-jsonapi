package co.vendorflow.oss.jsonapi.groovy.pipeline.validation.rules;

import static co.vendorflow.oss.jsonapi.model.error.JsonApiErrors.unprocessableEntity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import co.vendorflow.oss.jsonapi.model.error.JsonApiError;
import co.vendorflow.oss.jsonapi.model.resource.JsonApiRelationship;
import co.vendorflow.oss.jsonapi.model.resource.JsonApiResource;

public class HasSingleValuedRelationship extends RelationshipHasType {
    public static final String CODE = "jsonapi.relationship.RELATIONSHIP_SINGLE";
    public static final String TITLE = "a relationship should have had a single value";

    public HasSingleValuedRelationship(String rel, Collection<String> allowedTypes) {
        super(rel, allowedTypes);
    }

    @Override
    protected Collection<JsonApiError> doValidate(Optional<JsonApiRelationship> jarOpt, JsonApiResource<?, ?> domain) {
        var result = new ArrayList<JsonApiError>();
        result.addAll(super.doValidate(jarOpt, domain));

        jarOpt.ifPresentOrElse(
                jar -> validateSize(result, jar),
                () -> result.add(notPresent())
        );

        return result;
    }


    private void validateSize(List<JsonApiError> result, JsonApiRelationship jar) {
        long size = jar.size();
        if (size != 1) {
            result.add(unprocessableEntity(
                    CODE,
                    TITLE,
                    "the relationship " + rel + " should have had a single value but had " + size
            ));
        }
    }


    private JsonApiError notPresent() {
        return unprocessableEntity(
                CODE,
                TITLE,
                "the relationship " + rel + " was required but not present"
        );
    }

    @Override
    public String toString() {
        return "HasSingleValuedRelationship[rel=" + rel
                + ", allowedTypes=" + (allowedTypes.isEmpty() ? "*" : allowedTypes.toString())
                + ']';
    }

}
