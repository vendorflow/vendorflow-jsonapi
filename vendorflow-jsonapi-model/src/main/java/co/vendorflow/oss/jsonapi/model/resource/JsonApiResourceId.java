package co.vendorflow.oss.jsonapi.model.resource;

import lombok.Value;

@Value
public final class JsonApiResourceId {
    String type;
    String id;

    public static JsonApiResourceId of(String type, String id) {
        return new JsonApiResourceId(type, id);
    }
}
