package co.vendorflow.oss.jsonapi.jackson.mixin;

import java.net.URI;

import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import co.vendorflow.oss.jsonapi.jackson.serdes.LinkValueDeserializer;

@JsonDeserialize(using = LinkValueDeserializer.class)
public interface JsonApiLinkMixin {

    public interface BareUriMixin {
        @JsonValue
        URI getHref();
    }

    @JsonDeserialize(using = JsonDeserializer.None.class) // don't inherit special handling from superclass
    public interface LinkObjectMixin {}
}
