package co.vendorflow.oss.jsonapi.model.request;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collector;

import co.vendorflow.oss.jsonapi.model.resource.JsonApiResource;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class JsonApiDataCollection<A, RM, R extends JsonApiResource<A, RM>, M> extends JsonApiDataDocument<Collection<R>, M> {
    {
        data = new ArrayList<>();
    }


    public static <A, RM, R extends JsonApiResource<A, RM>, M> JsonApiDataCollection<A, RM, R, M> of(Collection<R> data) {
        JsonApiDataCollection<A, RM, R, M> dc = new JsonApiDataCollection<>();
        dc.setData(new ArrayList<>(data));
        return dc;
    }


    public static
    <A, RM, R extends JsonApiResource<A, RM>, M>
    Collector<R, ?, JsonApiDataCollection<A, RM, R, M>> toDataCollection() {
        return collectingAndThen(toList(), JsonApiDataCollection::of);
    }
}
