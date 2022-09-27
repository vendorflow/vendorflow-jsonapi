package co.vendorflow.oss.jsonapi.groovy.transform

import static co.vendorflow.oss.jsonapi.groovy.transform.ResourceClassElements.ANNOTATION_NOT_NULL
import static co.vendorflow.oss.jsonapi.groovy.transform.ResourceClassElements.addAccessors
import static co.vendorflow.oss.jsonapi.groovy.transform.ResourceClassElements.makeResource
import static co.vendorflow.oss.jsonapi.groovy.transform.ResourceClassElements.type_MapStringObject
import static co.vendorflow.oss.jsonapi.processor.support.ResourceClassInfo.resourceSimpleName
import static com.chrylis.gbt.transform.GbtUtils.getAnnotationParameterStringValue
import static com.chrylis.gbt.transform.GbtUtils.getAnnotationParameterValueOrDefault
import static java.lang.reflect.Modifier.FINAL
import static java.lang.reflect.Modifier.PUBLIC
import static org.codehaus.groovy.ast.ClassHelper.OBJECT_TYPE
import static org.codehaus.groovy.ast.ClassHelper.STRING_TYPE
import static org.codehaus.groovy.ast.ClassHelper.VOID_TYPE
import static org.codehaus.groovy.ast.ClassHelper.make
import static org.codehaus.groovy.ast.ClassHelper.makeWithoutCaching
import static org.codehaus.groovy.ast.tools.GeneralUtils.asX
import static org.codehaus.groovy.ast.tools.GeneralUtils.assignS
import static org.codehaus.groovy.ast.tools.GeneralUtils.block
import static org.codehaus.groovy.ast.tools.GeneralUtils.callSuperX
import static org.codehaus.groovy.ast.tools.GeneralUtils.constX
import static org.codehaus.groovy.ast.tools.GeneralUtils.ctorX
import static org.codehaus.groovy.ast.tools.GeneralUtils.declS
import static org.codehaus.groovy.ast.tools.GeneralUtils.localVarX
import static org.codehaus.groovy.ast.tools.GeneralUtils.param
import static org.codehaus.groovy.ast.tools.GeneralUtils.propX
import static org.codehaus.groovy.ast.tools.GeneralUtils.returnS
import static org.codehaus.groovy.ast.tools.GeneralUtils.stmt
import static org.codehaus.groovy.ast.tools.GeneralUtils.varX
import static org.codehaus.groovy.ast.tools.GenericsUtils.makeClassSafeWithGenerics
import static org.codehaus.groovy.control.CompilePhase.SEMANTIC_ANALYSIS
import static org.codehaus.groovy.control.messages.WarningMessage.LIKELY_ERRORS

import javax.validation.constraints.NotNull

import org.codehaus.groovy.ast.ASTNode
import org.codehaus.groovy.ast.AnnotatedNode
import org.codehaus.groovy.ast.AnnotationNode
import org.codehaus.groovy.ast.ClassNode
import org.codehaus.groovy.ast.GenericsType
import org.codehaus.groovy.ast.MethodNode
import org.codehaus.groovy.ast.Parameter
import org.codehaus.groovy.ast.expr.ClassExpression
import org.codehaus.groovy.control.SourceUnit
import org.codehaus.groovy.transform.AbstractASTTransformation
import org.codehaus.groovy.transform.GroovyASTTransformation

import co.vendorflow.oss.jsonapi.model.resource.JsonApiAttributes
import co.vendorflow.oss.jsonapi.model.resource.JsonApiResource
import co.vendorflow.oss.jsonapi.model.resource.JsonApiType
import co.vendorflow.oss.jsonapi.processor.support.ResourceClassInfo
import groovy.transform.CompileStatic
import groovy.transform.Generated
import groovy.transform.PackageScope
import groovy.transform.stc.POJO

@GroovyASTTransformation(phase = SEMANTIC_ANALYSIS)
@CompileStatic
@POJO
class JsonApiAttributesAstTransformation extends AbstractASTTransformation {
    private static final ClassNode TYPE_JAR = make(JsonApiResource)
    private static final ClassNode TYPE_JAT = make(JsonApiType)

