package co.vendorflow.oss.jsonapi.model.request;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collector;

import co.vendorflow.oss.jsonapi.model.resource.JsonApiResource;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class JsonApiDataCollection<A, RM, M, R extends JsonApiResource<A, RM>> extends JsonApiDataDocument<Collection<R>, M> {
    {
        data = new ArrayList<>();
    }


    public static <A, RM, M, R extends JsonApiResource<A, RM>> JsonApiDataCollection<A, RM, M, R> fromData(List<R> data) {
        JsonApiDataCollection<A, RM, M, R> dc = new JsonApiDataCollection<>();
        dc.setData(data);
        return dc;
    }


    public static
    <A, RM, M, R extends JsonApiResource<A, RM>>
    Collector<R, ?, JsonApiDataCollection<A, RM, M, R>> toDataCollection() {
        return collectingAndThen(toList(), JsonApiDataCollection::fromData);
    }
}
