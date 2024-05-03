package co.vendorflow.oss.jsonapi.groovy.pipeline.validation;

import static java.util.Collections.emptyList;
import static lombok.AccessLevel.PRIVATE;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

import co.vendorflow.oss.jsonapi.model.error.JsonApiError;
import co.vendorflow.oss.jsonapi.model.resource.JsonApiResource;
import lombok.AllArgsConstructor;

@AllArgsConstructor(access = PRIVATE)
public final class HasId implements JsonApiValidationRule<JsonApiResource<?, ?>> {
    public static final String CODE = "jsonapi.resource.INVALID_ID";
    public static final String TITLE = "the resource had an invalid ID";

    private final String expectedId;

    @Override
    public Collection<JsonApiError> validate(JsonApiResource<?, ?> domain) {
        if (Objects.equals(expectedId, domain.getId())) {
            return emptyList();
        }

        return List.of(new JsonApiError(400, CODE, TITLE));
    }

    @Override
    public String toString() {
        return "HasId[expectedId=" + expectedId + ']';
    }


    public static HasId hasId(String expectedId) {
        return new HasId(expectedId);
    }
}
