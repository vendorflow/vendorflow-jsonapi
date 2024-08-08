package co.vendorflow.oss.jsonapi.model.resource.id

import spock.lang.Rollup
import spock.lang.Specification

@Rollup
class CompositeIdTest extends Specification {

    def 'CompositeId.of returns arity 2..4'(String[] components, Class type, string) {
        when:
        final id = CompositeId.of(components)

        then:
        type.isInstance(id)
        string == id.toString()

        where:
        components               || type         | string
        ['a1', 'b2']             || CompositeId2 | 'a1--b2'
        ['a1', 'b2', 'c3']       || CompositeId3 | 'a1--b2--c3'
        ['a1', 'b2', 'c3', 'd4'] || CompositeId4 | 'a1--b2--c3--d4'
    }


    def 'CompositeId.of throws on arity <2 or >4'(String[] components) {
        when:
        final id = CompositeId.of(components)

        then:
        IllegalArgumentException ex = thrown()

        where:
        components << [
            [],
            ['a1'],
            ['a1', 'b2', 'c3', 'd4', 'e5']
        ]
    }


    def 'CompositeIds can round-trip'(Class type, String[] components, String string) {
        when:
        final id = type.of(components)

        then:
        string == id.toString()
        components == id.components()

        when:
        final parsed = type.parse(string)

        then:
        parsed == id
        components == parsed.components()

        where:
        type         | components               || string
        CompositeId2 | ['a1', 'b2']             || 'a1--b2'
        CompositeId3 | ['a1', 'b2', 'c3']       || 'a1--b2--c3'
        CompositeId4 | ['a1', 'b2', 'c3', 'd4'] || 'a1--b2--c3--d4'
    }


    def 'parsing a String ID of incorrect arity throws'(Class type, String tooShort, String correct, String tooLong) {
        when:
        type.parse(tooShort)

        then:
        IllegalArgumentException exS = thrown()

        when:
        type.parse(tooLong)

        then:
        IllegalArgumentException exL = thrown()

        when:
        final id = type.parse(correct)

        then:
        type.isInstance(id)
        correct == id.toString()

        where:
        type         || tooShort  | correct      | tooLong
        CompositeId2 || 'a'       | 'a--b'       | 'a--b--c'
        CompositeId3 || 'a--b'    | 'a--b--c'    | 'a--b--c--d'
        CompositeId4 || 'a--b--c' | 'a--b--c--d' | 'a--b--c--d--e'
    }


    def 'non-String objects can be combined into an ID'() {
        when:
        final id = CompositeId.of('str', 42, new UUID(0, 0), 'asdf')

        then:
        id instanceof CompositeId4

        and:
        'str--42--00000000-0000-0000-0000-000000000000--asdf' == id.toString()
        ['str', '42', '00000000-0000-0000-0000-000000000000', 'asdf'] == id.components()
    }
}
