package co.vendorflow.oss.jsonapi.groovy.pipeline;

import static co.vendorflow.oss.jsonapi.model.error.JsonApiErrors.notFound;
import static lombok.AccessLevel.PRIVATE;

import java.util.Arrays;
import java.util.Collection;

import co.vendorflow.oss.jsonapi.groovy.pipeline.validation.JsonApiValidationRule;
import co.vendorflow.oss.jsonapi.model.request.JsonApiDataSingle;
import co.vendorflow.oss.jsonapi.model.request.JsonApiErrorDocument;
import co.vendorflow.oss.jsonapi.model.resource.JsonApiResource;
import co.vendorflow.oss.jsonapi.model.resource.JsonApiResourceId;
import io.vavr.control.Either;
import io.vavr.control.Option;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = PRIVATE)
public final class JsonApiPipelineExtensions {

    /* Option<Domain> → 404 if empty *****************************************/

    /**
     * Converts an Option containing a domain object to Either(404 response, the domain object).
     *
     * @param <D> the type of the domain object
     * @param <R> a placeholder for the expected top-level resource type
     * @param self the receiver
     * @return Left of a 404 error or Right of the domain object
     */
    public static<D, R>
    Either<JsonApiErrorDocument<R>, D>
    emptyTo404(
            Option<D> self
    ) {
        return self.toEither(() -> notFound().asDocument());
    }

    /**
     * Converts an Option containing a domain object to Either(404 response, the domain object).
     * If the domain object is missing, the error object will contain a resource ID in its meta
     * corresponding to the provided ID.
     *
     * @param <D> the type of the domain object
     * @param <R> a placeholder for the expected top-level resource type
     * @param self the receiver
     * @param type the JSON:API type of the expected resource
     * @param id the ID of the expected resource
     * @return Left of a 404 error or Right of the domain object
     */
    public static<D, R>
    Either<JsonApiErrorDocument<R>, D>
    emptyTo404(
            Option<D> self, String type, Object id
    ) {
        return self.toEither(() -> notFound(JsonApiResourceId.of(type, id)).asDocument());
    }


    /**
     * Maps a Right maybe-containing a domain object to a Right of the domain object
     * if it is present, or a 404 response (as {@link #emptyTo404(Option)}) if it is absent.
     * If this Either is already Left (i.e., the pipeline has already produced an error),
     * it is unchanged.
     *
     * @param <D> the type of the domain object
     * @param <R> a placeholder for the expected top-level resource type
     * @param self the receiver
     * @return a Left of the existing error document, or a Left of a 404 error
     *  if the Option is empty, or a Right of the unwrapped domain object
     */
    public static<D, R>
    Either<JsonApiErrorDocument<R>, D>
    flatMapEmptyTo404(
            Either<JsonApiErrorDocument<R>, Option<D>> self
    ) {
        return self.flatMap(JsonApiPipelineExtensions::emptyTo404);
    }


    /**
     * Performs validation against a {@link JsonApiResource} object, such as
     * checking relationship validity.
     *
     * @param <A> the resource's attribute type
     * @param <M> the resource's meta type
     * @param <REQ> the request resource type
     * @param <R> the response resource type
     * @param self the receiver
     * @return a Left of an existing error document, or a Left if any validation rule
     *   produces an error, or a Right of the original request object
     */
    @SafeVarargs
    public static <A, M, REQ extends JsonApiResource<A, M>, R>
    Either<JsonApiErrorDocument<R>, JsonApiDataSingle<A, M, ?, REQ>>
    validate(
            Either<JsonApiErrorDocument<R>, JsonApiDataSingle<A, M, ?, REQ>> self,
            JsonApiValidationRule<? super REQ>... rules
    ) {
        return self.flatMap(single ->
            Arrays.stream(rules)
                .map(rule -> rule.validate(single.getData()))
                .flatMap(Collection::stream)
                .collect(JsonApiErrorDocument.<R> toJsonApiErrorDocument())
                .<Either<JsonApiErrorDocument<R>, JsonApiDataSingle<A, M, ?, REQ>>> map(Either::left)
                .orElse(self)
        );
    }
}
