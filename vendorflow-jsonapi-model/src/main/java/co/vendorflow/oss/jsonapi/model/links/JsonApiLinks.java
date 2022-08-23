package co.vendorflow.oss.jsonapi.model.links;

import static java.util.Collections.unmodifiableMap;
import static java.util.stream.Collectors.joining;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

public final class JsonApiLinks {

    Map<String, JsonApiLink> links = new LinkedHashMap<>();

    public JsonApiLinks add(JsonApiLink link) {
        get(link.getRel())
            .ifPresent(existing -> throwExisting(existing, link));

        links.put(link.getRel(), link);
        return this;
    }


    public JsonApiLink put(String name, JsonApiLink link) {
        if (link == null) {
            throw new IllegalArgumentException("link for " + name + " cannot be null");
        }

        return links.put(name, link);
    }


    public void putAll(Map<String, JsonApiLink> links) {
        this.links.putAll(links);
    }


    private void throwExisting(JsonApiLink existing, JsonApiLink fresh) {
        String message = "tried to add implicit rel " + fresh.getRel() + " to " + fresh.getHref()
            + ", but rel already points to " + existing.getHref();
        throw new IllegalStateException(message);
    }


    /**
     * Thanks to a loophole in the JSON:API spec, the key and the rel can be different if
     * a Link Object has a differing rel.
     */
    public Optional<JsonApiLink> get(String rel) {
        var hasRel = hasRel(rel);

        return Optional.ofNullable(links.get(rel))
            .filter(hasRel)
            .or(() -> links.values().stream().filter(hasRel).findAny());
    }


    public Map<String, JsonApiLink> getAll() {
        return unmodifiableMap(links);
    }


    static Predicate<JsonApiLink> hasRel(String rel) {
        return l -> l.getRel().equals(rel);
    }


    @Override
    public String toString() {
        var sb = new StringBuilder("JsonApiLinks[");
        sb.append(links.entrySet().stream()
            .map(l -> l.getKey() + "=" + l.getValue().getHref())
            .collect(joining(", ")));
        sb.append(']');
        return sb.toString();
    }
}
