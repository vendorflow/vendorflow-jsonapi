package co.vendorflow.oss.jsonapi.model.request;

import co.vendorflow.oss.jsonapi.model.resource.JsonApiResource;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class JsonApiDataSingle<R extends JsonApiResource<?, ?>, M> extends JsonApiDataDocument<R, M> {
    public static <R extends JsonApiResource<?, ?>, M> JsonApiDataSingle<R, M> of(R resource) {
        JsonApiDataSingle<R, M> ds = new JsonApiDataSingle<>();
        ds.setData(resource);
        return ds;
    }
}
