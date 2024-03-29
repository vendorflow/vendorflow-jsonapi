package co.vendorflow.oss.jsonapi.groovy.pipeline;

import static co.vendorflow.oss.jsonapi.model.error.JsonApiErrors.notFound;
import static co.vendorflow.oss.jsonapi.model.request.JsonApiDataCollection.toDataCollection;
import static java.util.stream.Collectors.toList;
import static lombok.AccessLevel.PRIVATE;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;

import co.vendorflow.oss.jsonapi.groovy.pipeline.validation.JsonApiValidationRule;
import co.vendorflow.oss.jsonapi.model.error.JsonApiError;
import co.vendorflow.oss.jsonapi.model.request.JsonApiDataCollection;
import co.vendorflow.oss.jsonapi.model.request.JsonApiDataCollectionResourceIds;
import co.vendorflow.oss.jsonapi.model.request.JsonApiDataSingle;
import co.vendorflow.oss.jsonapi.model.request.JsonApiErrorDocument;
import co.vendorflow.oss.jsonapi.model.resource.JsonApiResource;
import co.vendorflow.oss.jsonapi.model.resource.JsonApiResourceId;
import io.vavr.CheckedConsumer;
import io.vavr.CheckedFunction1;
import io.vavr.control.Either;
import io.vavr.control.Option;
import io.vavr.control.Try;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = PRIVATE)
public final class JsonApiPipelineExtensions {

    /* Option<Domain> → 404 if empty *****************************************/

    /**
     * Converts an Option containing a domain object to Either(404 response, the domain object).
     *
     * @param <DOM> the type of the domain object
     * @param <D> a placeholder for the expected top-level data type
     * @param self the receiver
     * @return Left of a 404 error or Right of the domain object
     */
    public static<DOM, D>
    Either<JsonApiErrorDocument<D>, DOM>
    emptyTo404(
            Option<DOM> self
    ) {
        return self.toEither(() -> notFound().asDocument());
    }

    /**
     * Converts an Option containing a domain object to Either(404 response, the domain object).
     * If the domain object is missing, the error object will contain a resource ID in its meta
     * corresponding to the provided ID.
     *
     * @param <DOM> the type of the domain object
     * @param <D> a placeholder for the expected top-level data type
     * @param self the receiver
     * @param type the JSON:API type of the expected resource
     * @param id the ID of the expected resource
     * @return Left of a 404 error or Right of the domain object
     */
    public static<DOM, D>
    Either<JsonApiErrorDocument<D>, DOM>
    emptyTo404(
            Option<DOM> self, String type, Object id
    ) {
        return self.toEither(() -> notFound(JsonApiResourceId.of(type, id)).asDocument());
    }


    /**
     * Maps a Right maybe-containing a domain object to a Right of the domain object
     * if it is present, or a 404 response (as {@link #emptyTo404(Option)}) if it is absent.
     * If this Either is already Left (i.e., the pipeline has already produced an error),
     * it is unchanged.
     *
     * @param <DOM> the type of the domain object
     * @param <D> a placeholder for the expected top-level data type
     * @param self the receiver
     * @return a Left of the existing error document, or a Left of a 404 error
     *  if the Option is empty, or a Right of the unwrapped domain object
     */
    public static<DOM, D>
    Either<JsonApiErrorDocument<D>, DOM>
    flatMapEmptyTo404(
            Either<JsonApiErrorDocument<D>, Option<DOM>> self
    ) {
        return self.flatMap(JsonApiPipelineExtensions::emptyTo404);
    }


    /* Starting from Try *****************************************************/

    /**
     * Convert a {@code Try} of a domain object to an {@code Either}: A Failure
     * is converted to a Left of the exception as a {@link JsonApiError} in
     * a document, and a Success leaves the domain object in a Right.
     *
     * @param <DOM> the type of the domain object
     * @param <D> the expected data type of the response
     * @param self the receiver
     * @return a Left with an error document or a Right with the domain object
     */
    public static <DOM, D>
    Either<JsonApiErrorDocument<D>, DOM>
    failureToErrorDocument(
            Try<DOM> self
    ) {
        return self.toEither()
                .mapLeft(throwable -> JsonApiError.of(throwable).asDocument())
                ;
    }


