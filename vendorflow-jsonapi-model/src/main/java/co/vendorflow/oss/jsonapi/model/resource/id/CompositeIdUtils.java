package co.vendorflow.oss.jsonapi.model.resource.id;

import java.util.Arrays;

class CompositeIdUtils {
    static String[] stringifyComponents(Object[] components) {
        if (components instanceof String[]) {
            return (String[]) components;
        }

        var result = new String[components.length];
        for (int i = 0; i < components.length; i++) {
            if (components[i] == null) {
                throw new IllegalArgumentException("null component at index " + i + " in " + Arrays.toString(components));
            } else {
                result[i] = components[i].toString();
            }
        }
        return result;
    }

    static void validateComponents(int expectedLength, String[] components) {
        if (expectedLength != components.length) {
            throw new IllegalArgumentException("expected " + expectedLength + " ID components in " + Arrays.toString(components));
        }

        for (var component: components) {
            if (component == null) {
                throw new IllegalArgumentException("null ID component in " + Arrays.toString(components));
            }
            if (component.isBlank()) {
                throw new IllegalArgumentException("empty ID component in " + Arrays.toString(components));
            }
            if (component.contains(CompositeId.ID_COMPONENT_SEPARATOR)) {
                throw new IllegalArgumentException("the ID component separator '--' was contained in component '" + component + "' of " + Arrays.toString(components));
            }
        }
    }
}
