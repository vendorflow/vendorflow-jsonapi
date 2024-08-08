package co.vendorflow.oss.jsonapi.model.resource;

import static java.util.stream.Collectors.toList;

import java.util.Collection;
import java.util.List;

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

    public static List<JsonApiResourceId> ofAll(String type, Collection<?> ids) {
        return ids.stream().map(id -> of(type, id)).collect(toList());
    }
}
