package co.vendorflow.oss.jsonapi.model.links;

import java.net.URI;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
public abstract class JsonApiLink {
    String rel;
    URI href;


    public static LinkUri asUri(String rel, URI href) {
        var l = new LinkUri();
        l.setRel(rel);
        l.setHref(href);
        return l;
    }


    public static LinkObject asObject(String rel, URI href) {
        var l = new LinkObject();
        l.setRel(rel);
        l.setHref(href);
        return l;
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
