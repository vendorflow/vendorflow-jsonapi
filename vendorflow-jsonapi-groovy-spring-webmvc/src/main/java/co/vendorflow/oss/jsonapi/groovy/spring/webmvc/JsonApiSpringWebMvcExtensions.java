package co.vendorflow.oss.jsonapi.groovy.spring.webmvc;

import static lombok.AccessLevel.PRIVATE;

import java.util.Map;

import org.springframework.http.ResponseEntity;

import co.vendorflow.oss.jsonapi.model.error.JsonApiError;
import co.vendorflow.oss.jsonapi.model.request.JsonApiDataSingle;
import co.vendorflow.oss.jsonapi.model.request.JsonApiDocument;
import co.vendorflow.oss.jsonapi.model.request.JsonApiErrorDocument;
import co.vendorflow.oss.jsonapi.model.resource.JsonApiResource;
import io.vavr.control.Either;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = PRIVATE)
public final class JsonApiSpringWebMvcExtensions {

    /**
     * Wrap an error document in a {@link ResponseEntity}. The HTTP status code
     * for the {@code ResponseEntity} will be set based on the first error present.
     *
     * @param <R> the resource that was otherwise expected to have been returned
     * @param doc the errors document
     * @return a {@code ResponseEntity} containing {@code errors}
     */
    public static <R>
    ResponseEntity<JsonApiDocument<R, ?>>
    toResponseEntity(
            JsonApiErrorDocument<R> doc
    ) {
        var errors = doc.getErrors();
        if (errors.isEmpty()) {
            throw new IllegalArgumentException("the JsonApiErrorDocument contained no errors");
        }

        var status = errors.get(0).getStatus(); // FIXME; should be most specific ancestor of all errors
        return ResponseEntity.status(status).body(doc);
    }


    /**
     * Wrap a single error into an error document and then into a {@link ResponseEntity}.
     *
     * @see #toResponseEntity(JsonApiErrorDocument)
     * @param <R> the resource that was otherwise expected to have been returned
     * @param e an error object
     * @return a {@code ResponseEntity} containing the error
     */
    public static <R>
    ResponseEntity<JsonApiDocument<R, ?>>
    toResponseEntity(
            JsonApiError e
    ) {
        return toResponseEntity(e.asDocument());
    }


    /**
     * Fold a possible Left error document or Right with a response document to a
     * Spring MVC {@link ResponseEntity}.
     *
     * @param <R> the top-level resource type of the response
     * @param self the receiver
     * @return a {@code ResponseEntity} containing either {@code errors} or {@code data}
     */
    public static <R>
    ResponseEntity<JsonApiDocument<R, ?>>
    foldToResponseEntity(
            Either<JsonApiErrorDocument<R>, JsonApiDocument<R, ?>> self
    ) {
        return self.fold(
                JsonApiSpringWebMvcExtensions::toResponseEntity,
                ResponseEntity::ok
        );
    }


    /**
     * Fold a possible Left error document or a Right that is already a {@link ResponseEntity}.
     * This can be used if the pipeline has already produced a complete entity, in particular if
     * it has programmatically set headers.
     *
     * @param <R> the top-level resource type of the response
     * @param self the receiver
     * @return the {@code ResponseEntity} in the Right or a {@code ResponseEntity} containing the {@code errors} in the Left
     */
    public static <R>
    ResponseEntity<JsonApiDocument<R, ?>>
    foldErrorsAndResponseEntity(
            Either<JsonApiErrorDocument<R>, ResponseEntity<JsonApiDocument<R, ?>>> self
    ) {
        return self.getOrElseGet(JsonApiSpringWebMvcExtensions::toResponseEntity);
    }


    private static <A, RM, R extends JsonApiResource<A, RM>>
    HeadersAndBody<JsonApiDataSingle<A, RM, R, Map<String, Object>>> toDataSingleWithHeaders(
            R self
    ) {
        return HeadersAndBody.of(JsonApiDataSingle.of(self));
    }


    /**
     * Map a Resource object to a {@link HeadersAndBody} with a {@link JsonApiDataSingle} body
     * containing the resource.
     *
     * @param <A> the attribute type of the resource
     * @param <RM> the meta type of the resource
     * @param <R> the resource type
     * @param self the receiver
     * @return if this is a Resource, a {@code Right} of the Resource wrapped into a
     *   {@code HeadersAndBody<JsonApiDataSingle<R>>}; if this is a {@code Left}, returned unchanged
     */
    public static <A, RM, R extends JsonApiResource<A, RM>>
    Either<JsonApiErrorDocument<R>, HeadersAndBody<JsonApiDataSingle<A, RM, R, Map<String, Object>>>>
    mapToDataSingleWithHeaders(
            Either<JsonApiErrorDocument<R>, R> self
    ) {
        return self.map(JsonApiSpringWebMvcExtensions::toDataSingleWithHeaders);
    }
}
