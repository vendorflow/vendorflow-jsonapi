package co.vendorflow.oss.jsonapi.groovy.pipeline.validation.rules

import static co.vendorflow.oss.jsonapi.groovy.pipeline.validation.rules.ResourceRules.hasType
import static org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric

import co.vendorflow.oss.jsonapi.groovy.pipeline.validation.TestResource
import co.vendorflow.oss.jsonapi.groovy.pipeline.validation.rules.HasId
import co.vendorflow.oss.jsonapi.model.resource.JsonApiResource
import groovy.transform.CompileStatic
import groovy.transform.stc.POJO
import spock.lang.Rollup
import spock.lang.Specification

class HasTypeTest extends Specification {

    def 'rule refuses null type'() {
        when:
        var rule = hasType(null)

        then:
        NullPointerException npe = thrown()
        npe.message.contains 'expectedType'
    }


    def 'rule can match correct type'() {
        given:
        String expected = randomAlphanumeric(10)
        var rule = hasType(expected)
        var resource = TestResource.of(expected, 'asdf')

        when:
        var errors = rule.validate(resource)

        then:
        errors.empty
    }


    @Rollup
    def 'rule fails on different or null type'(String actual) {
        given:
        String expected = randomAlphanumeric(10)
        var rule = hasType(expected)
        var resource = TestResource.of(actual, 'asdf')

        when:
        var errors = rule.validate(resource)

        then:
        1 == errors.size()
        HasType.CODE == errors[0].code

        where:
        actual << [ 'otherType', null ]
    }
}
