package co.vendorflow.oss.jsonapi.model.resource;

import lombok.Value;

@Value
public final class JsonApiResourceId {
    String type;
    String id;

    @Override
    public String toString() {
        return new StringBuilder(type).append('/').append(id).toString();
    }

    public static JsonApiResourceId of(String type, String id) {
        return new JsonApiResourceId(type, id);
    }
}
