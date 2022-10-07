package co.vendorflow.oss.jsonapi.model.resource

import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic
import static org.apache.commons.lang3.RandomStringUtils.randomNumeric

import org.apache.commons.lang3.RandomStringUtils

import spock.lang.Specification

class JsonApiRelationshipsTest extends Specification {

    JsonApiRelationships jar = new JsonApiRelationships()

    def 'getSingle returns a single ID'() {
        given:
        def id = JsonApiResourceId.of(randomAlphabetic(4), randomNumeric(5))
        jar.add('single') { it.linkTo id }

        expect:
        id == jar.getSingle('single')
    }


    def 'getSingle throws when the relationship does not exist'() {
        given:
        def id = JsonApiResourceId.of(randomAlphabetic(4), randomNumeric(5))
        jar.add('single') { it.linkTo id }

        when:
        jar.getSingle('nonexistent')

        then:
        NoSuchElementException ex = thrown()
        ex.message.contains 'nonexistent'
    }


    def 'getSingle throws when the relationship is non-single'(ids) {
        given:
        jar.add('notsingle') { it.linkTo(ids) }

        when:
        jar.getSingle('notsingle')

        then:
        IllegalStateException ex = thrown()
        ex.message.contains 'notsingle'
        ex.message.contains (ids.size() as String)

        where:
        ids << [
            [],
            (0..1).collect { JsonApiResourceId.of(randomAlphabetic(4), randomNumeric(5)) }
        ]
    }
}
