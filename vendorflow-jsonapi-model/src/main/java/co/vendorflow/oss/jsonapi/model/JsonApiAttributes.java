package co.vendorflow.oss.jsonapi.model;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Retention(RUNTIME)
@Target(TYPE)
public @interface JsonApiAttributes {
    String type();

    Class<?> meta() default MapStringObject.class;

    /**
     * Whether the generated JsonApiResource will permit null attributes in validation.
     */
    boolean nullable() default false;

    String resourceSuffix() default "Resource";

    String resourceTypeName() default "";


    static final class MapStringObject {}
}
