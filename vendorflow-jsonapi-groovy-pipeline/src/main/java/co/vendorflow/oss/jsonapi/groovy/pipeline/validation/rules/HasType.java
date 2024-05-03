package co.vendorflow.oss.jsonapi.groovy.pipeline.validation.rules;

import static java.util.Collections.emptyList;
import static lombok.AccessLevel.PACKAGE;

import java.util.Collection;
import java.util.List;

import javax.annotation.Nonnull;

import co.vendorflow.oss.jsonapi.groovy.pipeline.validation.JsonApiValidationRule;
import co.vendorflow.oss.jsonapi.model.error.JsonApiError;
import co.vendorflow.oss.jsonapi.model.resource.JsonApiResource;
import lombok.AllArgsConstructor;

@AllArgsConstructor(access = PACKAGE)
public final class HasType implements JsonApiValidationRule<JsonApiResource<?, ?>> {
    public static final String CODE = "jsonapi.resource.INVALID_TYPE";
    public static final String TITLE = "the resource had an invalid type";

    @Nonnull
    private final String expectedType;

    @Override
    public Collection<JsonApiError> validate(JsonApiResource<?, ?> domain) {
        if (expectedType.equals(domain.getType())) {
            return emptyList();
        }

        return List.of(new JsonApiError(409, CODE, TITLE));
    }

    @Override
    public String toString() {
        return "HasType[expectedType=" + expectedType + ']';
    }
}
