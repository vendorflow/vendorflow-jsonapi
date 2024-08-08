package co.vendorflow.oss.jsonapi.model.resource.id;

import static java.util.Arrays.stream;
import static java.util.stream.Collectors.joining;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

public interface CompositeId {
    static final String ID_COMPONENT_SEPARATOR = "--";

    @Override
    String toString();

    List<String> components();

    static CompositeId of(Object... components) {
        switch (components.length) {
        case 2:
            return CompositeId2.of(components);
        case 3:
            return CompositeId3.of(components);
        case 4:
            return CompositeId4.of(components);
        default:
            throw new IllegalArgumentException("must supply between 2 and 4 components, but supplied " + Arrays.toString(components));
        }
    }


    static <T> Function<T, CompositeId2> mapping(
            Function<? super T, ?> f1,
            Function<? super T, ?> f2
    ) {
        return t -> CompositeId2.of(f1.apply(t), f2.apply(t));
    }

    static <T> Function<T, CompositeId3> mapping(
            Function<? super T, ?> f1,
            Function<? super T, ?> f2,
            Function<? super T, ?> f3
    ) {
        return t -> CompositeId3.of(f1.apply(t), f2.apply(t), f3.apply(t));
    }

    static <T> Function<T, CompositeId4> mapping(
            Function<? super T, ?> f1,
            Function<? super T, ?> f2,
            Function<? super T, ?> f3,
            Function<? super T, ?> f4
    ) {
        return t -> CompositeId4.of(f1.apply(t), f2.apply(t), f3.apply(t), f4.apply(t));
    }


    static String join(String... components) {
        return stream(components).collect(joining(ID_COMPONENT_SEPARATOR));
    }
}
