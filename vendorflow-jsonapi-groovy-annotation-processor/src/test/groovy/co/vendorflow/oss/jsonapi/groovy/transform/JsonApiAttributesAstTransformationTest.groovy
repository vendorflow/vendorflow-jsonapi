package co.vendorflow.oss.jsonapi.groovy.transform

import static java.util.UUID.randomUUID

import groovy.transform.Generated
import spock.lang.Specification

class JsonApiAttributesAstTransformationTest extends Specification {

    def 'BatterDto class exists'() {
        expect:
        // must use forName because using BatterDto as a literal compiles to a property access, which fails at runtime
        def rc = Class.forName('co.vendorflow.oss.jsonapi.groovy.transform.BatterDto')
        rc.getAnnotation(Generated)
    }


    def 'BatterAttributes has an asResource method'() {
        given:
        def id = randomUUID()
        def attr = new BatterAttributes(battingAverage: 0.25, rbis: 4)

        when:
        def resource = attr.asResource(id)

        then:
        "batters/$id" == resource.asResourceId().toString()
        4 == resource.attributes.rbis

        and:
        BatterAttributes.getMethod('asResource', Object).getAnnotation(Generated)
    }
}
