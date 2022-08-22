package co.vendorflow.oss.jsonapi.processor

import static javax.tools.JavaFileObject.Kind.CLASS

import java.util.regex.Matcher
import java.util.regex.Pattern

import com.google.testing.compile.Compilation

import groovy.transform.CompileDynamic
import groovy.transform.CompileStatic
import groovy.transform.stc.POJO
import javassist.ByteArrayClassPath
import javassist.ClassPath
import javassist.ClassPool
import javassist.Loader
import javassist.NotFoundException

@CompileStatic
@POJO
class CompilationClassPath implements ClassPath {
    static final Pattern CLASS_FILE_NAME = ~/^\/CLASS_OUTPUT\/(.*)\.class$/

    final List<ByteArrayClassPath> generatedClasses

    CompilationClassPath(Compilation c) {
        generatedClasses = c
            .generatedFiles()
            .findAll { it.kind == CLASS }
            .collect {
                def classname = fileToClassName(it.name)
                def bytecode = it.openInputStream().readAllBytes()
                new ByteArrayClassPath(classname, bytecode)
            }
            .asImmutable()
    }


    @CompileDynamic
    static String fileToClassName(String filename) {
        Matcher m = (filename =~ CLASS_FILE_NAME)
        if (!m) {
            throw new IllegalStateException("could not understand class filename $filename")
        }

        return m[0][1].replace('/', '.')
    }


    @Override
    InputStream openClassfile(String classname) throws NotFoundException {
        generatedClasses.findResult { it.openClassfile(classname) }
    }


    @Override
    URL find(String classname) {
        generatedClasses.findResult { it.find(classname) }
    }


    ClassPool toClassPool() {
        new ClassPool().tap { appendClassPath(this) }
    }


    ClassLoader toClassLoader(ClassLoader parent = CompilationClassPath.classLoader) {
        return new Loader(
            parent,
            toClassPool()
        )
    }
}
