package co.vendorflow.oss.jsonapi.groovy.pipeline;

import static lombok.AccessLevel.PRIVATE;

import co.vendorflow.oss.jsonapi.model.resource.id.CompositeId2;
import co.vendorflow.oss.jsonapi.model.resource.id.CompositeId3;
import co.vendorflow.oss.jsonapi.model.resource.id.CompositeId4;
import io.vavr.Tuple2;
import io.vavr.Tuple3;
import io.vavr.Tuple4;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = PRIVATE)
public final class JsonApiVavrExtensions {

    static Tuple2<String, String> toTuple(CompositeId2 self) {
        return new Tuple2<>(self._1, self._2);
    }

    static Tuple3<String, String, String> toTuple(CompositeId3 self) {
        return new Tuple3<>(self._1, self._2, self._3);
    }

    static Tuple4<String, String, String, String> toTuple(CompositeId4 self) {
        return new Tuple4<>(self._1, self._2, self._3, self._4);
    }
}
