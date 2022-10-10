package co.vendorflow.oss.jsonapi.jackson.mixin;

import static com.fasterxml.jackson.annotation.JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY;
import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_EMPTY;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

import co.vendorflow.oss.jsonapi.model.resource.JsonApiRelationship;
import co.vendorflow.oss.jsonapi.model.resource.JsonApiResource;
import co.vendorflow.oss.jsonapi.model.resource.JsonApiResourceId;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

public interface JsonApiRelationshipMixin extends JsonApiJacksonMixin {

    @JsonIgnore
    String getName();

    @JsonIgnore
    List<JsonApiResourceId> getLinked();

    @JsonIgnore
    List<JsonApiResource<?, ?>> getIncluded();

    @JsonInclude(NON_EMPTY)
    List<JsonApiResourceId> getData();

    @JsonIgnore
    boolean isSingleValued();


    @Getter
    @Setter
    @ToString
    public class DeserProxy {
        @JsonFormat(with = ACCEPT_SINGLE_VALUE_AS_ARRAY)
        List<JsonApiResourceId> data;

        public JsonApiRelationship toRelationship(String name) {
            return JsonApiRelationship.withoutSelfLink(name).linkTo(data);
        }
    }
}
