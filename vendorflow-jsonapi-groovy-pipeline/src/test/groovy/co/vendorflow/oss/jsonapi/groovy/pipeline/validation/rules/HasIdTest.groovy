package co.vendorflow.oss.jsonapi.groovy.pipeline.validation.rules

import static co.vendorflow.oss.jsonapi.groovy.pipeline.validation.rules.ResourceRules.hasId
import static org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric

import co.vendorflow.oss.jsonapi.groovy.pipeline.validation.TestResource
import co.vendorflow.oss.jsonapi.groovy.pipeline.validation.rules.HasId
import co.vendorflow.oss.jsonapi.model.resource.JsonApiResource
import groovy.transform.CompileStatic
import groovy.transform.stc.POJO
import spock.lang.Rollup
import spock.lang.Specification

class HasIdTest extends Specification {

    def 'rule can match null ID'() {
        given:
        var rule = hasId(null)
        var resource = resourceWithId(null)

        when:
        var errors = rule.validate(resource)

        then:
        errors.empty
    }


    def 'rule requiring null ID fails if ID is provided'() {
        given:
        var rule = hasId(null)
        var resource = resourceWithId('present')

        when:
        var errors = rule.validate(resource)

        then:
        1 == errors.size()
        HasId.CODE == errors[0].code
    }


    def 'rule can match present ID'() {
        given:
        String expected = randomAlphanumeric(10)
        var rule = hasId(expected)
        var resource = resourceWithId(expected)

        when:
        var errors = rule.validate(resource)

        then:
        errors.empty
    }


    @Rollup
    def 'rule requiring a present ID fails on different or null ID'(String actual) {
        given:
        String expected = randomAlphanumeric(10)
        var rule = hasId(expected)
        var resource = resourceWithId(actual)

        when:
        var errors = rule.validate(resource)

        then:
        1 == errors.size()
        HasId.CODE == errors[0].code

        where:
        actual << [ 'otherId', null ]
    }


    @CompileStatic
    JsonApiResource<?, ?> resourceWithId(String id) {
        new TestResource(type: 'test', id: id)
    }
}
