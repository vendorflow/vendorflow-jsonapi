package co.vendorflow.oss.jsonapi.processor.support;

import lombok.Value;

@Value
public class TypeRegistrationClassInfo {
    String namespace = "";
    String packageName;
    String resourceClassSimpleName;
    String jsonApiType;

    public String getSimpleName() {
        return "$" + resourceClassSimpleName + "TypeRegistration";
    }

    public String getFqcn() {
        return packageName + "." + getSimpleName();
    }

    public String getResourceFqcn() {
        return packageName + "." + resourceClassSimpleName;
    }
}
