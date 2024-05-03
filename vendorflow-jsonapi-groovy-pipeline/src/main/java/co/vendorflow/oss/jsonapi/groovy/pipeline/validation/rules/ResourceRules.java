package co.vendorflow.oss.jsonapi.groovy.pipeline.validation.rules;

import co.vendorflow.oss.jsonapi.groovy.pipeline.validation.CompositeValidationRule;
import co.vendorflow.oss.jsonapi.groovy.pipeline.validation.JsonApiValidationRule;
import co.vendorflow.oss.jsonapi.model.resource.JsonApiResource;
import co.vendorflow.oss.jsonapi.model.resource.JsonApiResourceId;

public final class ResourceRules {
    /**
     * Require the resource to have a specific type.
     *
     * @param expectedType the expected type
     * @return a validation rule enforcing the type
     */
    public static HasType hasType(String expectedType) {
        return new HasType(expectedType);
    }

    /**
     * Require the resource to have a specific ID.
     *
     * @param expectedId the expected ID
     * @return a validation rule enforcing the ID
     */
    public static HasId hasId(String expectedId) {
        return new HasId(expectedId);
    }


    /**
     * Require the resource to have a specific JSON:API Resource Identifier (type and ID).
     *
     * @param expectedType the expected type
     * @param expectedId the expected ID
     * @return a validation rule enforcing the Resource Identifier
     */
    public static JsonApiValidationRule<JsonApiResource<?, ?>> hasResourceId(String expectedType, String expectedId) {
        return new CompositeValidationRule<>(hasType(expectedType), hasId(expectedId));
    }


    /**
     * Require the resource to have a specific JSON:API Resource Identifier (type and ID).
     *
     * @param expected the expected Resource Identifier
     * @return a validation rule enforcing the Resource Identifier
     */
    public static JsonApiValidationRule<JsonApiResource<?, ?>> hasResourceId(JsonApiResourceId expected) {
        return hasResourceId(expected.getType(), expected.getId());
    }
}
