package co.vendorflow.oss.jsonapi.model.resource;

import java.util.function.Consumer;

import javax.validation.Valid;
import javax.validation.constraints.Size;

import co.vendorflow.oss.jsonapi.model.HasJsonApiMeta;
import co.vendorflow.oss.jsonapi.model.links.HasJsonApiLinks;
import co.vendorflow.oss.jsonapi.model.links.JsonApiLinks;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public abstract class JsonApiResource<A, M> implements HasJsonApiMeta<M>, HasJsonApiLinks, HasJsonApiResourceId {

    public abstract String getType();

    @Size(min = 1)
    String id;

    @Valid
    A attributes;

    @Valid
    M meta;


    @Valid
    JsonApiLinks links = new JsonApiLinks();


    @Valid
    JsonApiRelationships relationships = new JsonApiRelationships();

    public JsonApiResource<A, M> relationships(Consumer<JsonApiRelationships> builder) {
        builder.accept(relationships);
        return this;
    }


    @Override
    public final JsonApiResourceId asResourceId() {
        return new JsonApiResourceId(getType(), getId());
    }
}
