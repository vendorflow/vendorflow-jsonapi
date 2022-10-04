package co.vendorflow.oss.jsonapi.groovy.pipeline.validation;

import static java.util.Collections.emptyList;

import java.util.Collection;

import co.vendorflow.oss.jsonapi.model.error.JsonApiError;

/**
 * A validation rule to be applied to a domain object in the pipeline.
 *
 * @param <D> the type of the domain object
 */
public interface JsonApiValidationRule<D> {
    /**
     * Performs validation logic on the domain object.
     *
     * @param domain the object being validated
     * @return the validation errors found, or an empty collection if no errors were found
     */
    Collection<JsonApiError> validate(D domain);


    public static <D> JsonApiValidationRule<D> noOp() {
        return d -> emptyList();
    }
}
