package co.vendorflow.oss.jsonapi.spring.web

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc

import groovy.json.JsonSlurper
import groovy.transform.CompileStatic
import groovy.transform.stc.POJO
import spock.lang.Specification

@SpringBootTest(classes = JsonApiTestApplication, properties = "logging.level.root: WARN")
@ActiveProfiles('test')
@AutoConfigureMockMvc
@CompileStatic
@POJO
class AbstractJsonApiSpringTest extends Specification {
    @Autowired
    MockMvc mockMvc

    protected static final JsonSlurper jsonSlurper = new JsonSlurper()
}
