package co.vendorflow.oss.jsonapi.jackson.serdes;

import java.util.Map;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.deser.std.StdDelegatingDeserializer;
import com.fasterxml.jackson.databind.util.StdConverter;

import co.vendorflow.oss.jsonapi.model.links.JsonApiLink;
import co.vendorflow.oss.jsonapi.model.links.JsonApiLinks;

public class JsonApiLinksDeserializer extends StdDelegatingDeserializer<JsonApiLinks> {

    public JsonApiLinksDeserializer() {
        super(new Converter());
    }


    private JsonApiLinksDeserializer(
            com.fasterxml.jackson.databind.util.Converter<Object, JsonApiLinks> converter,
            JavaType delegateType,
            JsonDeserializer<?> delegateDeserializer
    ) {
        super(converter, delegateType, delegateDeserializer);
    }


    @Override
    protected StdDelegatingDeserializer<JsonApiLinks> withDelegate(
            com.fasterxml.jackson.databind.util.Converter<Object, JsonApiLinks> converter,
            JavaType delegateType,
            JsonDeserializer<?> delegateDeserializer
    ) {
        return new JsonApiLinksDeserializer(converter, delegateType, delegateDeserializer);
    }


    public static class Converter extends StdConverter<Map<String, JsonApiLink>, JsonApiLinks> {
        @Override
        public JsonApiLinks convert(Map<String, JsonApiLink> value) {
            value.entrySet().stream()
                .filter(e -> e.getValue().getRel() == null)
                .forEach(e -> e.getValue().setRel(e.getKey()));

            var l = new JsonApiLinks();
            l.putAll(value);
            return l;
        }
    }
}
