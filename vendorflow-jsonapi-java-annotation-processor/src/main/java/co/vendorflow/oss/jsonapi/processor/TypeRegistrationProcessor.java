package co.vendorflow.oss.jsonapi.processor;

import static javax.lang.model.SourceVersion.RELEASE_9;
import static javax.tools.Diagnostic.Kind.ERROR;
import static javax.tools.Diagnostic.Kind.NOTE;
import static javax.tools.StandardLocation.CLASS_OUTPUT;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.tools.FileObject;

import co.vendorflow.oss.jsonapi.jackson.JsonApiTypeRegistration;
import co.vendorflow.oss.jsonapi.model.resource.JsonApiType;
import lombok.Value;

@SupportedAnnotationTypes("co.vendorflow.oss.jsonapi.model.resource.JsonApiType")
@SupportedSourceVersion(RELEASE_9)
public class TypeRegistrationProcessor extends FreemarkerProcessor {
    private static final String JAT_CLASS_NAME = JsonApiType.class.getName();

    static final String SPI_TYPE_MANIFEST = "META-INF/services/" + JsonApiTypeRegistration.class.getCanonicalName();
    private final List<String> spiTypeRegistrations = new ArrayList<>();

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        annotations.stream()
                .filter(ann -> ann.toString().equals(JAT_CLASS_NAME))
                .map(roundEnv::getElementsAnnotatedWith)
                .flatMap(Set::stream)
                .map(TypeElement.class::cast)
                .forEach(this::generateRegistration);

        if (roundEnv.processingOver()) {
            finallyWriteSpiManifest();
        }

        return false;
    }


    void generateRegistration(TypeElement resourceClass) {
        var tr = TypeRegistrationClassInfo.forElement(resourceClass, processingEnv.getElementUtils());

        writeClass("JsonApiTypeRegistration", tr.getFqcn(), Map.of("tr", tr), resourceClass);
        spiTypeRegistrations.add(tr.getFqcn());
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

    <F extends FileObject> F logUri(F fo) {
        processingEnv.getMessager().printMessage(NOTE, "writing JsonApiTypeRegistrations to " + fo.toUri());
        return fo;
    }


    @Value
    public static class TypeRegistrationClassInfo {
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

        static TypeRegistrationClassInfo forElement(TypeElement res, Elements elements) {
            var packageName = elements.getPackageOf(res).getQualifiedName().toString();
            var jsonApiType = extractJsonApiType(res);

            return new TypeRegistrationClassInfo(
                    packageName,
                    res.getSimpleName().toString(),
                    jsonApiType
            );
        }
    }


    static String extractJsonApiType(TypeElement resourceClass) {
        return resourceClass.getAnnotationMirrors().stream()
            .filter(am -> am.getAnnotationType().toString().equals(JAT_CLASS_NAME))
            .findFirst()
            .map(AnnotationMirror::getElementValues)
            .flatMap(ev -> ev.entrySet().stream()
                    .filter(e -> e.getKey().getSimpleName().contentEquals("value"))
                    .map(e -> e.getValue().getValue())
                    .map(String.class::cast)
                    .findAny()
            )
            .orElseThrow(() -> new IllegalStateException("no JsonApiType annotation found"));
    }
}
