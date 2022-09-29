package co.vendorflow.oss.jsonapi.jackson

import co.vendorflow.oss.jsonapi.model.request.JsonApiDataSingle

import static co.vendorflow.oss.jsonapi.jackson.MapTestResource.mtrid
import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson

class RelSerializationTest extends JacksonTest {

    def 'resource with no relationships does not have a relationships object'() {
        given:
        def resource = new MapTestResource(id: '1234')
        def single = JsonApiDataSingle.of(resource)

        when:
        def json = serialize(single)

        then:
        assertThatJson(json)
            .inPath(TOP_LEVEL_DATA)
            .isObject()
            .containsEntry('id', '1234')
            .doesNotContainKey('relationships')
    }


    def 'a multi-valued relationship is serialized as an array value'() {
        given:
        def tl = JsonApiDataSingle.of(
            new MapTestResource(id: 'tl')
                .relationships { it.add('multi') { it.linkTo(mtrid('zero'), mtrid('one')) } }
        )

        when:
        def json = serialize(tl)

        then:
        assertThatJson(json)
            .inPath(TOP_LEVEL_DATA).and(
                hasId(mtrid('tl')),
                hasRelationship('multi', [mtrid('zero'), mtrid('one')])
            )
    }
}
