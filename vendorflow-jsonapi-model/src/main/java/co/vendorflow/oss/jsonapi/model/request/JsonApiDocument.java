package co.vendorflow.oss.jsonapi.model.request;

import javax.validation.Valid;

import co.vendorflow.oss.jsonapi.model.HasJsonApiMeta;
import co.vendorflow.oss.jsonapi.model.links.HasJsonApiLinks;
import co.vendorflow.oss.jsonapi.model.links.JsonApiLinks;
import lombok.Data;

@Data
public abstract class JsonApiDocument<D, M> implements HasJsonApiLinks, HasJsonApiMeta<M> {
    @Valid
    protected JsonApiLinks links = new JsonApiLinks();

    @Valid
    protected M meta;
}
