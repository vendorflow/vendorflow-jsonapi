package co.vendorflow.oss.jsonapi.model.resource;

import java.util.Map;

/**
 * Unstructured Resource type. Cannot be directly used as-is, but subclasses can
 * directly extend this class and add {@link JsonApiType} to create a "known but undefined"
 * Resource type.
 *
 * @author Christopher Smith
 */
public abstract class MapAttributesJsonApiResource extends JsonApiResource<Map<String, Object>, Map<String, Object>> {
    @Override
    public final void setAttributes(Map<String, Object> value) {
        super.setAttributes(value);
    }

    @Override
    public final void setMeta(Map<String, Object> value) {
        super.setMeta(value);
    }
}
