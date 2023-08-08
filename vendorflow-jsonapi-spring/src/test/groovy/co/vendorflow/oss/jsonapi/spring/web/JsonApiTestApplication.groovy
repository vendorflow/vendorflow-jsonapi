package co.vendorflow.oss.jsonapi.spring.web

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

import groovy.transform.CompileStatic
import groovy.transform.stc.POJO

@SpringBootApplication
@CompileStatic
@POJO
class JsonApiTestApplication {
    static void main(String... args) {
        SpringApplication.run(JsonApiTestApplication)
    }
}
