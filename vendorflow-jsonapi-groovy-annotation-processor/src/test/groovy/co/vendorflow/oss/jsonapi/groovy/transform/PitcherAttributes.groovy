package co.vendorflow.oss.jsonapi.groovy.transform

import co.vendorflow.oss.jsonapi.model.resource.JsonApiAttributes

@JsonApiAttributes(type = 'pitchers')
class PitcherAttributes {
    Double era
    Integer pitchCount
}
