package co.vendorflow.oss.jsonapi.jackson.serdes;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdNodeBasedDeserializer;

import co.vendorflow.oss.jsonapi.model.links.JsonApiLink;
import co.vendorflow.oss.jsonapi.model.links.JsonApiLink.LinkUri;
import co.vendorflow.oss.jsonapi.model.links.JsonApiLink.LinkObject;

public class LinkValueDeserializer extends StdNodeBasedDeserializer<JsonApiLink> {

    public LinkValueDeserializer() {
        super(JsonApiLink.class);
    }

    @Override
    public JsonApiLink convert(JsonNode root, DeserializationContext ctx) throws IOException {
        switch(root.getNodeType()) {
        case STRING:
            return deserializeBare(root, ctx);
        case OBJECT:
            return deserializeObject(root, ctx);
        default:
            return ctx.reportInputMismatch(this, "expected JsonApiLink to be a STRING or OBJECT but was %s", root.getNodeType());
        }
    }


    // trying to apply @JsonCreator via a mixin does not appear to work
    LinkUri deserializeBare(JsonNode value, DeserializationContext ctx) throws IOException {
        String href = value.asText();
        try {
            URI uri = new URI(href);
            var b = new LinkUri();
            b.setHref(uri);
            return b;
        } catch (URISyntaxException e) {
            throw new JsonParseException(ctx.getParser(), "could not parse \"" + href + "\" as a URI", e);
        }
    }


    LinkObject deserializeObject(JsonNode value, DeserializationContext ctx) throws IOException {
        return ctx.readTreeAsValue(value, LinkObject.class);
    }
}