    /**
     * Within an existing Either pipeline, flat-map a {@code Try} of a domain object
     * to a Left of an error document if it is a Failure or a Right of the domain object
     * if it is a Success.
     *
     * @param <DOM> the type of the domain object
     * @param <D> the expected data type of the response
     * @param self the receiver
     * @return if this Either is already a Left, then the same value unchanged; otherwise,
     *   a Left with an error document or a Right with the domain object
     */
    public static <DOM, D>
    Either<JsonApiErrorDocument<D>, DOM>
    flatMapFailureToErrorDocument(
            Either<JsonApiErrorDocument<D>, Try<DOM>> self
    ) {
        return self.flatMap(JsonApiPipelineExtensions::failureToErrorDocument);
    }



    /**
     * Attempt an operation on the current value; if it succeeds, proceed, but if it fails,
     * return the error in an error document.
     *
     * @param <T> the current pipeline value
     * @param <D> the expected data type of the response
     * @param self the receiver
     * @param consumer the operation to perform
     * @return this Right, if this is a Right and operation succeeds;
     *   otherwise, a Left of the original error document or of the operation's error
     */
    public static <T, D>
    Either<JsonApiErrorDocument<D>, T>
    flatMapTry(
            Either<JsonApiErrorDocument<D>, T> self,
            CheckedConsumer<T> consumer
    ) {
        return self.flatMap(value -> failureToErrorDocument(Try.success(value).andThenTry(consumer)));
    }


    /**
     * Attempt a function on the current value; if it succeeds, proceed, but if it fails,
     * return the error in an error document.
     *
     * @param <T> the current pipeline value
     * @param <U> the return type of the function
     * @param <D> the expected data type of the response
     * @param self the receiver
     * @param mapper the mapping function
     * @return if this is a Left of an error document, the same Left; otherwise,
     *   a Right of the function's return value or a Left if the function throws an exception
     */
    public static <T, U, D>
    Either<JsonApiErrorDocument<D>, U>
    flatMapTry(
            Either<JsonApiErrorDocument<D>, T> self,
            CheckedFunction1<T, U> mapper
    ) {
        return self.flatMap(value -> failureToErrorDocument(Try.success(value).mapTry(mapper)));
    }


    /* Validation ************************************************************/

    /**
     * Performs validation against a {@link JsonApiResource} object, such as
     * checking relationship validity.
     *
     * @param <REQ> the request resource type
     * @param <M> the request document meta type
     * @param <D> the response data type
     * @param self the receiver
     * @return a Left of an existing error document, or a Left if any validation rule
     *   produces an error, or a Right of the original request object
     */
    @SafeVarargs
    public static <REQ extends JsonApiResource<?, ?>, M, D>
    Either<JsonApiErrorDocument<D>, JsonApiDataSingle<REQ, M>>
    validateSingle(
            Either<JsonApiErrorDocument<D>, JsonApiDataSingle<REQ, M>> self,
            JsonApiValidationRule<? super REQ>... rules
    ) {
        return self.flatMap(single ->
            Arrays.stream(rules)
                .map(rule -> rule.validate(single.getData()))
                .flatMap(Collection::stream)
                .collect(JsonApiErrorDocument.<D> toJsonApiErrorDocument())
                .<Either<JsonApiErrorDocument<D>, JsonApiDataSingle<REQ, M>>> map(Either::left)
                .orElse(self)
        );
    }


    /**
     * Performs validation against a {@link JsonApiResource} object, such as
     * checking relationship validity.
     *
     * @param <REQ> the request resource type
     * @param <M> the request document meta type
     * @param <D> the response data type
     * @param self the single-valued data document
     * @return a Left if any validation rule produces an error, or a Right of the request document
     */
    @SafeVarargs
    public static <REQ extends JsonApiResource<?, ?>, M, D>
    Either<JsonApiErrorDocument<D>, JsonApiDataSingle<REQ, M>>
    validateSingle(
            JsonApiDataSingle<REQ, M> self,
            JsonApiValidationRule<? super REQ>... rules
    ) {
        return validateSingle(Either.right(self), rules);
    }


