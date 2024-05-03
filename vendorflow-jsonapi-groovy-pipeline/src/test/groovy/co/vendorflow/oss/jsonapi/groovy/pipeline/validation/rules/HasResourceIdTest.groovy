package co.vendorflow.oss.jsonapi.groovy.pipeline.validation.rules

import static co.vendorflow.oss.jsonapi.groovy.pipeline.validation.rules.ResourceRules.hasResourceId
import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic

import org.apache.commons.lang3.RandomStringUtils

import co.vendorflow.oss.jsonapi.groovy.pipeline.validation.TestResource
import spock.lang.Specification

class HasResourceIdTest extends Specification {

    def 'rule does not permit null type'() {
        when:
        hasResourceId(null, 'anything')

        then:
        NullPointerException npe = thrown()
        npe.message.contains 'expectedType'
    }


    def 'rule passes correct type and ID'() {
        given:
        var type = randomAlphabetic(5)
        var id = randomAlphabetic(8)
        var rule = hasResourceId(type, id)

        when:
        var errors = rule.validate(TestResource.of(type, id))

        then:
        errors.empty
    }


    def 'rule does permit null ID'() {
        given:
        var rule = hasResourceId('testType', null)

        when:
        var errors = rule.validate(TestResource.of('testType', null))

        then:
        errors.empty
    }


    def 'rule enforces null ID'() {
        given:
        var rule = hasResourceId('testType', null)

        when:
        var errors = rule.validate(TestResource.of('testType', 'present'))

        then:
        1 == errors.size()
        HasId.CODE == errors[0].code
    }


    def 'rule enforces present ID'() {
        given:
        var rule = hasResourceId('testType', 'present')

        when:
        var errors = rule.validate(TestResource.of('testType', 'wrongId'))

        then:
        1 == errors.size()
        HasId.CODE == errors[0].code
    }


    def 'rule enforces correct type'() {
        given:
        var rule = hasResourceId('testType', 'testId')

        when:
        var errors = rule.validate(TestResource.of('wrongType', 'testId'))

        then:
        1 == errors.size()
        HasType.CODE == errors[0].code
    }


    def 'rule reports when both type and ID are wrong'() {
        given:
        var rule = hasResourceId('testType', 'testId')

        when:
        var errors = rule.validate(TestResource.of('wrongType', 'wrongId'))

        then:
        2 == errors.size()
        [HasId.CODE, HasType.CODE] as Set == errors*.code as Set
    }

}
