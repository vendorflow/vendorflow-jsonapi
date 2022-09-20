package co.vendorflow.oss.jsonapi.groovy.transform

import static co.vendorflow.oss.jsonapi.groovy.transform.ResourceClassElements.addAccessors
import static co.vendorflow.oss.jsonapi.groovy.transform.ResourceClassElements.makeResource
import static co.vendorflow.oss.jsonapi.groovy.transform.ResourceClassElements.type_MapStringObject
import static co.vendorflow.oss.jsonapi.processor.support.ResourceClassInfo.ATTR_SUFFIX
import static java.lang.reflect.Modifier.isStatic
import static org.codehaus.groovy.ast.ClassHelper.OBJECT_TYPE
import static org.codehaus.groovy.ast.ClassHelper.make
import static org.codehaus.groovy.ast.tools.GenericsUtils.makeClassSafeWithGenerics
import static org.codehaus.groovy.control.CompilePhase.SEMANTIC_ANALYSIS

import java.lang.reflect.Modifier

import org.codehaus.groovy.ast.ASTNode
import org.codehaus.groovy.ast.AnnotationNode
import org.codehaus.groovy.ast.ClassHelper
import org.codehaus.groovy.ast.ClassNode
import org.codehaus.groovy.ast.tools.GenericsUtils
import org.codehaus.groovy.control.SourceUnit
import org.codehaus.groovy.transform.AbstractASTTransformation
import org.codehaus.groovy.transform.GroovyASTTransformation

import co.vendorflow.oss.jsonapi.model.resource.JsonApiResource
import co.vendorflow.oss.jsonapi.model.resource.JsonApiType
import co.vendorflow.oss.jsonapi.processor.support.ResourceClassInfo
import groovy.transform.CompileStatic
import groovy.transform.stc.POJO

@GroovyASTTransformation(phase = SEMANTIC_ANALYSIS)
@CompileStatic
@POJO
class JsonApiTypeInlineClassesAstTransformation extends AbstractASTTransformation {
    private static final ClassNode TYPE_JAR = make(JsonApiResource)

    @Override
    String getAnnotationName() { JsonApiType.name }


    @Override
    void visit(ASTNode[] nodes, SourceUnit source) {
        init(nodes, source)
        AnnotationNode jat = nodes[0] as AnnotationNode
        ClassNode resource = nodes[1] as ClassNode

        if (existingSuperClass(resource)) {
            return
        }

        def attr = findNestedClass(resource, ATTR_SUFFIX)
        if (!attr) {
            addError("no nested Attributes class found", jat)
            return
        }
        if (!isStatic(attr.modifiers)) {
            addError("nested Attributes class must be static", jat)
            return
        }

        def meta = findNestedClass(resource, 'Meta')
        if (meta && !isStatic(meta.modifiers)) {
            addError("nested Meta class must be static", jat)
            return
        }
        meta ?= type_MapStringObject()

        enhanceResource(resource, attr, meta)
    }


    boolean existingSuperClass(ClassNode resource) {
        def superClass = resource.superClass

        ClassNode current
        for (current = superClass; current != OBJECT_TYPE; current = current.superClass) {
            if (current == TYPE_JAR) {
                return true
            }
        }

        if (current != OBJECT_TYPE) {
            addError("resource class must extend JsonApiResource but extended $superClass", resource)
            return true
        }

        // else no superclass, so we'll add one
        return false
    }


    ClassNode findNestedClass(ClassNode resourceClass, String suffix) {
        final nestedName = resourceClass.name + '$' + suffix
        for (ClassNode nested: resourceClass.innerClasses) {
            if (nestedName == nested.name) {
                return nested
            }
        }
        return null
    }


    void enhanceResource(ClassNode resource, ClassNode attr, ClassNode meta) {
        makeResource(resource, attr.asGenericsType(), true, meta.asGenericsType())
    }
}
