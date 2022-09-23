package co.vendorflow.oss.jsonapi.groovy.transform

import static java.lang.Long.MAX_VALUE
import static java.lang.reflect.Modifier.FINAL
import static java.lang.reflect.Modifier.PUBLIC
import static java.lang.reflect.Modifier.STATIC
import static java.nio.channels.Channels.newReader
import static java.nio.channels.Channels.newWriter
import static java.nio.charset.StandardCharsets.UTF_8
import static org.codehaus.groovy.ast.ClassHelper.OBJECT_TYPE
import static org.codehaus.groovy.ast.ClassHelper.STRING_TYPE
import static org.codehaus.groovy.ast.ClassHelper.make
import static org.codehaus.groovy.ast.tools.GeneralUtils.args
import static org.codehaus.groovy.ast.tools.GeneralUtils.asX
import static org.codehaus.groovy.ast.tools.GeneralUtils.classX
import static org.codehaus.groovy.ast.tools.GeneralUtils.constX
import static org.codehaus.groovy.ast.tools.GeneralUtils.ctorX
import static org.codehaus.groovy.ast.tools.GeneralUtils.param
import static org.codehaus.groovy.ast.tools.GeneralUtils.returnS
import static org.codehaus.groovy.ast.tools.GeneralUtils.varX

import java.nio.channels.FileChannel
import java.nio.file.Path

import org.codehaus.groovy.ast.ASTNode
import org.codehaus.groovy.ast.AnnotationNode
import org.codehaus.groovy.ast.ClassNode
import org.codehaus.groovy.ast.MethodNode
import org.codehaus.groovy.ast.Parameter
import org.codehaus.groovy.ast.expr.ConstantExpression
import org.codehaus.groovy.control.SourceUnit
import org.codehaus.groovy.macro.transform.MacroClass
import org.codehaus.groovy.transform.AbstractASTTransformation
import org.codehaus.groovy.transform.GroovyASTTransformation

import co.vendorflow.oss.jsonapi.jackson.JsonApiTypeRegistration
import co.vendorflow.oss.jsonapi.model.resource.JsonApiResourceId
import co.vendorflow.oss.jsonapi.model.resource.JsonApiType
import co.vendorflow.oss.jsonapi.processor.support.TypeRegistrationClassInfo
import groovy.transform.CompileDynamic
import groovy.transform.CompileStatic
import groovy.transform.stc.POJO

@GroovyASTTransformation
@CompileStatic
@POJO
class JsonApiTypeAstTransformation extends AbstractASTTransformation {
    private static final Path SPI_PATH = Path.of('META-INF', 'services', JsonApiTypeRegistration.name)
    private static final ClassNode INTERFACE_JATR = make(JsonApiTypeRegistration)
    private static final ClassNode ANNOTATION_CS = make(CompileStatic)
    private static final ClassNode ANNOTATION_POJO = make(POJO)

    private static final ClassNode TYPE_JARI = make(JsonApiResourceId)
    private static final Parameter OBJECT_PARAM = param(OBJECT_TYPE, 'value')


    @Override
    String getAnnotationName() { JsonApiType.simpleName }


    @Override
    void visit(ASTNode[] nodes, SourceUnit source) {
        init(nodes, source)
        ClassNode resource = nodes[1] as ClassNode

        var trci = trci(nodes[0] as AnnotationNode, resource)

        var components = buildTypeComponents(trci)
        if (! resource.getField('TYPE')) {
            resource.addField(components.getField('TYPE'))
        }
        if (resource.getMethod('getType')?.abstract) {
            resource.addMethod(components.getMethod('getType'))
        }
        if (! resource.getMethod('id', OBJECT_PARAM)) {
            resource.addMethod(buildStaticIdMethod(trci))
        }

        var trcn = buildRegistrationClass(trci)
        source.AST.addClass(trcn)


        var outDir = source.AST.unit.config.targetDirectory
        if (!outDir) {
            // transformation is being run "speculatively" and not as part of a live build, so skip processing.
            return
        }

        var spiFile = outDir.toPath().resolve(SPI_PATH).toFile()
        synchronized (JsonApiTypeAstTransformation) { // file locks apply to the entire JVM
            try (def chan = channelFor(spiFile)) {
                chan.lock(0, MAX_VALUE, true)
                var existing = newReader(chan, UTF_8).iterator().toSet() // readLines "helpfully" closes the Channel

                if (trci.fqcn !in existing) {
                    def w = newWriter(chan, UTF_8)
                    w.println(trci.fqcn)
                    w.flush()
                }
            } catch (IOException e) {
                source.errorCollector.addException(e, source)
            }
        }
    }


    FileChannel channelFor(File spiFile) throws IOException {
        spiFile.parentFile?.mkdirs()
        return new RandomAccessFile(spiFile, 'rw').channel
    }


    static TypeRegistrationClassInfo trci(AnnotationNode an, ClassNode cn) {
        new TypeRegistrationClassInfo(
            cn.packageName,
            cn.nameWithoutPackage,
            (an.getMember('value') as ConstantExpression).value as String
        )
    }


    @CompileDynamic
    ClassNode buildTypeComponents(TypeRegistrationClassInfo trci) {
        var jat = constX(trci.jsonApiType)

        return new MacroClass() {
            class ResourceTypeComponents {
                public static final java.lang.String TYPE = $v { jat }
                java.lang.String getType() { $v { jat } }
            }
        }
    }


    MethodNode buildStaticIdMethod(TypeRegistrationClassInfo trci) { // GROOVY-10768
        new MethodNode(
            'id',
            PUBLIC | STATIC ,
            TYPE_JARI,
            [OBJECT_PARAM] as Parameter[],
            ClassNode.EMPTY_ARRAY,
            returnS(ctorX(TYPE_JARI, args(constX(trci.jsonApiType), asX(STRING_TYPE, varX(OBJECT_PARAM)))))
        )
    }


    @CompileDynamic
    ClassNode buildRegistrationClass(TypeRegistrationClassInfo trci) {
        return new MacroClass() {
            class TypeReg {
                java.lang.String namespace() { $v { constX(trci.namespace) } }
                java.lang.String typeName() { $v { constX(trci.jsonApiType) } }
                java.lang.Class typeClass() { $v { classX(make(trci.resourceFqcn)) } }
            }
        }.tap {
            name = trci.fqcn
            addInterface INTERFACE_JATR
            addAnnotation ANNOTATION_CS
            addAnnotation ANNOTATION_POJO
        }
    }
}
