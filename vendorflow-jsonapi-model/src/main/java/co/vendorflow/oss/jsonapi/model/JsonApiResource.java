package co.vendorflow.oss.jsonapi.model;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_EMPTY;
import static com.fasterxml.jackson.annotation.JsonTypeInfo.Id.NAME;

import javax.validation.Valid;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@JsonTypeInfo(use = NAME, property = "type", visible = true)
@JsonInclude(value = NON_EMPTY)
@Getter
@Setter
@ToString
public abstract class JsonApiResource<A, M> implements HasJsonApiMeta<M>, HasJsonApiLinks, HasJsonApiResourceId {

    @JsonIgnore
    public abstract String getType();

    @Size(min = 1)
    String id;

    @Valid
    A attributes;

    @Valid
    M meta;

    @Valid
    JsonApiLinks links;

    @Override
    public final JsonApiResourceId asResourceId() {
        return new JsonApiResourceId(getType(), getId());
    }
}
