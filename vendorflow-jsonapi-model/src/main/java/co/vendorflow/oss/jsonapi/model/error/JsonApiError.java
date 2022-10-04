package co.vendorflow.oss.jsonapi.model.error;

import java.util.LinkedHashMap;
import java.util.Map;

import co.vendorflow.oss.jsonapi.model.HasJsonApiMeta;
import co.vendorflow.oss.jsonapi.model.links.HasJsonApiLinks;
import co.vendorflow.oss.jsonapi.model.links.JsonApiLinks;
import co.vendorflow.oss.jsonapi.model.request.JsonApiErrorDocument;
import lombok.Data;
import lombok.NonNull;

@Data
public class JsonApiError implements HasJsonApiMeta<Map<String, Object>>, HasJsonApiLinks {
    @NonNull
    final Integer status;

    @NonNull
    final String code;

    String title;

    String detail;

    String id;

    JsonApiLinks links = new JsonApiLinks();


    public static final String META_RESOURCE = "resource";

    Map<String, Object> meta = new LinkedHashMap<>();


    public JsonApiError(Integer status, String code) {
        this(status, code, null);
    }

    public JsonApiError(Integer status, String code, String title) {
        this(status, code, title, null);
    }

    public JsonApiError(Integer status, String code, String title, String detail) {
        this(status, code, title, detail, null);
    }

    public JsonApiError(Integer status, String code, String title, String detail, String id) {
        this.status = status;
        this.code = code;
        this.title = title;
        this.detail = detail;
        this.id = id;
    }

    public String getStatusAsString() {
        return (status == null) ? null : status.toString();
    }

    @Override
    public String toString() {
        var sb = new StringBuilder("JsonApiError[").append(status).append(' ').append(code);
        if (detail != null) {
            sb.append(": ").append(detail);
        } else if (title != null) {
            sb.append(": ").append(title);
        }
        if (id != null) {
            sb.append("; ").append(id);
        }
        sb.append(']');
        return sb.toString();
    }


    public <R> JsonApiErrorDocument<R> asDocument() {
        return new JsonApiErrorDocument<R>().addError(this);
    }


    public static JsonApiError of(Throwable t) {
        if (t instanceof IsJsonApiError) {
            return ((IsJsonApiError) t).asJsonApiError();
        }

        var e = JsonApiErrors.internalServerError();
        e.setDetail(t.getClass().getName() + ": " + t.getMessage());
        return e;
    }


    public static JsonApiError fromStringStatus(String status, String code, String title, String detail, String id) {
        return new JsonApiError(Integer.valueOf(status), code, title, detail, id);
    }
}
