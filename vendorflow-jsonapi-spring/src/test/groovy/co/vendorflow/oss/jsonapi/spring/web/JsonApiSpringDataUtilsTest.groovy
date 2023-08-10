package co.vendorflow.oss.jsonapi.spring.web

import static org.springframework.data.domain.Sort.Order.asc
import static org.springframework.data.domain.Sort.Order.desc

import org.springframework.data.domain.Sort
import org.springframework.data.domain.Sort.Order

import spock.lang.Rollup
import spock.lang.Specification

@Rollup
class JsonApiSpringDataUtilsTest extends Specification {

    def 'individual parts are parsed into Orders'(String part, Order order) {
        expect:
        order == JsonApiSpringDataUtils.parseParameterPart(part)

        where:
        part       || order
        'asdf'     || asc('asdf')
        '-asdf'    || desc('asdf')
        'abc.def'  || asc('abc.def')
        '-abc.def' || desc('abc.def')
    }


    def 'composite parameters are decomposed'(String parameter, Sort expected) {
        expect:
        expected == JsonApiSpringDataUtils.parseSortParameter(parameter)

        where:
        parameter           || expected
        null                || Sort.unsorted()
        ''                  || Sort.unsorted()
        'abc,-def.ghi,jkl'  || Sort.by(asc('abc'), desc('def.ghi'), asc('jkl'))
        '-abc,def.ghi,-jkl' || Sort.by(desc('abc'), asc('def.ghi'), desc('jkl'))
    }


    def 'Sort objects are serialized into parameters'(Sort sort, String expected) {
        expect:
        expected == JsonApiSpringDataUtils.parameterValue(sort)

        where:
        sort                                              || expected
        Sort.unsorted()                                   || ''
        Sort.by(asc('abc'), desc('def.ghi'), asc('jkl'))  || 'abc,-def.ghi,jkl'
        Sort.by(desc('abc'), asc('def.ghi'), desc('jkl')) || '-abc,def.ghi,-jkl'
    }
}
