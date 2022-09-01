package co.vendorflow.oss.jsonapi.model.request;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import co.vendorflow.oss.jsonapi.model.HasJsonApiMeta;
import co.vendorflow.oss.jsonapi.model.links.HasJsonApiLinks;
import co.vendorflow.oss.jsonapi.model.links.JsonApiLinks;
import lombok.Data;

@Data
public abstract class JsonApiDataTopLevel<D, M> implements HasJsonApiLinks, HasJsonApiMeta<M> {
    @Valid
    @NotNull
    protected D data;

    @Valid
    protected JsonApiLinks links = new JsonApiLinks();

    @Valid
    protected M meta;
}
