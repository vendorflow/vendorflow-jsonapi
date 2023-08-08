package co.vendorflow.oss.jsonapi.spring.web

import static co.vendorflow.oss.jsonapi.spring.web.JsonApiSortHandlerMethodArgumentResolverTest.CaptureController.BUILD_URL
import static co.vendorflow.oss.jsonapi.spring.web.JsonApiSortHandlerMethodArgumentResolverTest.CaptureController.HAS_DEFAULTS
import static co.vendorflow.oss.jsonapi.spring.web.JsonApiSortHandlerMethodArgumentResolverTest.CaptureController.PATH
import static org.springframework.data.domain.Sort.Direction.DESC
import static org.springframework.data.domain.Sort.Order.asc
import static org.springframework.data.domain.Sort.Order.desc
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Sort
import org.springframework.data.web.SortDefault
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder
import org.springframework.web.util.UriComponentsBuilder

import spock.lang.Issue

class JsonApiSortHandlerMethodArgumentResolverTest extends AbstractJsonApiSpringTest {
    public static final String JSON_API_SORT = 'sort'

    @Autowired
    CaptureController capture

    @Issue([
        'WKS-1218',
    ])
    void 'omitting sort query parameter with no defaults results in UNSORTED'() {
        when:
        mockMvc.perform(
                get(PATH)
                    .queryParam('stringQueryValue', 'unsortedStuff')
            )
            .andExpect(status().is2xxSuccessful())

        then:
        'unsortedStuff' == capture.stringQueryValue
        Sort.unsorted() == capture.sort
    }


    @Issue([
        'WKS-1218',
    ])
    void 'omitting sort query parameter with defaults results in default value'() {
        when:
        mockMvc.perform(get(PATH + HAS_DEFAULTS))
            .andExpect(status().is2xxSuccessful())

        then:
        Sort.by(DESC, 'propDef') == capture.sort
    }


    @Issue([
        'WKS-1218',
    ])
    def 'JSON:API sort parameter is parsed'() {
        when:
        mockMvc.perform(
                get(PATH)
                    .queryParam('stringQueryValue', 'jsonApi')
                    .queryParam(JSON_API_SORT, 'propA,-propB,propC')
            )
            .andExpect(status().is2xxSuccessful())

        then:
        'jsonApi' == capture.stringQueryValue
        Sort.by(asc('propA'), desc('propB'), asc('propC')) == capture.sort
    }


    @Issue([
        'WKS-1218',
    ])
    def 'repeated sort parameter is rejected'() {
        expect:
        mockMvc.perform(
                get(PATH)
                    .queryParam(JSON_API_SORT, 'propA,-propB,propC')
                    .queryParam(JSON_API_SORT, 'tooMany')
            )
            .andExpect(status().isBadRequest())
    }


    @Issue([
        'WKS-1218',
    ])
    def 'URI builder uses JSON:API sort convention'() {
        when:
        var body = mockMvc.perform(get(PATH + BUILD_URL))
            .andExpect(status().is2xxSuccessful())
            .andReturn().response.contentAsString

        var json = jsonSlurper.parseText(body)

        then:
        var params = UriComponentsBuilder.fromHttpUrl(json.url).build().queryParams
        ['builderValue'] == params.stringQueryValue
        ['up,-down'] == params[JSON_API_SORT]
    }


    @RestController
    @RequestMapping(path = CaptureController.PATH)
    static class CaptureController {
        public static final String PATH = '/jashmart'
        public static final String HAS_DEFAULTS = '/hasDefaults'
        public static final String BUILD_URL = '/build'

        String stringQueryValue
        Sort sort

        @GetMapping
        void capture(
                @RequestParam(required = false) String stringQueryValue,
                /* no SortDefault */ Sort sort
        ) {
            this.stringQueryValue = stringQueryValue
            this.sort = sort
        }


        @GetMapping(HAS_DEFAULTS)
        void captureDefault(
                @SortDefault(sort = 'propDef', direction = DESC) Sort sort
        ) {
            this.sort = sort
        }


        @GetMapping(BUILD_URL)
        def buildUrl(UriComponentsBuilder ucb) {
            [url: MvcUriComponentsBuilder
                    .fromMethodName(ucb, CaptureController, 'capture', 'builderValue', Sort.by(asc('up'), desc('down')))
                    .toUriString()]
        }
    }
}
