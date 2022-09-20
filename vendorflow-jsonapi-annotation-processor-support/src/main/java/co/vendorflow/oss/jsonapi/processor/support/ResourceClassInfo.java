package co.vendorflow.oss.jsonapi.processor.support;

import static org.apache.commons.lang3.StringUtils.endsWith;
import static org.apache.commons.lang3.StringUtils.isBlank;

import co.vendorflow.oss.jsonapi.model.resource.JsonApiAttributes;
import lombok.Value;

@Value
public class ResourceClassInfo {
    public static final String JAA_CLASS_NAME = JsonApiAttributes.class.getName();
    public static final String MSO_CLASS_NAME = JAA_CLASS_NAME + ".MapStringObject"; // javac incorrectly uses . instead of $
    public static final String ATTR_SUFFIX = "Attributes";

    String packageName;
    String simpleName;
    String jsonApiType;
    String metaTypeParameter;
    boolean attributesNullable;

    public String getFqcn() {
        return packageName + '.' + simpleName;
    }


    public static String resourceSimpleName(CharSequence explicitName, CharSequence attrSimpleName, CharSequence resourceSuffix) {
        if (!isBlank(explicitName)) {
            return explicitName.toString();
        }

        var baseName = endsWith(attrSimpleName, ATTR_SUFFIX)
                ? attrSimpleName.subSequence(0, attrSimpleName.length() - ATTR_SUFFIX.length())
                : attrSimpleName;

        return new StringBuilder(baseName).append(resourceSuffix).toString();
    }
}
