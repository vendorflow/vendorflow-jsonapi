package co.vendorflow.oss.jsonapi.groovy.transform

import co.vendorflow.oss.jsonapi.model.resource.JsonApiType
import groovy.transform.CompileStatic
import groovy.transform.stc.POJO
import spock.lang.Specification

class CanaryTest extends Specification {

    //@JsonApiType(Attr.TYPE)
    @CompileStatic
    @POJO
    static class Attr {
        public static final String TYPE = 'canary-attr'
    }
}
