package co.vendorflow.oss.jsonapi.jackson.mixin;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.deser.std.StdDelegatingDeserializer;
import com.fasterxml.jackson.databind.util.Converter;
import com.fasterxml.jackson.databind.util.StdConverter;

import co.vendorflow.oss.jsonapi.jackson.mixin.JsonApiRelationshipMixin.DeserProxy;
import co.vendorflow.oss.jsonapi.jackson.mixin.JsonApiRelationshipsMixin.Deserializer;
import co.vendorflow.oss.jsonapi.model.resource.JsonApiRelationship;
import co.vendorflow.oss.jsonapi.model.resource.JsonApiRelationships;

@JsonDeserialize(using = Deserializer.class)
public interface JsonApiRelationshipsMixin extends JsonApiJacksonMixin {

    @JsonValue
    Map<String, JsonApiRelationship> getValues();

    @JsonCreator // jackson-databind#1820
    static JsonApiRelationships fromMap(Map<String, JsonApiRelationshipMixin.DeserProxy> values) {
        var jar = new JsonApiRelationships();
        values.entrySet().stream()
            .map(e -> e.getValue().toRelationship(e.getKey()))
            .forEach(jar::add);
        return jar;
    }


    public static class Deserializer extends StdDelegatingDeserializer<JsonApiRelationships> {
        public Deserializer() {
            super(new DeserConverter());
        }

        private Deserializer(
                com.fasterxml.jackson.databind.util.Converter<Object, JsonApiRelationships> converter,
                JavaType delegateType,
                JsonDeserializer<?> delegateDeserializer
        ) {
            super(converter, delegateType, delegateDeserializer);
        }

        @Override
        protected StdDelegatingDeserializer<JsonApiRelationships> withDelegate(
                Converter<Object, JsonApiRelationships> converter,
                JavaType delegateType,
                JsonDeserializer<?> delegateDeserializer
        ) {
            return new Deserializer(converter, delegateType, delegateDeserializer);
        }
    }


    static class DeserConverter extends StdConverter<Map<String, JsonApiRelationshipMixin.DeserProxy>, JsonApiRelationships> {
        @Override
        public JsonApiRelationships convert(Map<String, DeserProxy> values) {
            return fromMap(values);
        }
    }
}
