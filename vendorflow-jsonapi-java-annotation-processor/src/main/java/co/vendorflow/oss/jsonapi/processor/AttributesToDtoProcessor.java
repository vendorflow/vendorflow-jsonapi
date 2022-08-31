package co.vendorflow.oss.jsonapi.processor;

import static java.util.stream.Collectors.toMap;
import static javax.lang.model.SourceVersion.RELEASE_9;
import static org.apache.commons.lang3.StringUtils.endsWith;
import static org.apache.commons.lang3.StringUtils.isBlank;

import java.util.Map;
import java.util.Set;

import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;

import co.vendorflow.oss.jsonapi.model.resource.JsonApiAttributes;
import lombok.Value;

@SupportedAnnotationTypes("co.vendorflow.oss.jsonapi.model.resource.JsonApiAttributes")
@SupportedSourceVersion(RELEASE_9)
public class AttributesToDtoProcessor extends FreemarkerProcessor {
    private static final String JAA_CLASS_NAME = JsonApiAttributes.class.getName();
    static final String MSO_CLASS_NAME = JAA_CLASS_NAME + ".MapStringObject"; // javac incorrectly uses . instead of $

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        annotations.stream()
                .filter(ann -> ann.toString().equals(JAA_CLASS_NAME))
                .map(roundEnv::getElementsAnnotatedWith)
                .flatMap(Set::stream)
                .map(TypeElement.class::cast)
                .forEach(this::generateResourceAndRegistration);

        return false;
    }


    void generateResourceAndRegistration(TypeElement attrClass) {
        var rci = ResourceClassInfo.forElement(attrClass, processingEnv.getElementUtils());
        writeClass("JsonApiResource", rci.getFqcn(), Map.of("rci", rci, "attr", attrClass), attrClass);
    }

    static String resourceSimpleName(Map<String, Object> jaaMap, CharSequence simpleName) {
        String explicitName = (String) jaaMap.get("resourceTypeName");
        if (!isBlank(explicitName)) {
            return explicitName;
        }

        var baseName = endsWith(simpleName, "Attributes")
                ? simpleName.subSequence(0, simpleName.length() - 10)
                : simpleName;

        return baseName + (String) jaaMap.get("resourceSuffix");
    }


    static String metaTypeParameter(Map<String, Object> jaaMap) {
        var mc = jaaMap.get("meta");
        var mcs = mc.toString();
        return (mcs.equals(MSO_CLASS_NAME))
                ? "java.util.Map<String, Object>"
                : mcs;
    }


    static boolean attributesNullable(Map<String, Object> jaaMap) {
        var an = jaaMap.get("nullable");
        return Boolean.TRUE.equals(an);
    }


    static Map<String, Object> slurpJaa(TypeElement e, Elements elements) {
        return e.getAnnotationMirrors().stream()
                .filter(am -> am.getAnnotationType().toString().equals(JAA_CLASS_NAME))
                .findFirst()
                .map(elements::getElementValuesWithDefaults)
                .map(em -> em.entrySet().stream()
                        .collect(toMap(me -> me.getKey().getSimpleName().toString(), me -> me.getValue().getValue())))
                .orElseThrow(() -> new IllegalStateException("in processor for @JsonApiAttributes, but could not find the annotation"));
    }


    @Value
    public static class ResourceClassInfo {
        String packageName;
        String simpleName;
        String jsonApiType;
        String metaTypeParameter;
        boolean attributesNullable;

        public String getFqcn() {
            return packageName + '.' + simpleName;
        }

        static ResourceClassInfo forElement(TypeElement attr, Elements elements) {
            var jaaMap = slurpJaa(attr, elements);
            var packageName = elements.getPackageOf(attr).getQualifiedName().toString();
            var meta = metaTypeParameter(jaaMap);

            return new ResourceClassInfo(
                    packageName,
                    resourceSimpleName(jaaMap, attr.getSimpleName()),
                    (String) jaaMap.get("type"),
                    meta,
                    attributesNullable(jaaMap)
            );
        }
    }
}
