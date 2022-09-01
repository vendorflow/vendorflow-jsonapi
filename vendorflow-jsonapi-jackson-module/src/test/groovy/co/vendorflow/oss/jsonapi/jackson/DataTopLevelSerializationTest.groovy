package co.vendorflow.oss.jsonapi.jackson

import static co.vendorflow.oss.jsonapi.model.links.JsonApiLink.linkUri
import static java.util.Collections.emptyMap
import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson

import co.vendorflow.oss.jsonapi.model.request.JsonApiDataSingle

class DataTopLevelSerializationTest extends JacksonTest {

    def 'top-level links and meta are suppressed when absent'(meta) {
        given:
        def resource = new MapTestResource(id: 'test123', attributes: [some: 'value'])
        def single = JsonApiDataSingle.of(resource).tap { it.meta = meta }

        when:
        def json = JACKSON.writeValueAsString(single)

        then:
        assertThatJson(json)
            .isObject()
            .containsEntry('data', [type: 'test_map', id: 'test123', attributes: [some: 'value']])
            .doesNotContainKeys('links', 'meta')

        where:
        meta << [null, emptyMap()]
    }


    def 'top-level links and meta are included when present'() {
        given:
        def resource = new MapTestResource(id: 'test123', attributes: [some: 'value'])
        def single = JsonApiDataSingle.of(resource).tap {
            links.add(linkUri('example', 'https://example.test'))
            meta = [hyper: 'super']
        }

        when:
        def json = JACKSON.writeValueAsString(single)

        then:
        assertThatJson(json)
            .isObject()
            .containsEntry('data', [type: 'test_map', id: 'test123', attributes: [some: 'value']])
            .containsEntry('links', [example: 'https://example.test'])
            .containsEntry('meta', [hyper: 'super'])
    }


    def 'attributes are included even if empty'() {
        given:
        def resource = new MapTestResource(id: 'test123', attributes: emptyMap())
        def single = JsonApiDataSingle.of(resource).tap {
            links.add(linkUri('example', 'https://example.test'))
            meta = [hyper: 'super']
        }

        when:
        def json = JACKSON.writeValueAsString(single)

        then:
        assertThatJson(json)
            .isObject()
            .containsEntry('data', [type: 'test_map', id: 'test123', attributes: [:]])
            .containsEntry('links', [example: 'https://example.test'])
            .containsEntry('meta', [hyper: 'super'])
    }
}
