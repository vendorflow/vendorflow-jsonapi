package co.vendorflow.oss.jsonapi.processor;

import javax.validation.constraints.Min;
import javax.validation.constraints.Size;

import co.vendorflow.oss.jsonapi.model.resource.JsonApiAttributes;
import lombok.Data;

@JsonApiAttributes(type = "bravos", resourceSuffix = "Dto", meta = BravoAttributes.Meta.class)
@Data
public class BravoAttributes {
    @Size(max = 10)
    String bet;

    @Data
    public static class Meta {
        @Min(0)
        Integer applause;
    }
}
