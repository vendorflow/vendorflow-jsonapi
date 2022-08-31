package co.vendorflow.oss.jsonapi.processor;

import co.vendorflow.oss.jsonapi.model.resource.JsonApiAttributes;
import lombok.Data;

@JsonApiAttributes(type = "alphas")
@Data
public class AlphaAttributes {
    String aleph;
}
