package co.vendorflow.oss.jsonapi.groovy.pipeline.validation

import co.vendorflow.oss.jsonapi.model.resource.JsonApiResource
import groovy.transform.CompileStatic
import groovy.transform.stc.POJO

@CompileStatic
@POJO
class TestResource extends JsonApiResource<Object, Object> {
    String type

    static TestResource of(String type, String id) {
        new TestResource(type: type, id: id)
    }
}
