package co.vendorflow.oss.jsonapi.groovy.pipeline.validation;

import static java.util.stream.Collectors.toList;

import java.util.Arrays;
import java.util.Collection;

import co.vendorflow.oss.jsonapi.model.error.JsonApiError;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public final class CompositeValidationRule<D> implements JsonApiValidationRule<D> {
    private final Collection<JsonApiValidationRule<D>> rules;

    @SafeVarargs
    public CompositeValidationRule(JsonApiValidationRule<D>... rules) {
        this(Arrays.asList(rules));
    }

    @Override
    public Collection<JsonApiError> validate(D domain) {
        return rules.stream()
            .flatMap(rule -> rule.validate(domain).stream())
            .collect(toList());
    }
}
