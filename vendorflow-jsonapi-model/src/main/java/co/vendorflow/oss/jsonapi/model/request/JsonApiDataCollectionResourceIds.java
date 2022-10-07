package co.vendorflow.oss.jsonapi.model.request;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import co.vendorflow.oss.jsonapi.model.resource.JsonApiResourceId;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class JsonApiDataCollectionResourceIds<M> extends JsonApiDataDocument<List<JsonApiResourceId>, M> {
    {
        data = new ArrayList<>();
    }


    public static <M> JsonApiDataCollectionResourceIds<M> of(Collection<JsonApiResourceId> ids) {
        var dcr = new JsonApiDataCollectionResourceIds<M>();
        dcr.data.addAll(ids);
        return dcr;
    }
}
