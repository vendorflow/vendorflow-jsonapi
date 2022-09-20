package co.vendorflow.oss.jsonapi.groovy.transform

import co.vendorflow.oss.jsonapi.model.resource.JsonApiResource
import co.vendorflow.oss.jsonapi.model.resource.JsonApiType
import groovy.transform.CompileStatic
import groovy.transform.stc.POJO

@JsonApiType('dgr')
@CompileStatic
@POJO
class DirectGenericResource extends JsonApiResource<DirectGenericResource.Attributes, Map<String, Object>> {
    static class Attributes {}

    @Override
    String getType() {
        'dgr'
    }
}
