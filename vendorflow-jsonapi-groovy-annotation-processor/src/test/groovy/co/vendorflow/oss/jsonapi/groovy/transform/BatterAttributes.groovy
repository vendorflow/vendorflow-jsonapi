package co.vendorflow.oss.jsonapi.groovy.transform

import co.vendorflow.oss.jsonapi.model.resource.JsonApiAttributes
import groovy.transform.CompileStatic
import groovy.transform.stc.POJO

@JsonApiAttributes(type = 'batters', meta = BatterAttributes.Swing, nullable = false, resourceSuffix = 'Dto')
@CompileStatic
@POJO
class BatterAttributes {
    Double battingAverage
    Integer rbis

    static class Swing {}
}
