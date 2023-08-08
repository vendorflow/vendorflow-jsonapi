package co.vendorflow.oss.jsonapi.spring.web

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.web.SortHandlerMethodArgumentResolver

class JsonApiSpringDataWebConfigurationTest extends AbstractJsonApiSpringTest {

    @Autowired
    SortHandlerMethodArgumentResolver sortResolver

    def "JSON:API resolver is registered"() {
        expect:
        sortResolver instanceof JsonApiSortHandlerMethodArgumentResolver
        mockMvc
    }
}
