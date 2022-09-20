package co.vendorflow.oss.jsonapi.model.resource;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.codehaus.groovy.transform.GroovyASTTransformationClass;

@GroovyASTTransformationClass({
    "co.vendorflow.oss.jsonapi.groovy.transform.JsonApiTypeInlineClassesAstTransformation",
    "co.vendorflow.oss.jsonapi.groovy.transform.JsonApiTypeAstTransformation",
})
@Retention(RUNTIME)
@Target(TYPE)
public @interface JsonApiType {
    String value();
}
