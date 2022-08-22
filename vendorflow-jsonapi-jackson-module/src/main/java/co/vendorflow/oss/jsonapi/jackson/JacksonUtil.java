package co.vendorflow.oss.jsonapi.jackson;

import static lombok.AccessLevel.PRIVATE;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;

import lombok.NoArgsConstructor;

@NoArgsConstructor(access = PRIVATE)
public final class JacksonUtil {
    static void writeStringIfPresent(String name, String value, JsonGenerator gen) throws IOException {
        if (value != null) {
            gen.writeStringField(name, value);
        }
    }
}
