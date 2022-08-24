package co.vendorflow.oss.jsonapi.jackson

import static co.vendorflow.oss.jsonapi.model.links.JsonApiLink.linkObject
import static co.vendorflow.oss.jsonapi.model.links.JsonApiLink.linkUri
import static java.util.UUID.randomUUID

import co.vendorflow.oss.jsonapi.model.links.JsonApiLink
import co.vendorflow.oss.jsonapi.model.links.JsonApiLink.LinkObject
import co.vendorflow.oss.jsonapi.model.links.JsonApiLink.LinkUri
import co.vendorflow.oss.jsonapi.model.links.JsonApiLinks
import groovy.transform.CompileStatic

class LinksSerializationTest extends JacksonTest {

    @CompileStatic
    static URI newUri() {
        new URI("https://links.test/" + randomUUID())
    }


    def 'serializing bare URI'() {
        given:
        def uri = newUri()
        def lv = new LinkUri(href: uri)

        when:
        def json = JACKSON.writeValueAsString(lv)
        def jsonStringValue = jsonSlurper.parseText(json)

        then:
        uri as String == jsonStringValue
    }


    def 'deserializing bare URI'() {
        given:
        def uri = newUri()
        def json = '"' + uri + '"'

        when:
        def des = JACKSON.readValue(json, JsonApiLink)

        then:
        des instanceof LinkUri
        uri == des.href
    }


    def 'deserializing a bare URI with an implicit rel'() {
        given:
        def uri = newUri()
        def json = """{ "implicitRel": "$uri" }"""

        when:
        def parsed = JACKSON.readValue(json, JsonApiLinks)

        then:
        def links = parsed.all
        ['implicitRel'] as Set == links.keySet()
        def link = links.values()[0]
        uri == link.href
        'implicitRel' == link.rel
    }


    def 'round-tripping link object'() {
        given:
        def uri = newUri()
        def original = linkObject('test', uri)

        when:
        def json = JACKSON.writeValueAsString(original)
        def jsonMap = jsonSlurper.parseText(json)

        then:
        [rel: 'test', href: uri as String] == jsonMap

        when:
        def des = JACKSON.readValue(json, JsonApiLink)

        then:
        des instanceof LinkObject
        uri == des.href
        'test' == des.rel
    }


    def 'round-tripping links object'() {
        given:
        def bareUri = newUri()
        def saneUri = newUri()
        def wackyUri = newUri()

        def original = new JsonApiLinks()
            .add(linkUri('bare', bareUri))
            .add(linkObject('sane', saneUri))
            .tap { put('different', linkObject('other', wackyUri)) }

        when:
        def json = JACKSON.writeValueAsString(original)
        Map parsed = jsonSlurper.parseText(json)

        then:
        ['bare', 'sane', 'different'] as Set == parsed.keySet()

        and:
        bareUri as String == parsed['bare']

        and:
        'sane' == parsed['sane'].rel
        saneUri as String == parsed['sane'].href

        and:
        'other' == parsed['different'].rel
        wackyUri as String == parsed['different'].href

        when:
        def des = JACKSON.readValue(json, JsonApiLinks)
        def links = des.all

        then:
        ['bare', 'sane', 'different'] as Set == links.keySet()

        and:
        bareUri == links['bare'].href

        and:
        'sane' == links['sane'].rel
        saneUri == links['sane'].href

        and:
        'other' == links['different'].rel
        wackyUri == links['different'].href
    }
}
