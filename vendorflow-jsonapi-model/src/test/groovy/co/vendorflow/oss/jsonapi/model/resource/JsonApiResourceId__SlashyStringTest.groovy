package co.vendorflow.oss.jsonapi.model.resource

import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic

import org.apache.commons.lang3.RandomStringUtils

import spock.lang.Specification

class JsonApiResourceId__SlashyStringTest extends Specification {

    def 'parseSlashString throws on input without /'() {
        when:
        JsonApiResourceId.parseSlashString('widgets:123')

        then:
        IllegalArgumentException ex = thrown()
    }


    def 'parseSlashString takes the first segment of IDs with multiple slashes'() {
        when:
        final jari = JsonApiResourceId.parseSlashString('widgets/asdf/1234')

        then:
        'widgets' == jari.type
        'asdf/1234' == jari.id
    }


    def 'can round-trip an ID'() {
        given:
        final type = randomAlphabetic(5)
        final id = randomAlphabetic(10)

        when:
        final original = JsonApiResourceId.of(type, id)
        String slashy = original
        final parsed = JsonApiResourceId.parseSlashString(slashy)

        then:
        "$type/$id" == slashy
        original == parsed
    }
}
