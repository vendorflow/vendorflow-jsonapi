package co.vendorflow.oss.jsonapi.processor

import static co.vendorflow.oss.jsonapi.processor.AttributesToDtoProcessor.MSO_CLASS_NAME
import static co.vendorflow.oss.jsonapi.processor.AttributesToDtoProcessor.generateDtoSource
import static co.vendorflow.oss.jsonapi.processor.AttributesToDtoProcessor.metaTypeParameter
import static co.vendorflow.oss.jsonapi.processor.AttributesToDtoProcessor.resourceSimpleName
import static com.fasterxml.jackson.databind.SerializationFeature.INDENT_OUTPUT
import static com.google.testing.compile.CompilationSubject.assertThat
import static com.google.testing.compile.Compiler.javac
import static javax.tools.StandardLocation.CLASS_OUTPUT

import javax.lang.model.element.Name
import javax.lang.model.element.TypeElement
import javax.lang.model.util.Elements
import javax.tools.JavaFileObject
import javax.tools.StandardLocation

import com.fasterxml.jackson.annotation.JsonTypeName
import com.fasterxml.jackson.databind.ObjectMapper
import com.google.testing.compile.Compilation
import com.google.testing.compile.JavaFileObjects

import co.vendorflow.oss.jsonapi.jackson.JsonApiModule
import co.vendorflow.oss.jsonapi.model.JsonApiAttributes
import co.vendorflow.oss.jsonapi.model.JsonApiAttributes.MapStringObject
import co.vendorflow.oss.jsonapi.model.JsonApiResource
import co.vendorflow.oss.jsonapi.model.JsonApiResourceId
import co.vendorflow.oss.jsonapi.processor.AttributesToDtoProcessor.ResourceClassInfo
import groovy.transform.CompileStatic
import groovy.transform.TupleConstructor
import groovy.transform.stc.POJO
import spock.lang.Ignore
import spock.lang.Specification

class AttributesToDtoProcessorTest extends Specification {

    def 'learning CompileTesting'() {
        when:
        def compilation = javac()
            .compile(JavaFileObjects.forSourceString('test.FooAttributes', 'package test; public class FooAttributes { public int i; public String toString() { return "FA: i="+i; } }'))

        then:
        assertThat(compilation).succeeded()
        def gf = compilation.generatedFiles()

        and:
        def cl = new GroovyClassLoader()
        def c = cl.defineClass('test.FooAttributes', gf.first().openInputStream().readAllBytes())
        def fa = c.constructors.first().newInstance()

        when:
        fa.i = 3

        then:
        "FA: i=3" == fa.toString()
    }


    def 'resourceSimpleName calculates the DTO name'(annotation, String simpleName, String expected) {
        expect:
        expected == resourceSimpleName(annotation, simpleName)

        where:
        annotation                  | simpleName      || expected
        jaa('foo')                  | 'FooAttributes' || 'FooResource'
        jaa('foo', 'Dto')           | 'FooAttributes' || 'FooDto'
        jaa('bar')                  | 'FooThings'     || 'FooThingsResource'
        jaa('bar', 'Dto')           | 'FooThings'     || 'FooThingsDto'
        jaa('bar')                  | 'FooAttributes' || 'FooResource'
        jaa('bar', '', 'ResFoo')    | 'FooAttributes' || 'ResFoo'
        jaa('Bar', 'Dto', 'ResFoo') | 'FooAttributes' || 'ResFoo'
    }


    @CompileStatic
    JavaFileObject attrClass(String pkg, String clazz, String annotationAttrs) {
        String attrSource = """package $pkg;
        @co.vendorflow.oss.jsonapi.model.JsonApiAttributes($annotationAttrs)
        public class $clazz {
        }"""

        JavaFileObjects.forSourceString(pkg + '.' + clazz, attrSource)
    }


    @CompileStatic
    Compilation compile(JavaFileObject... jfos) {
        javac()
            .withProcessors(new AttributesToDtoProcessor(), new TypeRegistrationProcessor())
            .compile(jfos)
    }


    def 'basic case'() {
        given:
        def pkg = 'test.pkg'
        def clazz = 'QuuxAttributes'

        when:
        def compilation = compile(attrClass(pkg, clazz, 'type = "quuxes"'))

        then: 'the compilation is successful'
        assertThat(compilation).succeeded()
        6 == compilation.generatedFiles().size()

        and: 'the generated Resource class loads and has the expected annotation'
        def loader = new CompilationClassPath(compilation).toClassLoader()
        def resourceClass = loader.loadClass('test.pkg.QuuxResource')
        'quuxes' == resourceClass.getAnnotation(JsonTypeName).value()

        when:
        JsonApiResource dto = resourceClass.constructors.find { it.parameterCount == 0 }.newInstance()
        dto.id = '1234'
        dto.meta = [abc: 'def', jki: 57]
        def attr = loader.loadClass('test.pkg.QuuxAttributes').constructors.first().newInstance()
        dto.attributes = attr

        then:
        JsonApiResourceId.of('quuxes', '1234') == dto.asResourceId()
        dto.toString().contains('id=1234')

        and:
        def spiLines = compilation
            .generatedFile(CLASS_OUTPUT, 'META-INF/services/co.vendorflow.oss.jsonapi.jackson.JsonApiTypeRegistration').get()
            .openReader(false)
            .readLines()

        ['test.pkg.$QuuxResourceTypeRegistration'] == spiLines

        // save Jackson tests for external compilation due to resource-loading headaches
    }


    def 'annotated meta type'() {
        when:
        def compilation = compile(attrClass(
            'test.pkg',
            'QuuxAttributes',
            'type = "quuxes", meta = co.vendorflow.oss.jsonapi.processor.AttributesToDtoProcessorTest.FooMeta.class'
        ))

        then:
        assertThat(compilation).succeeded()

        and:
        def genFiles = compilation.generatedSourceFiles()
        2 == genFiles.size()

        // println genFiles[0].openInputStream().text
    }


    def 'two Attribute classes'() {
        when:
        def compilation = compile(
            attrClass('test.pak', 'FooAttributes', 'type = "foos", resourceSuffix = "Dto"'),
            attrClass('test.pak', 'BazAttributes', 'type = "bazzes"'),
        )

        then:
        assertThat(compilation).succeeded()

        and:
        def spiLines = compilation
            .generatedFile(CLASS_OUTPUT, 'META-INF/services/co.vendorflow.oss.jsonapi.jackson.JsonApiTypeRegistration').get()
            .openReader(false)
            .readLines()

        [
            'test.pak.$FooDtoTypeRegistration',
            'test.pak.$BazResourceTypeRegistration',
        ] == spiLines
    }



    @CompileStatic
    static Map<String, Object> jaa(String type, String resourceSuffix = 'Resource', String resourceTypeName = '', meta = MSO_CLASS_NAME) {
        return [
            type: type,
            meta: meta,
            resourceSuffix: resourceSuffix,
            resourceTypeName: resourceTypeName,
        ]
    }


    @Ignore // present only to generate a runtime annotation on the class
    @CompileStatic
    static class FooMeta {}

    @CompileStatic
    @POJO
    @TupleConstructor(defaults = false)
    static class TestName implements Name {
        @Delegate(includeTypes = Name) CharSequence value

        @Override
        boolean contentEquals(CharSequence cs) {
            value.toString().contentEquals(cs)
        }
    }



}
