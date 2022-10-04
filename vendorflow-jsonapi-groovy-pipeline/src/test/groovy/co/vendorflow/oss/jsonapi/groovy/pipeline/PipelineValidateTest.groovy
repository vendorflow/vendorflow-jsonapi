package co.vendorflow.oss.jsonapi.groovy.pipeline

import static co.vendorflow.oss.jsonapi.groovy.pipeline.validation.RelationshipRules.*

import co.vendorflow.oss.jsonapi.groovy.pipeline.validation.JsonApiValidationRule
import co.vendorflow.oss.jsonapi.model.request.JsonApiDataSingle
import co.vendorflow.oss.jsonapi.model.resource.JsonApiResource
import co.vendorflow.oss.jsonapi.model.resource.JsonApiResourceId
import io.vavr.control.Either
import spock.lang.Shared
import spock.lang.Specification

class PipelineValidateTest extends Specification {
    @Shared
    static JsonApiValidationRule<JsonApiResource>[] rules = [
        hasNoRelationship('parole-officer'),
        hasSingleValuedRelationship('spouse', 'people'),
        relationshipHasType('child', 'people'),
        hasSingleValuedRelationship('pet'),
    ]

    def 'validate succeeds if all rules pass'() {
        given:
        def r = new PipelineResource().relationships {
            it.add('spouse') { it.linkTo(JsonApiResourceId.of('people', 'Sally')) }
            it.add('pet')    { it.linkTo(JsonApiResourceId.of('project', 'todo')) }
        }
        def dataSingle = JsonApiDataSingle.of(r)

        when:
        Either e = Either.right(dataSingle)
            .validate(rules)

        then:
        e.isRight()
        dataSingle == e.get()
    }


    def 'validate collects multiple errors from different rules'() {
        given:
        def r = new PipelineResource().relationships {
            it.add('parole-officer') { it.linkTo(JsonApiResourceId.of('robots', 'RoboCop')) }
            it.add('spouse') { it.linkTo(JsonApiResourceId.of('people', 'Sally')) }
            it.add('child')  { it.linkTo(JsonApiResourceId.of('nodes', 'leaf'), JsonApiResourceId.of('pid', 'systemd')) }
        }
        def dataSingle = JsonApiDataSingle.of(r)

        when:
        Either e = Either.right(dataSingle)
            .validate(rules)

        then:
        e.isLeft()
        var errors = e.getLeft().errors
        3 == errors.size()
        errors.every { it.status == 422 }
        errors.every { it.code.startsWith 'jsonapi.relationship.' }
    }


    def 'validate collects multiple errors from the same rule'() {
        given:
        def r = new PipelineResource().relationships {
            it.add('spouse') { it.linkTo(JsonApiResourceId.of('rocks', 'igneous'), JsonApiResourceId.of('stars', 'sun')) }
            it.add('pet')  { it.linkTo(JsonApiResourceId.of('rocks', 'sedimentary')) }
        }

        def dataSingle = JsonApiDataSingle.of(r)

        when:
        Either e = Either.right(dataSingle)
            .validate(rules)

        then:
        e.isLeft()
        var errors = e.getLeft().errors
        2 == errors.size()
        errors.every { it.detail.contains 'relationship spouse' }
    }
}
