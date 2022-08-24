package co.vendorflow.oss.jsonapi.model.links;

import java.net.URI;
import java.net.URISyntaxException;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
public abstract class JsonApiLink {
    String rel;
    URI href;


    public static LinkUri linkUri(String rel, URI href) {
        var l = new LinkUri();
        l.setRel(rel);
        l.setHref(href);
        return l;
    }


    public static LinkUri linkUri(String rel, String href) {
        return linkUri(rel, parseUri(href));
    }


    public static LinkObject linkObject(String rel, URI href) {
        var l = new LinkObject();
        l.setRel(rel);
        l.setHref(href);
        return l;
    }


    public static LinkObject linkObject(String rel, String href) {
        return linkObject(rel, parseUri(href));
    }


    private static URI parseUri(String uri) {
        try {
            return new URI(uri);
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("invalid URI: " + uri, e);
        }
    }


    @Data
    @ToString(callSuper = true)
    @EqualsAndHashCode(callSuper = true)
    public static final class LinkUri extends JsonApiLink {
    }


    @Data
    @ToString(callSuper = true)
    @EqualsAndHashCode(callSuper = true)
    public static final class LinkObject extends JsonApiLink {
    }
}
