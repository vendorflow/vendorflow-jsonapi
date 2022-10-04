package co.vendorflow.oss.jsonapi.jackson

import static co.vendorflow.oss.jsonapi.model.error.JsonApiErrors.NOT_FOUND_CODE
import static java.util.UUID.randomUUID
import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson

import co.vendorflow.oss.jsonapi.model.error.JsonApiError
import co.vendorflow.oss.jsonapi.model.error.JsonApiErrors
import co.vendorflow.oss.jsonapi.model.request.JsonApiErrorDocument
import co.vendorflow.oss.jsonapi.model.resource.JsonApiResourceId

class ErrorsSerdesTest extends JacksonTest {

    def 'can serialize an errors document'() {
        given:
        def ser = new JsonApiError(418, 'teapot', "I am a little teapot", null, randomUUID() as String)

        when:
        def json = JACKSON.writeValueAsString(ser.asDocument())

        then:
        assertThatJson(json)
            .inPath('$.errors')
            .isArray()
            .hasSize(1)
            .first()
            .isObject()
            .isEqualTo([status: '418', code: 'teapot', title: "I am a little teapot", id: ser.id])
    }


    def 'can deserialize an errors document'() {
        given:
        def json = """{
            "errors": [{
                "status": "451",
                "code": "BURN",
                "title": "Fahrenheit 451",
                "id": "bradbury",
                "meta": { "celsius": 233 }
            }],
            "meta": {
                "errorCount": 1
            }
        }"""

        when:
        JsonApiErrorDocument document = JACKSON.readValue(json, JsonApiErrorDocument)

        then:
        1 == document.meta.errorCount
        1 == document.errors.size()

        and:
        def error = document.errors[0]
        451 == error.status
        'BURN' == error.code
        'Fahrenheit 451' == error.title
        'bradbury' == error.id
        [celsius: 233] == error.meta
    }


    def 'can serialize a notFound error'() {
        given:
        String id = randomUUID()
        def ser = JsonApiErrors.notFound(JsonApiResourceId.of('test', id))

        when:
        def json = JACKSON.writeValueAsString(ser.asDocument())

        then:
        assertThatJson(json)
            .inPath('$.errors[0]')
            .isObject()
            .isEqualTo([
                status: '404',
                code: NOT_FOUND_CODE,
                meta: [resource: [type: 'test', id: id]]
            ])
    }
}
