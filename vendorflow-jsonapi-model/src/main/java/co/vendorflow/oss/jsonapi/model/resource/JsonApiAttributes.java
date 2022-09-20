package co.vendorflow.oss.jsonapi.model.resource;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.codehaus.groovy.transform.GroovyASTTransformationClass;

@GroovyASTTransformationClass("co.vendorflow.oss.jsonapi.groovy.transform.JsonApiAttributesAstTransformation")
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
