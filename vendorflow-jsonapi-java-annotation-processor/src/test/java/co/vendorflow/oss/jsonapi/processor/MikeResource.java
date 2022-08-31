package co.vendorflow.oss.jsonapi.processor;

import co.vendorflow.oss.jsonapi.model.resource.JsonApiType;
import co.vendorflow.oss.jsonapi.model.resource.MapAttributesJsonApiResource;

@JsonApiType("mikes")
public class MikeResource extends MapAttributesJsonApiResource {
    @Override
    public String getType() {
        return "mikes";
    }
}
