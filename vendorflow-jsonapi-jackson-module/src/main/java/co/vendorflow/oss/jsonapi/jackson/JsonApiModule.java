package co.vendorflow.oss.jsonapi.jackson;

import java.util.Map;

import com.fasterxml.jackson.databind.module.SimpleModule;
import com.google.common.collect.ImmutableMap;

import co.vendorflow.oss.jsonapi.jackson.mixin.HasJsonApiLinksMixin;
import co.vendorflow.oss.jsonapi.jackson.mixin.HasJsonApiMetaMixin;
import co.vendorflow.oss.jsonapi.jackson.mixin.JsonApiJacksonMixin;
import co.vendorflow.oss.jsonapi.jackson.mixin.JsonApiLinkMixin;
import co.vendorflow.oss.jsonapi.jackson.mixin.JsonApiLinkMixin.BareUriMixin;
import co.vendorflow.oss.jsonapi.jackson.mixin.JsonApiLinksMixin;
import co.vendorflow.oss.jsonapi.jackson.mixin.JsonApiRelationshipMixin;
import co.vendorflow.oss.jsonapi.jackson.mixin.JsonApiRelationshipsMixin;
import co.vendorflow.oss.jsonapi.jackson.mixin.JsonApiResourceMixin;
import co.vendorflow.oss.jsonapi.model.HasJsonApiMeta;
import co.vendorflow.oss.jsonapi.model.links.HasJsonApiLinks;
import co.vendorflow.oss.jsonapi.model.links.JsonApiLink;
import co.vendorflow.oss.jsonapi.model.links.JsonApiLinks;
import co.vendorflow.oss.jsonapi.model.resource.JsonApiRelationship;
import co.vendorflow.oss.jsonapi.model.resource.JsonApiRelationships;
import co.vendorflow.oss.jsonapi.model.resource.JsonApiResource;

public class JsonApiModule extends SimpleModule {
    private static final Map<Class<?>, Class<? extends JsonApiJacksonMixin>> MIXINS = ImmutableMap.<Class<?>, Class<? extends JsonApiJacksonMixin>> builder()
            // structural interfaces
            .put(HasJsonApiMeta.class, HasJsonApiMetaMixin.class)
            .put(HasJsonApiLinks.class, HasJsonApiLinksMixin.class)
            // entity objects
            .put(JsonApiResource.class, JsonApiResourceMixin.class)
            // link objects
            .put(JsonApiLink.class, JsonApiLinkMixin.class)
            .put(JsonApiLinks.class, JsonApiLinksMixin.class)
            .put(JsonApiLink.LinkUri.class, BareUriMixin.class)
            .put(JsonApiLink.LinkObject.class, JsonApiLinkMixin.LinkObjectMixin.class)
            // relationship objects
            .put(JsonApiRelationship.class, JsonApiRelationshipMixin.class)
            .put(JsonApiRelationships.class, JsonApiRelationshipsMixin.class)
            .build();


    @SuppressWarnings("unused")
    private final String namespace;

    public JsonApiModule() {
        this("");
    }

    public JsonApiModule(String namespace) {
        this.namespace = namespace;

        MIXINS.forEach(this::setMixInAnnotation);
    }
}
