package co.vendorflow.oss.jsonapi.model.resource.id

import static java.lang.Integer.MAX_VALUE
import static java.util.UUID.randomUUID
import static org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric
import static org.apache.commons.lang3.RandomUtils.nextInt

import groovy.transform.CompileStatic
import groovy.transform.stc.POJO
import spock.lang.Specification

class CompositeId__MappingTest extends Specification {

    Entity e = new Entity(
        a: randomAlphanumeric(10),
        b: nextInt(0, MAX_VALUE),
        c: randomUUID(),
        d: randomAlphanumeric(11)
    )


    def 'mapping to CompositeId2'() {
        given:
        final function = CompositeId.mapping(Entity::getB, Entity::getC)

        when:
        final id = function.apply(e)

        then:
        "$e.b--$e.c" == id.toString()

        and:
        id == CompositeId2.parse(id.toString())
    }


    def 'mapping to CompositeId3'() {
        given:
        final function = CompositeId.mapping(Entity::getD, Entity::getA, Entity::getC)

        when:
        final id = function.apply(e)

        then:
        "$e.d--$e.a--$e.c" == id.toString()

        and:
        id == CompositeId3.parse(id.toString())
    }


    def 'mapping to CompositeId4'() {
        given:
        final function = CompositeId.mapping(Entity::getB, Entity::getD, Entity::getA, Entity::getC)

        when:
        final id = function.apply(e)

        then:
        "$e.b--$e.d--$e.a--$e.c" == id.toString()

        and:
        id == CompositeId4.parse(id.toString())
    }


    @CompileStatic
    @POJO
    static class Entity {
        String a
        Integer b
        UUID c
        String d
    }

}
