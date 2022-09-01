package co.vendorflow.oss.jsonapi.jackson;

import com.fasterxml.jackson.databind.module.SimpleModule;

import co.vendorflow.oss.jsonapi.jackson.mixin.HasJsonApiLinksMixin;
import co.vendorflow.oss.jsonapi.jackson.mixin.HasJsonApiMetaMixin;
import co.vendorflow.oss.jsonapi.jackson.mixin.JsonApiLinkMixin;
import co.vendorflow.oss.jsonapi.jackson.mixin.JsonApiLinkMixin.BareUriMixin;
import co.vendorflow.oss.jsonapi.jackson.mixin.JsonApiLinksMixin;
import co.vendorflow.oss.jsonapi.jackson.mixin.JsonApiResourceMixin;
import co.vendorflow.oss.jsonapi.model.HasJsonApiMeta;
import co.vendorflow.oss.jsonapi.model.links.HasJsonApiLinks;
import co.vendorflow.oss.jsonapi.model.links.JsonApiLink;
import co.vendorflow.oss.jsonapi.model.links.JsonApiLinks;
import co.vendorflow.oss.jsonapi.model.resource.JsonApiResource;

public class JsonApiModule extends SimpleModule {
    @SuppressWarnings("unused")
    private final String namespace;

    public JsonApiModule() {
        this("");
    }

    public JsonApiModule(String namespace) {
        this.namespace = namespace;

        setMixInAnnotation(JsonApiResource.class, JsonApiResourceMixin.class);

        setMixInAnnotation(HasJsonApiMeta.class, HasJsonApiMetaMixin.class);

        setMixInAnnotation(JsonApiLink.class, JsonApiLinkMixin.class);
        setMixInAnnotation(JsonApiLink.LinkUri.class, BareUriMixin.class);
        setMixInAnnotation(JsonApiLink.LinkObject.class, JsonApiLinkMixin.LinkObjectMixin.class);
        setMixInAnnotation(JsonApiLinks.class, JsonApiLinksMixin.class);
        setMixInAnnotation(HasJsonApiLinks.class, HasJsonApiLinksMixin.class);
    }
}
