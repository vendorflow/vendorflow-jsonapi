package co.vendorflow.oss.jsonapi.spring.web

import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Import
import org.springframework.data.web.config.EnableSpringDataWebSupport

@TestConfiguration
@Import([
    JsonApiSpringDataWebConfiguration,
])
@EnableSpringDataWebSupport
class JsonApiTestConfiguration {
}
