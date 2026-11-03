package co.vendorflow.oss.jsonapi.model.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public abstract class JsonApiDataDocument<D, M> extends JsonApiDocument<D, M> {
    @Valid
    @NotNull
    protected D data;
}
