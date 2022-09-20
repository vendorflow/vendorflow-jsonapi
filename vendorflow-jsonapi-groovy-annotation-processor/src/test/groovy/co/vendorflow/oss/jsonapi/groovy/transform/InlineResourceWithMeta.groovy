package co.vendorflow.oss.jsonapi.groovy.transform

import co.vendorflow.oss.jsonapi.model.resource.JsonApiType
import groovy.transform.CompileStatic
import groovy.transform.stc.POJO

@JsonApiType('inlines-with')
@CompileStatic
@POJO
class InlineResourceWithMeta {

    static class Attributes {
        Integer inlines
    }

    static class Meta {
        String comment
    }
}
