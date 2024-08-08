package co.vendorflow.oss.jsonapi.model.resource.id;

import static co.vendorflow.oss.jsonapi.model.resource.id.CompositeIdUtils.stringifyComponents;
import static co.vendorflow.oss.jsonapi.model.resource.id.CompositeIdUtils.validateComponents;

import java.util.List;

import lombok.Value;

@Value
public class CompositeId4 implements CompositeId {
    public final String _1;
    public final String _2;
    public final String _3;
    public final String _4;

    public String _1() { return _1; }
    public String _2() { return _2; }
    public String _3() { return _3; }
    public String _4() { return _4; }

    @Override
    public String toString() {
        return CompositeId.join(_1, _2, _3, _4);
    }

    @Override
    public List<String> components() {
        return List.of(_1, _2, _3, _4);
    }

    public static CompositeId4 of(Object... components) {
        return of(stringifyComponents(components));
    }

    public static CompositeId4 of(String... components) {
        validateComponents(4, components);
        return new CompositeId4(components[0], components[1], components[2], components[3]);
    }

    public static CompositeId4 parse(String joined) {
        return CompositeId4.of(joined.split(ID_COMPONENT_SEPARATOR));
    }
}