    /* Assembly **************************************************************/

    /**
     * Maps a Right containing a domain object to a {@link DomainAndResource} with
     * the domain object and its resource object.
     * @param <DOM> the type of the domain object
     * @param <R> the type of the resource
     * @param <D> the response data type
     * @param self the receiver
     * @param toResource a function mapping the domain object to a resource
     * @return a Right containing the (domain, resource) pair, or the same object if this was a Left
     */
    public static <DOM, D, A, RM, R extends JsonApiResource<A, RM>>
    Either<JsonApiErrorDocument<D>, DomainAndResource<DOM, R>>
    mapWithResource(
            Either<JsonApiErrorDocument<D>, DOM> self,
            Function<? super DOM, ? extends R> toResource
    ) {
        return self.map(d -> DomainAndResource.start(d, toResource));
    }


    /**
     * Maps a collection of domain objects to a List of {@link DomainAndResource}
     * with the domain objects and their resource objects.
     *
     * @param <DOM> the type of the domain objects
     * @param <R> the type of the resources
     * @param <D> the response data type
     * @param self the collection of domain objects
     * @param toResource a function mapping the domain object to a resource
     * @return a Right containing the List of (domain, resource) pairs
     */
    public static <DOM, D, A, RM, R extends JsonApiResource<A, RM>>
    Either<JsonApiErrorDocument<D>, List<DomainAndResource<DOM, R>>>
    mapWithResources(
            Collection<DOM> self,
            Function<? super DOM, ? extends R> toResource
    ) {
        return mapWithResources(Either.right(self), toResource);
    }


    /**
     * Maps a Right of a collection of domain objects to a List of {@link DomainAndResource}
     * with the domain objects and their resource objects.
     *
     * @param <DOM> the type of the domain objects
     * @param <R> the type of the resources
     * @param <D> the response data type
     * @param self the receiver
     * @param toResource a function mapping the domain object to a resource
     * @return a Right containing the List of (domain, resource) pairs,
     *   or a Left if this object was already a Left
     */
    public static <DOM, D, R extends JsonApiResource<?, ?>>
    Either<JsonApiErrorDocument<D>, List<DomainAndResource<DOM, R>>>
    mapWithResources(
            Either<JsonApiErrorDocument<D>, ? extends Collection<DOM>> self,
            Function<? super DOM, ? extends R> toResource
    ) {
        return self.map(ds -> ds.stream()
                .<DomainAndResource<DOM, R>> map(d -> DomainAndResource.start(d, toResource))
                .collect(toList())
        );
    }


    /**
     * Executes a consumer for each (domain, resource) pair. This method can be used,
     * for example, to enrich each resource with included resources or links.
     *
     * @param <DOM> the type of the domain objects
     * @param <R> the type of the resource objects
     * @param <D> the response data type
     * @param <L> the list type
     * @param self the receiver
     * @param consumer the action to take on each (domain, resource) pair
     * @return this Either, unchanged
     */
    public static <DOM, D, R extends JsonApiResource<?, ?>, L extends List<DomainAndResource<DOM, R>>>
    Either<JsonApiErrorDocument<D>, L>
    forEachDomainAndResource(
            Either<JsonApiErrorDocument<D>, L> self,
            BiConsumer<? super DOM, ? super R> consumer
    ) {
        self.forEach(dars -> dars.forEach(dar -> consumer.accept(dar.domain(), dar.resource())));
        return self;
    }

