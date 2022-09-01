package co.vendorflow.oss.jsonapi.model.request;

import java.util.ArrayList;
import java.util.Collection;

import co.vendorflow.oss.jsonapi.model.resource.JsonApiResource;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class JsonApiDataCollection<A, RM, M, R extends JsonApiResource<A, RM>> extends JsonApiDataTopLevel<Collection<R>, M> {
    {
        data = new ArrayList<>();
    }
}
