package co.vendorflow.oss.jsonapi.model.resource.id;

import static co.vendorflow.oss.jsonapi.model.resource.id.CompositeIdUtils.stringifyComponents;
import static co.vendorflow.oss.jsonapi.model.resource.id.CompositeIdUtils.validateComponents;

import java.util.List;

import lombok.Value;

@Value
public class CompositeId2 implements CompositeId {
    public final String _1;
    public final String _2;

    public String _1() { return _1; }
    public String _2() { return _2; }

    @Override
    public String toString() {
        return CompositeId.join(_1, _2);
    }

    @Override
    public List<String> components() {
        return List.of(_1, _2);
    }

    public static CompositeId2 of(Object... components) {
        return of(stringifyComponents(components));
    }

    public static CompositeId2 of(String... components) {
        validateComponents(2, components);
        return new CompositeId2(components[0], components[1]);
    }

    public static CompositeId2 parse(String joined) {
        return CompositeId2.of(joined.split(ID_COMPONENT_SEPARATOR));
    }
}
