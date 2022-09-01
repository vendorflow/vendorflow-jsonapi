package co.vendorflow.oss.jsonapi.jackson;

import co.vendorflow.oss.jsonapi.model.resource.JsonApiType;
import co.vendorflow.oss.jsonapi.model.resource.MapAttributesJsonApiResource;

@JsonApiType("test_map")
public class MapTestResource extends MapAttributesJsonApiResource {
    public static final String TYPE = "test_map";

    @Override
    public String getType() {
        return TYPE;
    }

}
