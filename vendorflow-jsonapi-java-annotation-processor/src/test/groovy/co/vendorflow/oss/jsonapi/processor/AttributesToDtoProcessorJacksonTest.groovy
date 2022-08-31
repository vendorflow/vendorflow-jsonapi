package co.vendorflow.oss.jsonapi.processor

import static com.fasterxml.jackson.databind.SerializationFeature.INDENT_OUTPUT

import javax.validation.Path.PropertyNode
import javax.validation.Validation
import javax.validation.Validator
import javax.validation.constraints.Min
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

import org.hibernate.validator.messageinterpolation.ParameterMessageInterpolator

import com.fasterxml.jackson.databind.ObjectMapper

import co.vendorflow.oss.jsonapi.jackson.JsonApiModule
import co.vendorflow.oss.jsonapi.model.resource.JsonApiResource
import groovy.json.JsonSlurper
import spock.lang.Specification

class AttributesToDtoProcessorJacksonTest extends JacksonTest {
    private static final Validator validator = Validation.byDefaultProvider()
        .configure()
        .messageInterpolator(new ParameterMessageInterpolator())
        .buildValidatorFactory()
        .validator


    def 'can round-trip an Alpha'() {
        given:
        def ar = new AlphaResource(
            id: 'ALPHA',
            attributes: new AlphaAttributes(aleph: 'first letter'),
            meta: [hyper: 'super']
        )

        when:
        def jsonString = jackson.writeValueAsString(ar)
        def json = jsonSlurper.parseText(jsonString)

        then:
        'alphas' == json.type
        'ALPHA' == json.id
        [aleph: 'first letter'] == json.attributes
        [hyper: 'super'] == json.meta

        when:
        def des = jackson.readValue(jsonString, JsonApiResource)

        then:
        des instanceof AlphaResource
        'ALPHA' == des.id
        'first letter' == des.attributes.aleph
        des.meta instanceof Map
        [hyper: 'super'] == des.meta
    }


    def 'can round-trip a Bravo'() {
        given:
        def bd = new BravoDto(
            id: 'BRAVO',
            attributes: new BravoAttributes(bet: 'not likely'),
            meta: new BravoAttributes.Meta(applause: 42)
        )

        when:
        def jsonString = jackson.writeValueAsString(bd)
        def json = jsonSlurper.parseText(jsonString)

        then:
        'bravos' == json.type
        'BRAVO' == json.id
        [bet: 'not likely'] == json.attributes

        when:
        def des = jackson.readValue(jsonString, JsonApiResource)

        then:
        des instanceof BravoDto
        'BRAVO' == des.id
        'not likely' == des.attributes.bet
        des.meta instanceof BravoAttributes.Meta
        42 == des.meta.applause
    }


    def 'can deserialize a type that was not yet serialized'() {
        given:
        def jsonString = """{
            "type": "charlies",
            "id": "Townsend",
            "attributes": {
                "angels": [ "Jill", "Sabrina", "Kelly" ]
            },
            "meta": {
                "network": "ABC",
                "seasons": 5
            }
        }"""

        when:
        ChuckRez c = jackson.readValue(jsonString, JsonApiResource)

        then:
        'Townsend' == c.id
        ['Jill', 'Sabrina', 'Kelly'] == c.attributes.angels
        'ABC' == c.meta.network
        5 == c.meta.seasons

        and:
        validator.validate(c).isEmpty()
    }


    def 'nullable attributes permitted'() {
        given:
        def jsonString = """{
            "type": "charlies",
            "id": "Chaplin",
            "meta": {
                "character": "Tramp"
            }
        }"""

        when:
        ChuckRez c = jackson.readValue(jsonString, JsonApiResource)

        then:
        'Chaplin' == c.id
        null == c.attributes
        'Tramp' == c.meta.character

        and:
        validator.validate(c).isEmpty()
    }


    def 'non-nullable attributes are required'() {
        given:
        def jsonString = """{
            "type": "bravos",
            "id": "Hush",
            "meta": {
                "applause": 0
            }
        }"""

        when:
        BravoDto b = jackson.readValue(jsonString, JsonApiResource)

        then:
        'Hush' == b.id
        null == b.attributes
        0 == b.meta.applause

        and:
        def cv = validator.validate(b).first()
        cv.constraintDescriptor.annotation instanceof NotNull

        and:
        def attrPath = cv.propertyPath.toList()
        1 == attrPath.size()
        def attrNode = attrPath.first()
        attrNode instanceof PropertyNode
        'attributes' == attrNode.name
    }


    def '@Valid is propagated to both attributes and meta'() {
        given:
        def jsonString = """{
            "type": "bravos",
            "id": "Ovation",
            "attributes": {
                "bet": "really long value"
            },
            "meta": {
                "applause": -100
            }
        }"""

        when:
        BravoDto b = jackson.readValue(jsonString, JsonApiResource)

        and:
        def cvs = validator.validate(b)

        then:
        'Ovation' == b.id
        'really long value' == b.attributes.bet
        -100 == b.meta.applause

        and:
        2 == cvs.size()

        and:
        def attrCv = cvs.find { it.propertyPath.first().name == 'attributes' }
        attrCv.constraintDescriptor.annotation instanceof Size

        def metaCv = cvs.find { it.propertyPath.first().name == 'meta' }
        metaCv.constraintDescriptor.annotation instanceof Min
    }
}
