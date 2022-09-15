package co.vendorflow.oss.jsonapi.groovy.transform

import co.vendorflow.oss.jsonapi.jackson.JsonApiTypeIdResolver
import co.vendorflow.oss.jsonapi.model.resource.JsonApiType
import spock.lang.Specification

class JsonApiTypeAstTransformationTest extends Specification {

    private static final TYPE_NAME = ExplicitResource.getAnnotation(JsonApiType).value()


    def 'TYPE and getType are added to the annotated class'() {
        expect:
        TYPE_NAME == ExplicitResource.TYPE
        TYPE_NAME == new ExplicitResource().type
    }


    def 'type registration is added to SPI'() {
        when:
        def registrations = JsonApiTypeIdResolver.findRegistrations()

        then:
        def match = registrations.filter { it.typeName() == TYPE_NAME }.findFirst().get()
        ExplicitResource == match.typeClass()
    }
}
