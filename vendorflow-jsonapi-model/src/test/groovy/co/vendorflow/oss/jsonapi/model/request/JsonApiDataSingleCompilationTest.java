package co.vendorflow.oss.jsonapi.model.request;

import java.util.Map;

public class JsonApiDataSingleCompilationTest {
    void compile1(JsonApiDataDocument<TestResource, Map<String, Object>> document) {
    }

    void compile2(JsonApiDataSingle<TestResource, Map<String, Object>> single) {
        compile1(single);
    }
}
