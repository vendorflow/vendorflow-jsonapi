package co.vendorflow.oss.jsonapi.groovy.transform

import co.vendorflow.oss.jsonapi.model.resource.JsonApiType
import groovy.transform.CompileStatic
import groovy.transform.stc.POJO

@JsonApiType('inlines-without')
@CompileStatic
@POJO
class InlineResourceWithoutMeta {
    static class Attributes {
        Integer outlines
    }
}
