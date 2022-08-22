package co.vendorflow.oss.jsonapi.jackson;

import com.fasterxml.jackson.databind.module.SimpleModule;

import co.vendorflow.oss.jsonapi.model.JsonApiResource;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JsonApiModule extends SimpleModule {
    private final String namespace;

    public JsonApiModule() {
        this("");
    }

    public JsonApiModule(String namespace) {
        this.namespace = namespace;

        setMixInAnnotation(JsonApiResource.class, JsonApiResourceMixin.class);
    }
}
