package co.vendorflow.oss.jsonapi.groovy.pipeline.validation.rules;

import static java.util.Collections.emptyList;
import static lombok.AccessLevel.PACKAGE;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

import co.vendorflow.oss.jsonapi.groovy.pipeline.validation.JsonApiValidationRule;
import co.vendorflow.oss.jsonapi.model.error.JsonApiError;
import co.vendorflow.oss.jsonapi.model.resource.JsonApiResource;
import lombok.AllArgsConstructor;

@AllArgsConstructor(access = PACKAGE)
public final class HasId implements JsonApiValidationRule<JsonApiResource<?, ?>> {
    public static final String CODE = "jsonapi.resource.INVALID_ID";
    public static final String TITLE = "the resource had an invalid ID";

    private final String expectedId;

    @Override
    public Collection<JsonApiError> validate(JsonApiResource<?, ?> domain) {
        if (Objects.equals(expectedId, domain.getId())) {
            return emptyList();
        }

        return List.of(new JsonApiError(409, CODE, TITLE));
    }

    @Override
    public String toString() {
        return "HasId[expectedId=" + expectedId + ']';
    }


    @Deprecated(forRemoval = true)
    public static HasId hasId(String expectedId) {
        return new HasId(expectedId);
    }
}
