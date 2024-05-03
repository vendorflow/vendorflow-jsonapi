package co.vendorflow.oss.jsonapi.groovy.pipeline.validation.rules;

import static co.vendorflow.oss.jsonapi.model.error.JsonApiErrors.unprocessableEntity;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import co.vendorflow.oss.jsonapi.model.error.JsonApiError;
import co.vendorflow.oss.jsonapi.model.resource.JsonApiRelationship;
import co.vendorflow.oss.jsonapi.model.resource.JsonApiResource;
import co.vendorflow.oss.jsonapi.model.resource.JsonApiResourceId;

public class RelationshipHasType extends RelationshipRule {
    public static final String CODE = "jsonapi.relationship.INVALID_TYPE";
    public static final String TITLE = "a relationship had an invalid resource type";

    protected final Collection<String> allowedTypes;

    public RelationshipHasType(String rel, Collection<String> allowedTypes) {
        super(rel);
        this.allowedTypes = allowedTypes;
    }

    @Override
    protected Collection<JsonApiError> doValidate(Optional<JsonApiRelationship> jarOpt, JsonApiResource<?, ?> domain) {
        if (allowedTypes.isEmpty()) {
            return emptyList();
        }

        return jarOpt
            .map(this::collectDisallowed)
            .orElse(emptyList());
    }


    private Collection<JsonApiError> collectDisallowed(JsonApiRelationship jar) {
        var disallowed = jar.getData().stream()
                .map(JsonApiResourceId::getType)
                .filter(type -> !allowedTypes.contains(type))
                .collect(toList());

        return disallowed.isEmpty()
                ? Collections.emptyList()
                : List.of(unprocessableEntity(
                        CODE,
                        TITLE,
                        "the relationship " + rel + " must have type " + allowedTypes + " but had " + disallowed
                ));
    }


    @Override
    public String toString() {
        return "RelationshipHasType[rel=" + rel + ", allowedTypes=" + allowedTypes + ']';
    }

}