    /**
     * Maps a resource object to a {@link JsonApiDataSingle} containing it.
     *
     * @param <R> the resource's type
     * @param self the receiver
     * @return a Right of a document containing the resource,
     *   or a Left if this object was already a Left
     */
    public static <R extends JsonApiResource<?, ?>>
    Either<JsonApiErrorDocument<R>, JsonApiDataSingle<R, Map<String, Object>>>
    mapToDataSingle(
            Either<JsonApiErrorDocument<R>, R> self
    ) {
        return self.map(JsonApiDataSingle::of);
    }


    /**
     * Maps a {@code DomainAndResource} object to a {@link JsonApiDataSingle}
     * object containing the resource.
     *
     * @param <R> the resource's type
     * @param self the receiver
     * @return a Right of a document containing the resource,
     *   or a Left if this object was already a Left
     */
    public static <R extends JsonApiResource<?, ?>>
    Either<JsonApiErrorDocument<R>, JsonApiDataSingle<R, Map<String, Object>>>
    mapResourceToDataSingle(
            Either<JsonApiErrorDocument<R>, DomainAndResource<?, ? extends R>> self
    ) {
        return self.map(dar -> JsonApiDataSingle.of(dar.resource()));
    }


    /**
     * Maps a collection of resource objects to a {@link JsonApiDataCollection} containing it.
     *
     * @param <A> the resources' attribute type
     * @param <RM> the resources' meta type
     * @param <R> the resources' type
     * @param self the receiver
     * @return a Right of a document containing the resources,
     *   or a Left if this object was already a Left
     */
    public static <A, RM, R extends JsonApiResource<A, RM>>
    Either<JsonApiErrorDocument<R>, JsonApiDataCollection<A, RM, R, Map<String, Object>>>
    mapToDataCollection(
            Either<JsonApiErrorDocument<R>, ? extends Collection<R>> self
    ) {
        return self.map(JsonApiDataCollection::of);
    }


    /**
     * Maps a collection of {@code DomainAndResource} objects to a {@link JsonApiDataCollection}
     * object containing the resources.
     *
     * @param <A> the resources' attribute type
     * @param <RM> the resources' meta type
     * @param <R> the resources' type
     * @param self the receiver
     * @return a Right of a data collection containing the resources,
     *   or a Left if this object was already a Left
     */
    public static <A, RM, R extends JsonApiResource<A, RM>>
    Either<JsonApiErrorDocument<Collection<R>>, JsonApiDataCollection<A, RM, R, Map<String, Object>>>
    mapResourcesToDataCollection(
            Either<JsonApiErrorDocument<Collection<R>>, ? extends Collection<DomainAndResource<?, R>>> self
    ) {
        return self.map(dars -> dars.stream().map(DomainAndResource::resource).collect(toDataCollection()));
    }


    /* ID operations *********************************************************/

    /**
     * Maps a collection of business IDs to {@link JsonApiResourceId}s of the specified type.
     *
     * @param ids the business IDs
     * @param jsonApiType the JSON:API type name
     * @return the JSON:API resource IDs for the business records
     */
    public static
    List<JsonApiResourceId>
    toResourceIds(
            Collection<?> ids,
            String jsonApiType
    ) {
        return ids.stream()
            .map(Object::toString)
            .map(str -> JsonApiResourceId.of(jsonApiType, str))
            .collect(toList());
    }


    /**
     * Wraps a collection of {@link JsonApiResourceId} objects into a top-level document whose
     * {@code data} will contain only resource IDs.
     *
     * @param <M> the meta type of the document
     * @param ids the resource IDs
     * @return a document containing the resource IDs
     */
    public static <M>
    JsonApiDataCollectionResourceIds<M>
    toResourceIdDocument(
            Collection<JsonApiResourceId> ids
    ) {
        return JsonApiDataCollectionResourceIds.of(ids);
    }


    /**
     * Shorthand for
     * <pre> .toResourceIds(jsonApiType)
     * .toResourceIdDocument()</pre>
     */
    public static <M>
    JsonApiDataCollectionResourceIds<M>
    toResourceIdDocument(
            Collection<?> businessIds,
            String jsonApiType
    ) {
        return toResourceIdDocument(toResourceIds(businessIds, jsonApiType));
    }
}
