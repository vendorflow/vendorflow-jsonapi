package co.vendorflow.oss.jsonapi.processor

import static com.fasterxml.jackson.databind.SerializationFeature.INDENT_OUTPUT

import com.fasterxml.jackson.databind.ObjectMapper

import co.vendorflow.oss.jsonapi.jackson.JsonApiModule
import groovy.json.JsonSlurper
import groovy.transform.CompileStatic
import spock.lang.Specification

@CompileStatic
abstract class JacksonTest extends Specification {
    protected static final ObjectMapper jackson = new ObjectMapper()
        .enable(INDENT_OUTPUT)
        .findAndRegisterModules()
        .registerModule(new JsonApiModule())

    protected static final JsonSlurper jsonSlurper = new JsonSlurper()
}
