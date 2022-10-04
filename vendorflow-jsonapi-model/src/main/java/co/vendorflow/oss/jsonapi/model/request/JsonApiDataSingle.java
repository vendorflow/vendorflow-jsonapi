package co.vendorflow.oss.jsonapi.model.request;

import co.vendorflow.oss.jsonapi.model.resource.JsonApiResource;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class JsonApiDataSingle<A, RM, M, R extends JsonApiResource<A, RM>> extends JsonApiDataDocument<R, M> {
    public static <A, RM, M, R extends JsonApiResource<A, RM>> JsonApiDataSingle<A, RM, M, R> of(R resource) {
        JsonApiDataSingle<A, RM, M, R> ds = new JsonApiDataSingle<>();
        ds.setData(resource);
        return ds;
    }
}
