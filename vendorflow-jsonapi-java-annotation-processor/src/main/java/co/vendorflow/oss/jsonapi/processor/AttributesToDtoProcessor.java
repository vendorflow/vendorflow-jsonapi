package co.vendorflow.oss.jsonapi.processor;

import static java.util.stream.Collectors.toMap;
import static javax.lang.model.SourceVersion.RELEASE_9;
import static javax.tools.Diagnostic.Kind.ERROR;
import static javax.tools.StandardLocation.CLASS_OUTPUT;
import static org.apache.commons.lang3.StringUtils.endsWith;
import static org.apache.commons.lang3.StringUtils.isBlank;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.tools.FileObject;
import javax.tools.Diagnostic.Kind;

import co.vendorflow.oss.jsonapi.jackson.JsonApiTypeRegistration;
import co.vendorflow.oss.jsonapi.model.JsonApiAttributes;
import lombok.Value;

@SupportedAnnotationTypes("co.vendorflow.oss.jsonapi.model.JsonApiAttributes")
@SupportedSourceVersion(RELEASE_9)
public class AttributesToDtoProcessor extends AbstractProcessor {
    private static final String JAA_CLASS_NAME = JsonApiAttributes.class.getName();
    static final String MSO_CLASS_NAME = JAA_CLASS_NAME + ".MapStringObject"; // javac incorrectly uses . instead of $

    static final String SPI_TYPE_MANIFEST = "META-INF/services/" + JsonApiTypeRegistration.class.getCanonicalName();
    private final List<String> spiTypeRegistrations = new ArrayList<>();

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        annotations.stream()
                .filter(ann -> ann.toString().equals(JAA_CLASS_NAME))
                .map(roundEnv::getElementsAnnotatedWith)
                .flatMap(Set::stream)
                .map(TypeElement.class::cast)
                .forEach(this::generateResource);

        if (roundEnv.processingOver()) {
            finallyWriteSpiManifest();
        }

        return false;
    }


    void generateResource(TypeElement attrClass) {
        var rci = ResourceClassInfo.forElement(attrClass, processingEnv.getElementUtils());
        String resSource = generateDtoSource(attrClass, rci);
        try (var w = processingEnv.getFiler().createSourceFile(rci.getFqcn(), attrClass).openWriter()) {
            w.write(resSource);
        } catch (IOException e) {
            var error = "exception while generating JsonApiResource class for " + attrClass + ": " + e.getMessage();
            processingEnv.getMessager().printMessage(ERROR, error);
        }

        String resRegistrationSource = generateRegistrationSource(rci);
        String rfqcn = rci.getRegistrationFqcn();
        try (var w = processingEnv.getFiler().createSourceFile(rfqcn, attrClass).openWriter()) {
            w.write(resRegistrationSource);
            spiTypeRegistrations.add(rfqcn);
        } catch (IOException e) {
            var error = "exception while generating JsonApiTypeRegistration class " + rfqcn + ": " + e.getMessage();
            processingEnv.getMessager().printMessage(ERROR, error);
        }
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


    static String generateDtoSource(TypeElement attr, ResourceClassInfo rci) {
        //@formatter:off
        var sb = new StringBuilder();
        sb.append("package ").append(rci.packageName).append(";\n\n")
            .append("import javax.validation.constraints.NotNull;\n")
            .append("import javax.validation.Valid;\n")
            .append('\n')
            .append("@co.vendorflow.oss.jsonapi.model.JsonApiType(\"").append(rci.jsonApiType).append("\")\n")
            .append("@com.fasterxml.jackson.annotation.JsonTypeName(\"").append(rci.jsonApiType).append("\")\n")
            .append("@javax.annotation.Generated(\"").append(AttributesToDtoProcessor.class.getName()).append("\")\n")
            .append("public final class ").append(rci.simpleName).append(" extends co.vendorflow.oss.jsonapi.model.JsonApiResource<")
                .append(attr.getQualifiedName()).append(", ")
                .append(rci.metaTypeParameter).append("> {\n")
            .append("  @Override public String getType() { return \"").append(rci.jsonApiType).append("\"; }\n")
            .append("  @Override public void setAttributes(").append(attr.getQualifiedName()).append(" value) { super.setAttributes(value); } \n")
            ;

        if (!rci.attributesNullable) {
            sb.append("  @Override @Valid @NotNull public ").append(attr.getQualifiedName()).append(" getAttributes() { return super.getAttributes(); } \n");
        }

        sb
            .append("  @Override public void setMeta(").append(rci.metaTypeParameter).append(" value) { super.setMeta(value); } \n")
            .append("  @Override public String toString() { return new StringBuilder()")
                .append(".append(\"").append(rci.simpleName)
                    .append("[id=\")").append(".append(getId())")
                    .append(".append(\", attributes=\")").append(".append(getAttributes())")
                    .append(".append(\", links=\")").append(".append(getLinks())")
                    .append(".append(\", meta=\")").append(".append(getMeta())")
                .append(".append(']').toString(); } \n")
            ;

        sb.append("}\n")
            ;
        //@formatter:on
        return sb.toString();
    }


    static String generateRegistrationSource(ResourceClassInfo rci) {
        //@formatter:off
        var sb = new StringBuilder();
        sb.append("package ").append(rci.packageName).append(";\n\n")
            .append("@javax.annotation.Generated(\"").append(AttributesToDtoProcessor.class.getName()).append("\")\n")
            .append("public final class ").append(rci.getRegistrationSimpleName())
                .append(" implements co.vendorflow.oss.jsonapi.jackson.JsonApiTypeRegistration {\n")
            .append("  @Override public String namespace() { return \"\"; }\n")
            .append("  @Override public String typeName() { return \"").append(rci.jsonApiType).append("\"; }\n")
            .append("  @Override public Class<? extends co.vendorflow.oss.jsonapi.model.JsonApiResource<?, ?>> typeClass() { return ").append(rci.getFqcn()).append(".class ; }\n")
            .append("}\n")
        ;
        //@formatter:on

        return sb.toString();
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


    <F extends FileObject> F logUri(F fo) {
        processingEnv.getMessager().printMessage(Kind.NOTE, "writing JsonApiTypeRegistrations to " + fo.toUri());
        return fo;
    }

    void finallyWriteSpiManifest() {
        try(var out = logUri(processingEnv.getFiler().createResource(CLASS_OUTPUT, "", SPI_TYPE_MANIFEST)).openWriter()) {
            for (var type : spiTypeRegistrations) {
                out.write(type);
                out.write('\n');
            }
        } catch (IOException e) {
            processingEnv.getMessager().printMessage(ERROR, "error writing " + SPI_TYPE_MANIFEST + ": " + e.getMessage());
        }

        try {
            //Thread.sleep(5000);
        } catch (Exception e) {
        }
    }


    @Value
    static class ResourceClassInfo {
        String packageName;
        String simpleName;
        String jsonApiType;
        String metaTypeParameter;
        boolean attributesNullable;

        String getFqcn() {
            return packageName + '.' + simpleName;
        }

        String getRegistrationSimpleName() {
            return "$" + simpleName + "TypeRegistration";
        }

        String getRegistrationFqcn() {
            return packageName + "." + getRegistrationSimpleName();
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