    private static final ClassNode TYPE_COMPILE_STATIC = make(CompileStatic)
    private static final ClassNode TYPE_POJO = make(POJO)
    private static final ClassNode TYPE_GENERATED = make(Generated)


    private static final String METHOD_NAME_AS_RESOURCE = 'asResource'
    private static final Parameter PARAM_ID = param(OBJECT_TYPE, 'id').tap { addAnnotation ANNOTATION_NOT_NULL }


    @Override
    String getAnnotationName() { JsonApiAttributes.name }


    @Override
    void visit(ASTNode[] nodes, SourceUnit source) {
        init(nodes, source)
        AnnotationNode annotation = nodes[0] as AnnotationNode
        ClassNode attr = nodes[1] as ClassNode

        // extract the Resource class info from the annotation and the Attributes class
        def rci = buildResourceClassInfo(annotation, attr)
        if (source.AST.unit.getClass(rci.fqcn)) {
            source.errorCollector.addWarning(LIKELY_ERRORS, "class $rci.fqcn already exists", source.CST, source)
            return
        }

        // build the Resource class and add it to the compilation
        def resource = buildResourceClass(rci, attr.asGenericsType(), resolveMetaType(annotation))
        source.AST.addClass(resource)

        // if the Attributes class doesn't have an asResource(Object) method, insert one
        if (! attr.getMethod(METHOD_NAME_AS_RESOURCE, PARAM_ID)) {
            attr.addMethod buildAsResourceMethod(resource)
        }
    }


    static ResourceClassInfo buildResourceClassInfo(AnnotationNode annotation, ClassNode attr) {
        String jsonApiType = getAnnotationParameterStringValue(annotation, 'type')
        boolean nullable = getAnnotationParameterValueOrDefault(annotation, 'nullable', Boolean)

        String packageName = attr.packageName
        String explicitResourceTypeName = getAnnotationParameterValueOrDefault(annotation, 'resourceTypeName', String)
        String resourceSuffix = getAnnotationParameterValueOrDefault(annotation, 'resourceSuffix', String)
        String resourceSimpleName = resourceSimpleName(explicitResourceTypeName, attr.nameWithoutPackage, resourceSuffix)

        ResourceClassInfo rci = new ResourceClassInfo(
            attr.packageName,
            resourceSimpleName,
            jsonApiType,
            '<unused>',
            nullable
        )
    }


    static GenericsType resolveMetaType(AnnotationNode annotation) {
        def meta = annotation.getMember('meta')
        def type = (meta as ClassExpression)?.type

        return (type ?: type_MapStringObject())
            .asGenericsType()
    }


    static ClassNode buildResourceClass(ResourceClassInfo rci, GenericsType attr, GenericsType meta) {
        makeWithoutCaching(rci.fqcn).tap {
            modifiers |= (PUBLIC | FINAL)
            makeResource(it, attr, rci.attributesNullable, meta)

            addAnnotation(new AnnotationNode(TYPE_JAT).tap { setMember('value', constX(rci.jsonApiType)) })
            addAnnotation(TYPE_COMPILE_STATIC)
            addAnnotation(TYPE_POJO)
            addAnnotation(TYPE_GENERATED)
        }
    }


    static MethodNode buildAsResourceMethod(ClassNode resource) {
        resource = resource.plainNodeReference  // GROOVY-10763
        var resInstance = localVarX('resInstance', resource)

        var body = block(
            declS(resInstance, ctorX(resource)),
            assignS(propX(resInstance, 'id'), asX(STRING_TYPE, varX(PARAM_ID))),
            assignS(propX(resInstance, 'attributes'), varX('this')),
            returnS(resInstance)
        )

        new MethodNode(
            'asResource',
            PUBLIC | FINAL,
            resource,
            [PARAM_ID] as Parameter[],
            ClassNode.EMPTY_ARRAY,
            body
        ).tap { addAnnotation TYPE_GENERATED }
    }
}
