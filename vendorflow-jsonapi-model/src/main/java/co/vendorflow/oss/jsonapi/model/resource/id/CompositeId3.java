package co.vendorflow.oss.jsonapi.model.resource.id;

import static co.vendorflow.oss.jsonapi.model.resource.id.CompositeIdUtils.stringifyComponents;
import static co.vendorflow.oss.jsonapi.model.resource.id.CompositeIdUtils.validateComponents;

import java.util.List;

import lombok.Value;

@Value
public class CompositeId3 implements CompositeId {
    public final String _1;
    public final String _2;
    public final String _3;

    public String _1() { return _1; }
    public String _2() { return _2; }
    public String _3() { return _3; }

    @Override
    public String toString() {
        return CompositeId.join(_1, _2, _3);
    }

    @Override
    public List<String> components() {
        return List.of(_1, _2, _3);
    }

    public static CompositeId3 of(Object... components) {
        return of(stringifyComponents(components));
    }

    public static CompositeId3 of(String... components) {
        validateComponents(3, components);
        return new CompositeId3(components[0], components[1], components[2]);
    }

    public static CompositeId3 parse(String joined) {
        return CompositeId3.of(joined.split(ID_COMPONENT_SEPARATOR));
    }
}
