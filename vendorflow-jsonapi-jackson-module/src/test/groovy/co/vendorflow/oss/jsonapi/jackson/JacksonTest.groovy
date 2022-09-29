package co.vendorflow.oss.jsonapi.jackson

import static com.fasterxml.jackson.databind.SerializationFeature.INDENT_OUTPUT

import com.fasterxml.jackson.databind.ObjectMapper

import co.vendorflow.oss.jsonapi.model.resource.HasJsonApiResourceId
import co.vendorflow.oss.jsonapi.model.resource.JsonApiResource
import groovy.json.JsonSlurper
import groovy.transform.CompileStatic
import net.javacrumbs.jsonunit.assertj.JsonAssert
import net.javacrumbs.jsonunit.assertj.JsonAssertion
import spock.lang.Specification

@CompileStatic
abstract class JacksonTest extends Specification {
    protected final static String DATA = 'data'
    protected final static String TOP_LEVEL_DATA = '$.data'
    protected final static String LINKS = 'links'
    protected final static String RELATIONSHIPS = 'relationships'

    protected final static ObjectMapper JACKSON = new ObjectMapper()
        .findAndRegisterModules()
        .registerModule(new JsonApiModule())
        .enable(INDENT_OUTPUT)

    protected final static String serialize(Object o) {
        JACKSON.writeValueAsString(o)
    }

    protected final static <R extends JsonApiResource<?, ?>> R deserialize(String json, Class<R> clazz = JsonApiResource) {
        JACKSON.readValue(json, clazz)
    }


    protected final static JsonSlurper jsonSlurper = new JsonSlurper()


    protected JsonAssertion hasId(String type, String id) {
        return { JsonAssert node -> node.isObject().containsEntry('type', type).containsEntry('id', id) }
    }

    protected JsonAssertion hasId(HasJsonApiResourceId h) {
        h.asResourceId().with { hasId(type, id) }
    }


    protected JsonAssertion hasRelationship(String name, Collection<HasJsonApiResourceId> hs) {
        def ids = hs*.asResourceId().collect { [type: it.type, id: it.id] }
        return { JsonAssert node -> node.node(RELATIONSHIPS).node(name).isObject().containsEntry('data', ids) }
    }
}
