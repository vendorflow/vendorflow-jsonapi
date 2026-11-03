package co.vendorflow.oss.jsonapi.model

import java.lang.module.ModuleFinder
import java.nio.file.Path

import spock.lang.Specification

class ModulePathTest extends Specification {
    private static final String MODULE = 'co.vendorflow.oss.jsonapi.model'

    def 'descriptor resolves on the module path'() {
        given:
        var modulePath = System.getProperty('surefire.test.class.path')
            .split(File.pathSeparator)
            .collect { Path.of(it) } as Path[]

        when:
        var resolved = ModuleLayer.boot().configuration()
            .resolve(ModuleFinder.of(modulePath), ModuleFinder.of(), [MODULE])

        then:
        resolved.findModule(MODULE).present
    }
}
