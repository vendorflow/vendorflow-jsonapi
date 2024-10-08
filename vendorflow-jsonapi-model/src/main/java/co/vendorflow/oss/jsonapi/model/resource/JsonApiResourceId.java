package co.vendorflow.oss.jsonapi.model.resource;

import static java.util.stream.Collectors.toList;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;

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
        return type + '/' + id;
    }

    public static JsonApiResourceId parseSlashString(String slashy) {
        final var parts = slashy.split("/", 2);
        if (parts.length != 2) {
            throw new IllegalArgumentException("ID string " + slashy + " must consist of two parts");
        }
        return new JsonApiResourceId(parts[0], parts[1]);
    }

    public static JsonApiResourceId of(String type, Object id) {
        return new JsonApiResourceId(type, String.valueOf(id));
    }

    public static List<JsonApiResourceId> ofAll(String type, Collection<?> ids) {
        return ids.stream().map(id -> of(type, id)).collect(toList());
    }

    public static <T> List<JsonApiResourceId> ofAll(String type, Collection<T> entities, Function<? super T, ?> idExtractor) {
        return entities.stream().map(idExtractor).map(id -> of(type, id)).collect(toList());
    }
}
