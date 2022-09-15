package co.vendorflow.oss.jsonapi.groovy.transform

import co.vendorflow.oss.jsonapi.model.resource.JsonApiType
import co.vendorflow.oss.jsonapi.model.resource.MapAttributesJsonApiResource
import groovy.transform.CompileStatic
import groovy.transform.stc.POJO

@JsonApiType('test-ma')
@CompileStatic
@POJO
class ExplicitResource extends MapAttributesJsonApiResource {
}
