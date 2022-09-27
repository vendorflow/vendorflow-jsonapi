package co.vendorflow.oss.jsonapi.groovy.transform

import co.vendorflow.oss.jsonapi.jackson.JsonApiTypeIdResolver
import co.vendorflow.oss.jsonapi.model.resource.JsonApiResourceId
import co.vendorflow.oss.jsonapi.model.resource.JsonApiType
import groovy.transform.Generated
import spock.lang.Specification

class JsonApiTypeAstTransformationTest extends Specification {

    private static final TYPE_NAME = ExplicitResource.getAnnotation(JsonApiType).value()


    def 'TYPE and getType are added to the annotated class'() {
        expect:
        TYPE_NAME == ExplicitResource.TYPE
        TYPE_NAME == new ExplicitResource().type

        and:
        [ExplicitResource.getField('TYPE'), ExplicitResource.getMethod('getType')].every { it.getAnnotation(Generated) }
    }


    def 'static id method is added to the annotated class'() {
        expect:
        JsonApiResourceId.of(TYPE_NAME, '1234') == ExplicitResource.id(1234)

        and:
        ExplicitResource.getMethod('id', Object).getAnnotation(Generated)
    }


    def 'type registration is added to SPI'() {
        when:
        def registrations = JsonApiTypeIdResolver.findRegistrations()

        then:
        def match = registrations.filter { it.typeName() == TYPE_NAME }.findFirst().get()
        ExplicitResource == match.typeClass()
    }
}
