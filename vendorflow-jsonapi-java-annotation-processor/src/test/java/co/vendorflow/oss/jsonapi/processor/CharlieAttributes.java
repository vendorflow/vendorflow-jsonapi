package co.vendorflow.oss.jsonapi.processor;

import java.util.List;

import co.vendorflow.oss.jsonapi.model.JsonApiAttributes;
import lombok.Data;

@JsonApiAttributes(type = "charlies", resourceTypeName = "ChuckRez", nullable = true)
@Data
public class CharlieAttributes {
    List<String> angels;
}
