package co.vendorflow.oss.jsonapi.processor;

import static freemarker.template.Configuration.VERSION_2_3_31;
import static java.util.stream.Collectors.toMap;
import static javax.lang.model.SourceVersion.RELEASE_9;
import static javax.tools.Diagnostic.Kind.ERROR;
import static javax.tools.StandardLocation.CLASS_OUTPUT;
import static org.apache.commons.lang3.StringUtils.endsWith;
import static org.apache.commons.lang3.StringUtils.isBlank;

import java.io.IOException;
import java.io.StringWriter;
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
import javax.tools.Diagnostic.Kind;
import javax.tools.FileObject;

import co.vendorflow.oss.jsonapi.jackson.JsonApiTypeRegistration;
import co.vendorflow.oss.jsonapi.model.JsonApiAttributes;
import freemarker.cache.ClassTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.TemplateException;
import lombok.Value;

@SupportedAnnotationTypes("co.vendorflow.oss.jsonapi.model.JsonApiAttributes")
@SupportedSourceVersion(RELEASE_9)
public class AttributesToDtoProcessor extends AbstractProcessor {
    private static final String JAA_CLASS_NAME = JsonApiAttributes.class.getName();
    static final String MSO_CLASS_NAME = JAA_CLASS_NAME + ".MapStringObject"; // javac incorrectly uses . instead of $

    private static final Configuration FREEMARKER = freemarker();

    static final String SPI_TYPE_MANIFEST = "META-INF/services/" + JsonApiTypeRegistration.class.getCanonicalName();
    private final List<String> spiTypeRegistrations = new ArrayList<>();

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        annotations.stream()
                .filter(ann -> ann.toString().equals(JAA_CLASS_NAME))
                .map(roundEnv::getElementsAnnotatedWith)
                .flatMap(Set::stream)
                .map(TypeElement.class::cast)
                .forEach(this::generateResourceAndRegistration);

        if (roundEnv.processingOver()) {
            finallyWriteSpiManifest();
        }

        return false;
    }


    void generateResourceAndRegistration(TypeElement attrClass) {
        var rci = ResourceClassInfo.forElement(attrClass, processingEnv.getElementUtils());

        writeClass("JsonApiResource", rci.getFqcn(), attrClass, rci);

        String rfqcn = rci.getRegistrationFqcn();
        writeClass("JsonApiTypeRegistration", rfqcn, attrClass, rci);
        spiTypeRegistrations.add(rfqcn);
    }


    void writeClass(String classRole, String fqcn, TypeElement attrClass, ResourceClassInfo rci) {
        var model = Map.of("rci", rci, "attr", attrClass);
        try {
            String source = processFreemarker(classRole, model);
            try (var w = processingEnv.getFiler().createSourceFile(fqcn, attrClass).openWriter()) {
                w.write(source);
            }
        } catch (IOException | TemplateException e) {
            var error = "exception while generating " + classRole + " class for " + attrClass + ": " + e.getMessage();
            processingEnv.getMessager().printMessage(ERROR, error);
        }
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
    }


    String processFreemarker(String templateBase, Map<String, Object> model) throws IOException, TemplateException {
        var sw = new StringWriter();
        FREEMARKER.getTemplate(templateBase + ".ftl").process(model, sw);
        return sw.toString();
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


    <F extends FileObject> F logUri(F fo) {
        processingEnv.getMessager().printMessage(Kind.NOTE, "writing JsonApiTypeRegistrations to " + fo.toUri());
        return fo;
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

        public String getRegistrationSimpleName() {
            return "$" + simpleName + "TypeRegistration";
        }

        public String getRegistrationFqcn() {
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


    static Configuration freemarker() {
        var cfg = new Configuration(VERSION_2_3_31);
        cfg.setTemplateLoader(new ClassTemplateLoader(AttributesToDtoProcessor.class, "templates"));
        cfg.setDefaultEncoding("UTF-8");
        return cfg;
    }
}
