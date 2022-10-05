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
import co.vendorflow.oss.jsonapi.model.request.JsonApiDataCollection;
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


    /* Validation ************************************************************/

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


    /* Assembly **************************************************************/

    /**
     * Maps a Right containing a domain object to a {@link DomainAndResource} with
     * the domain object and its resource object.
     * @param <D> the type of the domain object
     * @param <R> the type of the resource
     * @param self the receiver
     * @param toResource a function mapping the domain object to a resource
     * @return a Right containing the (domain, resource) pair, or the same object if this was a Left
     */
    public static <D, R extends JsonApiResource<?, ?>>
    Either<JsonApiErrorDocument<R>, DomainAndResource<D, R>>
    mapWithResource(
            Either<JsonApiErrorDocument<R>, D> self,
            Function<? super D, ? extends R> toResource
    ) {
        return self.map(d -> DomainAndResource.start(d, toResource));
    }


    /**
     * Maps a collection of domain objects to a List of {@link DomainAndResource}
     * with the domain objects and their resource objects.
     *
     * @param <D> the type of the domain objects
     * @param <R> the type of the resources
     * @param self the collection of domain objects
     * @param toResource a function mapping the domain object to a resource
     * @return a Right containing the List of (domain, resource) pairs
     */
    public static <D, R extends JsonApiResource<?, ?>>
    Either<JsonApiErrorDocument<R>, List<DomainAndResource<D, R>>>
    mapWithResources(
            Collection<D> self,
            Function<? super D, ? extends R> toResource
    ) {
        return mapWithResources(Either.right(self), toResource);
    }


    /**
     * Maps a Right of a collection of domain objects to a List of {@link DomainAndResource}
     * with the domain objects and their resource objects.
     *
     * @param <D> the type of the domain objects
     * @param <R> the type of the resources
     * @param self the receiver
     * @param toResource a function mapping the domain object to a resource
     * @return a Right containing the List of (domain, resource) pairs,
     *   or a Left if this object was already a Left
     */
    public static <D, R extends JsonApiResource<?, ?>>
    Either<JsonApiErrorDocument<R>, List<DomainAndResource<D, R>>>
    mapWithResources(
            Either<JsonApiErrorDocument<R>, Collection<D>> self,
            Function<? super D, ? extends R> toResource
    ) {
        return self.map(ds -> ds.stream()
                .<DomainAndResource<D, R>> map(d -> DomainAndResource.start(d, toResource))
                .collect(toList())
        );
    }


    /**
     * Executes a consumer for each (domain, resource) pair. This method can be used,
     * for example, to enrich each resource with included resources or links.
     *
     * @param <D> the type of the domain objects
     * @param <R> the type of the resource objects
     * @param self the receiver
     * @param consumer the action to take on each (domain, resource) pair
     * @return this Either, unchanged
     */
    public static <D, R extends JsonApiResource<?, ?>>
    Either<JsonApiErrorDocument<R>, List<DomainAndResource<D, R>>>
    forEachDomainAndResource(
            Either<JsonApiErrorDocument<R>, List<DomainAndResource<D, R>>> self,
            BiConsumer<? super D, ? super R> consumer
    ) {
        self.forEach(dars -> dars.forEach(dar -> consumer.accept(dar.domain(), dar.resource())));
        return self;
    }


    /**
     * Maps a {@code DomainAndResource} object to a {@link JsonApiDataSingle}
     * object containing the resource.
     *
     * @param <A> the resource's attribute type
     * @param <RM> the resource's meta type
     * @param <R> the resource's type
     * @param self the receiver
     * @return a Right of a document containing the resource,
     *   or a Left if this object was already a Left
     */
    public static <A, RM, R extends JsonApiResource<A, RM>>
    Either<JsonApiErrorDocument<R>, JsonApiDataSingle<A, RM, Map<String, Object>, R>>
    mapResourceToDataSingle(
            Either<JsonApiErrorDocument<R>, DomainAndResource<?, R>> self
    ) {
        return self.map(dar -> JsonApiDataSingle.of(dar.resource()));
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
    Either<JsonApiErrorDocument<R>, JsonApiDataCollection<A, RM, Map<String, Object>, R>>
    mapResourcesToDataCollection(
            Either<JsonApiErrorDocument<R>, Collection<DomainAndResource<?, R>>> self
    ) {
        return self.map(dars -> dars.stream().map(DomainAndResource::resource).collect(toDataCollection()));
    }
}
