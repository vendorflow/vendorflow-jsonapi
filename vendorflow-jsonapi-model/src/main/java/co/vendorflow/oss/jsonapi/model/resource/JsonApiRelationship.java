package co.vendorflow.oss.jsonapi.model.resource;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static lombok.AccessLevel.PACKAGE;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Stream;

import lombok.Getter;
import lombok.NonNull;

public final class JsonApiRelationship {
    /**
     * The name of this relationship. Will be hoisted to its property name in the
     * JSON:API <b>relationships object</b>.
     */
    @NonNull
    @Getter
    final String name;


    /**
     * Holds a list of resource IDs that should be linked but whose contents
     * will not be included in the response.
     */
    @Getter(PACKAGE)
    final Collection<JsonApiResourceId> linked = new ArrayList<>();


    /**
     * Holds a list of related resources that should be included in the response.
     * These resources are not inlined in the containing resource; instead, their
     * IDs are collected in the {@link #getData()}, and the contents are assembled
     * and deduplicated at the top level of the response.
     */
    @Getter(PACKAGE)
    final Collection<JsonApiResource<?, ?>> included = new ArrayList<>();


    /**
     * Provides "resource linkage" as described in JSON:API, consisting of a
     * JSON array of resource identifiers.
     *
     * @return the included resources' IDs
     */
    public Collection<JsonApiResourceId> getData() {
        return Stream.concat(
                included.stream().map(HasJsonApiResourceId::asResourceId),
                linked.stream()
        )
            .distinct()
            .collect(toList());
    }


    private JsonApiRelationship(String name) {
        this.name = name;
    }


    public JsonApiRelationship linkTo(Collection<JsonApiResourceId> resourceIds) {
        linked.addAll(resourceIds);
        return this;
    }

    public JsonApiRelationship linkTo(JsonApiResourceId... resourceIds) {
        return linkTo(asList(resourceIds));
    }


    public JsonApiRelationship include(Collection<? extends JsonApiResource<?, ?>> resources) {
        included.addAll(resources);
        return this;
    }

    public JsonApiRelationship include(JsonApiResource<?, ?>... resources) {
        return include(asList(resources));
    }


    public static JsonApiRelationship withoutSelfLink(String name) {
        return new JsonApiRelationship(name);
    }


    @Override
    public String toString() {
        return "JsonApiRelationship[" + name + ", data:" + (getData().size()) + ']';
    }
}
