package ${rci.packageName};

import javax.annotation.Generated;
import javax.validation.constraints.NotNull;
import javax.validation.Valid;

import co.vendorflow.oss.jsonapi.model.resource.JsonApiResource;
import co.vendorflow.oss.jsonapi.model.resource.JsonApiResourceId;
import co.vendorflow.oss.jsonapi.model.resource.JsonApiType;

import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonApiType(${rci.simpleName}.TYPE)
@JsonTypeName(${rci.simpleName}.TYPE)
@Generated("co.vendorflow.oss.jsonapi.processor.AttributesToDtoProcessor")
public final class ${rci.simpleName} 
    extends JsonApiResource<
        ${attrClassQualifiedName},
        ${rci.metaTypeParameter}
    >
{
  public static final String TYPE = "${rci.jsonApiType}";

  @Override public String getType() { return TYPE; }
  
  public static JsonApiResourceId id(Object id) { return JsonApiResourceId.of(TYPE, id); }
  
  @Override public void setAttributes(${attrClassQualifiedName} value) { super.setAttributes(value); }
  <#if !rci.attributesNullable>
  @Override @Valid @NotNull public ${attrClassQualifiedName} getAttributes() { return super.getAttributes(); }
  </#if>
  
  @Override public void setMeta(${rci.metaTypeParameter} value) { super.setMeta(value); }
  
  @Override public String toString() {
    return new StringBuilder("${rci.simpleName}")
      .append("[id=").append(getId())
      .append(", attributes=").append(getAttributes())
      .append(", relationships=").append(getRelationships())
      .append(", links=").append(getLinks())
      .append(", meta=").append(getMeta())
      .append(']').toString();
  }
}
