package co.vendorflow.oss.jsonapi.model.request;

import java.util.Map;

import co.vendorflow.oss.jsonapi.model.resource.JsonApiResource;
import co.vendorflow.oss.jsonapi.model.resource.JsonApiType;

@JsonApiType(TestResource.TYPE)
public class TestResource extends JsonApiResource<TestResource.Attributes, Map<String, Object>> {
    public static final String TYPE = "jsonapi-model-test";

    public static class Attributes {}

    @Override
    public String getType() {
        return TYPE;
    }
}
