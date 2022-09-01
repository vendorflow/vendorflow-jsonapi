package co.vendorflow.oss.jsonapi.processor

import static co.vendorflow.oss.jsonapi.processor.AttributesToDtoProcessorTest.compile
import static com.google.testing.compile.CompilationSubject.assertThat

import com.google.testing.compile.JavaFileObjects

import spock.lang.Specification

class SequentialMixedTypeProcessingTest extends Specification {

    def 'seq'() {
        given:
        def attrFile = JavaFileObjects.forSourceString('test.seq.OneAttributes', """
            package test.seq;
            @co.vendorflow.oss.jsonapi.model.resource.JsonApiAttributes(type="one")
            public class OneAttributes {}
        """)

        def resFile = JavaFileObjects.forSourceString('test.seq.MapResource', """
            package test.seq;
            @co.vendorflow.oss.jsonapi.model.resource.JsonApiType("mapped")
            public class MapResource extends co.vendorflow.oss.jsonapi.model.resource.MapAttributesJsonApiResource {
                @Override public String getType() { return "mapped"; }
            }
        """)


        when:
        def compilation = compile(attrFile, resFile)

        then:
        assertThat(compilation).succeeded()
        def spi = compilation.generatedFiles().find { it.name.endsWith '.JsonApiTypeRegistration' }
        def spiEntries = spi.openReader(true).readLines()
        spiEntries.containsAll(['test.seq.$MapResourceTypeRegistration', 'test.seq.$OneResourceTypeRegistration'])
    }
}
