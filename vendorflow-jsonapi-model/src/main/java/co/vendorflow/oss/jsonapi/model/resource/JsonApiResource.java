package co.vendorflow.oss.jsonapi.model.resource;

import java.util.function.Consumer;

import javax.validation.Valid;
import javax.validation.constraints.Size;

import co.vendorflow.oss.jsonapi.model.HasJsonApiMeta;
import co.vendorflow.oss.jsonapi.model.links.HasJsonApiLinks;
import co.vendorflow.oss.jsonapi.model.links.JsonApiLinks;
import co.vendorflow.oss.jsonapi.model.resource.id.CompositeId;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString(of = { "id", "attributes", "relationships", "links", "meta" })
public abstract class JsonApiResource<A, M> implements HasJsonApiMeta<M>, HasJsonApiLinks, HasJsonApiResourceId {

    public abstract String getType();

    @Size(min = 1)
    String id;

    final void setId(String id) {
        this.id = id;
    }

    final void setId(CompositeId compositeId) {
        this.id = compositeId.toString();
    }


    @Valid
    A attributes;

    @Valid
    M meta;


    @Valid
    JsonApiLinks links = new JsonApiLinks();


    @Valid
    JsonApiRelationships relationships = new JsonApiRelationships();

    @SuppressWarnings("unchecked") // oh, for self types
    public <SELF extends JsonApiResource<A, M>> SELF relationships(Consumer<JsonApiRelationships> builder) {
        builder.accept(relationships);
        return (SELF) this;
    }


    @Override
    public final JsonApiResourceId asResourceId() {
        return new JsonApiResourceId(getType(), getId());
    }
}
