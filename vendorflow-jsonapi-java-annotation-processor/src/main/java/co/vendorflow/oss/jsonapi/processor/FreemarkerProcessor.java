package co.vendorflow.oss.jsonapi.processor;

import static freemarker.template.Configuration.VERSION_2_3_32;
import static javax.tools.Diagnostic.Kind.ERROR;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;

import javax.annotation.processing.AbstractProcessor;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;

import freemarker.cache.ClassTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.TemplateException;

abstract class FreemarkerProcessor extends AbstractProcessor {
    private static final Configuration FREEMARKER = freemarker();

    private static Configuration freemarker() {
        var cfg = new Configuration(VERSION_2_3_32);
        cfg.setTemplateLoader(new ClassTemplateLoader(FreemarkerProcessor.class, "templates"));
        cfg.setDefaultEncoding("UTF-8");
        return cfg;
    }

    /**
     * The processors in this library do not inspect the source code directly.
     */
    @Override
    public final SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latest();
    }

    protected String processFreemarker(String templateBase, Map<String, ?> model) throws IOException, TemplateException {
        var sw = new StringWriter();
        FREEMARKER.getTemplate(templateBase + ".ftl").process(model, sw);
        return sw.toString();
    }


    protected void writeClass(String classRole, String fqcn, Map<String, ?> model, TypeElement originating) {
        try {
            String source = processFreemarker(classRole, model);
            try (var w = processingEnv.getFiler().createSourceFile(fqcn, originating).openWriter()) {
                w.write(source);
            }
        } catch (IOException | TemplateException e) {
            var error = "exception while generating " + classRole + " class for " + originating + ": " + e.getMessage();
            processingEnv.getMessager().printMessage(ERROR, error);
        }
    }
}
