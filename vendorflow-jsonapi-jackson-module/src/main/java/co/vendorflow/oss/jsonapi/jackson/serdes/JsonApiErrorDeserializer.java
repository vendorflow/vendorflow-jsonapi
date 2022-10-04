package co.vendorflow.oss.jsonapi.jackson.serdes;

import java.util.LinkedHashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.deser.std.StdDelegatingDeserializer;
import com.fasterxml.jackson.databind.util.StdConverter;

import co.vendorflow.oss.jsonapi.model.error.JsonApiError;
import co.vendorflow.oss.jsonapi.model.links.JsonApiLinks;
import lombok.Getter;
import lombok.Setter;

public class JsonApiErrorDeserializer extends StdDelegatingDeserializer<JsonApiError> {

    public JsonApiErrorDeserializer() {
        super(new Converter());
    }


    private JsonApiErrorDeserializer(
            com.fasterxml.jackson.databind.util.Converter<Object, JsonApiError> converter,
            JavaType delegateType,
            JsonDeserializer<?> delegateDeserializer
    ) {
        super(converter, delegateType, delegateDeserializer);
    }


    @Override
    protected StdDelegatingDeserializer<JsonApiError> withDelegate(
            com.fasterxml.jackson.databind.util.Converter<Object, JsonApiError> converter,
            JavaType delegateType,
            JsonDeserializer<?> delegateDeserializer
    ) {
        return new JsonApiErrorDeserializer(converter, delegateType, delegateDeserializer);
    }


    @Getter
    @Setter
    public static class JsonApiErrorProxy {
        String status;
        String code;
        String title;
        String detail;
        String id;
        JsonApiLinks links = new JsonApiLinks();
        Map<String, Object> meta = new LinkedHashMap<>();

        JsonApiError toError() {
            var e = new JsonApiError(Integer.valueOf(status), code, title, detail, id);
            e.setLinks(links);
            e.setMeta(meta);
            return e;
        }
    }

    public static class Converter extends StdConverter<JsonApiErrorProxy, JsonApiError> {
        @Override
        public JsonApiError convert(JsonApiErrorProxy value) {
            return value.toError();
        }
    }
}
