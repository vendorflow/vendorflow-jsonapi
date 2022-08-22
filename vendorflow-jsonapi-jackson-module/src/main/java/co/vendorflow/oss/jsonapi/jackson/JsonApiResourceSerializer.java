package co.vendorflow.oss.jsonapi.jackson;

import static org.apache.commons.lang3.StringUtils.isBlank;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import co.vendorflow.oss.jsonapi.model.HasJsonApiResourceId;

public class JsonApiResourceSerializer extends StdSerializer<HasJsonApiResourceId> {

    public JsonApiResourceSerializer() {
        this(HasJsonApiResourceId.class);
    }

    public JsonApiResourceSerializer(Class<HasJsonApiResourceId> t) {
        super(t);
    }

    @Override
    public void serialize(HasJsonApiResourceId value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        var jari = value.asResourceId();
        if (isBlank(jari.getType())) {
            throw JsonMappingException.from(gen, "no type found for " + value);
        }

        gen.writeStartObject();
        gen.writeString(jari.getType());
    }
}
