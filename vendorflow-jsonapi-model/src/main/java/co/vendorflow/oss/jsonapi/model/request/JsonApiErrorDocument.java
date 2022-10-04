package co.vendorflow.oss.jsonapi.model.request;

import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import co.vendorflow.oss.jsonapi.model.error.JsonApiError;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JsonApiErrorDocument<R> extends JsonApiDocument<R, Map<String, Object>> {
    List<JsonApiError> errors = new ArrayList<>();

    public JsonApiErrorDocument<R> addError(JsonApiError error) {
        this.errors.add(error);
        return this;
    }


    public JsonApiErrorDocument<R> addErrors(Collection<JsonApiError> errors) {
        this.errors.addAll(errors);
        return this;
    }


    @Override
    public String toString() {
        return "JsonApiErrorDocument" + errors;
    }


    /**
     * Collects a potential stream of errors into an error document. If no errors are present,
     * then the {@code Optional} will be empty.
     *
     * @param <R> a placeholder for the response resource type
     * @return a present error document if the stream contained any errors, or an empty {@code Optional} if no errors were found
     */
    public static <R> Collector<JsonApiError, ?, Optional<JsonApiErrorDocument<R>>> toJsonApiErrorDocument() {
        return Collectors.collectingAndThen(
                toList(),
                errors ->
                        errors.isEmpty()
                        ? Optional.empty()
                        : Optional.of(new JsonApiErrorDocument<R>().addErrors(errors))
        );
    }
}
