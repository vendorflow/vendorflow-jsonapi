package co.vendorflow.oss.jsonapi.jackson

import static com.fasterxml.jackson.databind.SerializationFeature.INDENT_OUTPUT

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature

import groovy.json.JsonSlurper
import groovy.transform.CompileStatic
import spock.lang.Specification

@CompileStatic
abstract class JacksonTest extends Specification {
    protected final static ObjectMapper JACKSON = new ObjectMapper()
        .findAndRegisterModules()
        .registerModule(new JsonApiModule())
        .enable(INDENT_OUTPUT)

    protected final static JsonSlurper jsonSlurper = new JsonSlurper()
}
