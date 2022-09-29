package co.vendorflow.oss.jsonapi.model.resource;

import lombok.Value;

@Value
public final class JsonApiResourceId implements HasJsonApiResourceId {
    String type;
    String id;

    @Override
    public JsonApiResourceId asResourceId() {
        return this;
    }

    @Override
    public String toString() {
        return new StringBuilder(type).append('/').append(id).toString();
    }


    public static JsonApiResourceId of(String type, Object id) {
        return new JsonApiResourceId(type, String.valueOf(id));
    }
}
