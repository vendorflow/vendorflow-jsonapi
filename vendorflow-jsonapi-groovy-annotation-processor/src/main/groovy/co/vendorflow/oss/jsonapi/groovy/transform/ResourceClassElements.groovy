package co.vendorflow.oss.jsonapi.groovy.transform

import static java.lang.reflect.Modifier.FINAL
import static java.lang.reflect.Modifier.PUBLIC
import static org.codehaus.groovy.ast.ClassHelper.MAP_TYPE
import static org.codehaus.groovy.ast.ClassHelper.OBJECT_TYPE
import static org.codehaus.groovy.ast.ClassHelper.STRING_TYPE
import static org.codehaus.groovy.ast.ClassHelper.VOID_TYPE
import static org.codehaus.groovy.ast.ClassHelper.make
import static org.codehaus.groovy.ast.ClassHelper.makeWithoutCaching
import static org.codehaus.groovy.ast.tools.GeneralUtils.callSuperX
import static org.codehaus.groovy.ast.tools.GeneralUtils.param
import static org.codehaus.groovy.ast.tools.GeneralUtils.returnS
import static org.codehaus.groovy.ast.tools.GeneralUtils.stmt
import static org.codehaus.groovy.ast.tools.GeneralUtils.varX
import static org.codehaus.groovy.ast.tools.GenericsUtils.makeClassSafeWithGenerics

import javax.validation.constraints.NotNull

import org.codehaus.groovy.ast.AnnotatedNode
import org.codehaus.groovy.ast.ClassNode
import org.codehaus.groovy.ast.GenericsType
import org.codehaus.groovy.ast.MethodNode
import org.codehaus.groovy.ast.Parameter

import co.vendorflow.oss.jsonapi.model.resource.JsonApiResource
import groovy.transform.CompileStatic
import groovy.transform.stc.POJO

@CompileStatic
@POJO
final class ResourceClassElements {
    public static final ClassNode TYPE_NOT_NULL = make(NotNull)
    public static final ClassNode TYPE_JSON_API_RESOURCE = makeWithoutCaching(JsonApiResource)

    static ClassNode type_MapStringObject() {
        makeClassSafeWithGenerics(MAP_TYPE, STRING_TYPE.asGenericsType(), OBJECT_TYPE.asGenericsType())
    }


    static void maybeAddNotNull(AnnotatedNode e, boolean nullable) {
        if (!nullable) {
            e.addAnnotation(TYPE_NOT_NULL)
        }
    }


    static MethodNode buildGetter(String name, GenericsType type, boolean nullable = true) {
        var body = returnS(callSuperX(name))

        new MethodNode(
                name,
                PUBLIC | FINAL,
                type.type,
                Parameter.EMPTY_ARRAY,
                ClassNode.EMPTY_ARRAY,
                body
        )
            .tap { maybeAddNotNull(it, nullable) }
    }


    static MethodNode buildSetter(String name, GenericsType type, boolean nullable = true) {
        var valueParam = param(type.type, 'value')
            .tap { maybeAddNotNull(it, nullable) }

        var body = stmt(callSuperX(name, varX(valueParam)))

        new MethodNode(
                name,
                PUBLIC | FINAL,
                VOID_TYPE,
                [valueParam] as Parameter[],
                ClassNode.EMPTY_ARRAY,
                body
        )
    }


    static void addAccessors(ClassNode resource, GenericsType attr, boolean attributesNullable, GenericsType meta) {
        resource.tap {
            addMethod(buildGetter('getAttributes', attr, attributesNullable))
            addMethod(buildSetter('setAttributes', attr, attributesNullable))
            addMethod(buildGetter('getMeta', meta))
            addMethod(buildSetter('setMeta', meta))
        }
    }


    static void makeResource(ClassNode resource, GenericsType attr, boolean attributesNullable, GenericsType meta) {
        def sc = makeClassSafeWithGenerics(TYPE_JSON_API_RESOURCE, attr, meta)
        resource.superClass = sc
        resource.usingGenerics = true  // GROOVY-10763

        addAccessors(resource, attr, attributesNullable, meta)
    }
}