package co.vendorflow.oss.jsonapi.model.request;

import java.util.ArrayList;
import java.util.Collection;

import co.vendorflow.oss.jsonapi.model.resource.JsonApiResourceId;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class JsonApiDataCollectionResourceIds<M> extends JsonApiDataDocument<Collection<JsonApiResourceId>, M> {
    {
        data = new ArrayList<>();
    }
}
