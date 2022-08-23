package ${rci.packageName};
import javax.validation.constraints.NotNull;
import javax.validation.Valid;

@co.vendorflow.oss.jsonapi.model.JsonApiType("${rci.jsonApiType}")
@com.fasterxml.jackson.annotation.JsonTypeName("${rci.jsonApiType}")
@javax.annotation.Generated("co.vendorflow.oss.jsonapi.processor.AttributesToDtoProcessor")
public final class ${rci.simpleName} 
    extends co.vendorflow.oss.jsonapi.model.JsonApiResource<
        ${attr.qualifiedName},
        ${rci.metaTypeParameter}
    >
{
  @Override public String getType() { return "${rci.jsonApiType}"; }
  
  @Override public void setAttributes(${attr.qualifiedName} value) { super.setAttributes(value); }
  <#if !rci.attributesNullable>
  @Override @Valid @NotNull public ${attr.qualifiedName} getAttributes() { return super.getAttributes(); }
  </#if>
  
  @Override public void setMeta(${rci.metaTypeParameter} value) { super.setMeta(value); }
  
  @Override public String toString() {
    return new StringBuilder("${rci.simpleName}")
      .append("[id=").append(getId())
      .append(", attributes=").append(getAttributes())
      .append(", links=").append(getLinks())
      .append(", meta=").append(getMeta())
      .append(']').toString();
  }
}
