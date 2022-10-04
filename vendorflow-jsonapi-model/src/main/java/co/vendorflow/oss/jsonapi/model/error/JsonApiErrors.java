package co.vendorflow.oss.jsonapi.model.error;

import static co.vendorflow.oss.jsonapi.model.error.JsonApiError.META_RESOURCE;
import static lombok.AccessLevel.PRIVATE;

import co.vendorflow.oss.jsonapi.model.resource.JsonApiResourceId;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = PRIVATE)
public final class JsonApiErrors {
    public static final String NOT_FOUND_CODE = "http.client.NOT_FOUND";

    public static JsonApiError notFound() {
        return new JsonApiError(404, NOT_FOUND_CODE);
    }

    public static JsonApiError notFound(JsonApiResourceId notFoundResource) {
        var e = notFound();
        e.getMeta().put(META_RESOURCE, notFoundResource);
        return e;
    }


    public static final String INTERNAL_SERVER_ERROR_CODE = "http.server.INTERNAL_SERVER_ERROR";

    public static JsonApiError internalServerError() {
        return new JsonApiError(500, INTERNAL_SERVER_ERROR_CODE);
    }


    public static JsonApiError unprocessableEntity(String code, String title, String detail) {
        return new JsonApiError(422, code, title, detail);
    }
}
