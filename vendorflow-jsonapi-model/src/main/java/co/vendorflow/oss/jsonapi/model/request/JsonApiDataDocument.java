package co.vendorflow.oss.jsonapi.model.request;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public abstract class JsonApiDataDocument<D, M> extends JsonApiDocument<D, M> {
    @Valid
    @NotNull
    protected D data;
}
