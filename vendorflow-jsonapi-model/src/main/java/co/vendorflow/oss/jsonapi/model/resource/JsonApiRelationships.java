package co.vendorflow.oss.jsonapi.model.resource;

import static java.util.Collections.unmodifiableSet;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Stream;

import lombok.Getter;

@Getter
public class JsonApiRelationships {
    final Map<String, JsonApiRelationship> values = new LinkedHashMap<>();

    public boolean isEmpty() {
        return values.isEmpty();
    }


    public JsonApiRelationships add(JsonApiRelationship rel) {
        if (values.containsKey(rel.getName())) {
            throw new IllegalStateException("rel " + rel.getName() + " is already present");
        }

        values.put(rel.getName(), rel);
        return this;
    }



    public JsonApiRelationships add(String name, Consumer<JsonApiRelationship> builder) {
        var rel = JsonApiRelationship.withoutSelfLink(name);
        builder.accept(rel);
        this.add(rel);
        return this;
    }


    public Collection<String> keys() {
        return unmodifiableSet(values.keySet());
    }


    public Optional<JsonApiRelationship> get(String rel) {
        return Optional.ofNullable(values.get(rel));
    }


    Stream<JsonApiResource<?, ?>> allIncluded() {
        return values.values().stream()
                .map(JsonApiRelationship::getIncluded)
                .flatMap(Collection::stream);
    }


    @Override
    public String toString() {
        return "JsonApiRelationships" + values.keySet();
    }
}
