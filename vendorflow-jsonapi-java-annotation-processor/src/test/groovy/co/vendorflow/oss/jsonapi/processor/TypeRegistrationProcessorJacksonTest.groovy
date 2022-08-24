package co.vendorflow.oss.jsonapi.processor

import javax.validation.Validation
import javax.validation.Validator

import org.hibernate.validator.messageinterpolation.ParameterMessageInterpolator

import co.vendorflow.oss.jsonapi.model.JsonApiResource

class TypeRegistrationProcessorJacksonTest extends JacksonTest {

    private static final Validator validator = Validation.byDefaultProvider()
        .configure()
        .messageInterpolator(new ParameterMessageInterpolator())
        .buildValidatorFactory()
        .validator


    def '@JsonApiType is discovered on hand-written JsonApiResource'() {
        given:
        def json = """{
            "type": "mikes",
            "id": "Tyson",
            "attributes": {
                "weight": "heavy",
                "ears": -1
            },
            "meta": {
                "born": 1966
            }
        }"""

        when:
        MikeResource des = jackson.readValue(json, JsonApiResource)

        then:
        'heavy' == des.attributes.weight
        -1 == des.attributes.ears
        1966 == des.meta.born
    }
}
