package co.vendorflow.oss.jsonapi.model.request;

import co.vendorflow.oss.jsonapi.model.resource.JsonApiResource;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class JsonApiDataSingle<A, RM, R extends JsonApiResource<A, RM>, M> extends JsonApiDataDocument<R, M> {
    public static <A, RM, R extends JsonApiResource<A, RM>, M> JsonApiDataSingle<A, RM, R, M> of(R resource) {
        JsonApiDataSingle<A, RM, R, M> ds = new JsonApiDataSingle<>();
        ds.setData(resource);
        return ds;
    }
}
