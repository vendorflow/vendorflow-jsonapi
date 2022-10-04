package co.vendorflow.oss.jsonapi.groovy.pipeline.validation;

import static java.util.Arrays.asList;
import static lombok.AccessLevel.PRIVATE;

import lombok.NoArgsConstructor;

@NoArgsConstructor(access = PRIVATE)
public final class RelationshipRules {
    public static HasNoRelationship hasNoRelationship(String rel) {
        return new HasNoRelationship(rel);
    }

    public static HasSingleValuedRelationship hasSingleValuedRelationship(String rel, String... allowedTypes) {
        return new HasSingleValuedRelationship(rel, asList(allowedTypes));
    }

    public static RelationshipHasType relationshipHasType(String rel, String... allowedTypes) {
        if (allowedTypes.length == 0) {
            throw new IllegalArgumentException("at least one allowed type must be provided");
        }

        return new RelationshipHasType(rel, asList(allowedTypes));
    }
}
