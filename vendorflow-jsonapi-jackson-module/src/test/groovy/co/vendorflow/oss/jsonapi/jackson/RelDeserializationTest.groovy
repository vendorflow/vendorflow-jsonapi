package co.vendorflow.oss.jsonapi.jackson

import static co.vendorflow.oss.jsonapi.jackson.MapTestResource.mtrid

import co.vendorflow.oss.jsonapi.model.resource.JsonApiRelationship
import co.vendorflow.oss.jsonapi.model.resource.JsonApiResourceId

class RelDeserializationTest extends JacksonTest {
    def 'a resource with no relationship has empty rather than null property'() {
        given:
        def json = """{ "type": "test_map", "id": "no_rels" }"""

        when:
        MapTestResource obj = deserialize(json)

        then:
        obj.relationships != null
        obj.relationships.isEmpty()
    }


    def 'a resource with an unwrapped relationship can be deserialized'() {
        given:
        def json = """{
            "type": "test_map",
            "id": "unwrapped",
            "relationships": {
                "sig_other": {
                    "data": { "type": "sweetie", "id": "theOne" }
                }
            }
        }"""

        when:
        MapTestResource obj = deserialize(json)

        then:
        'unwrapped' == obj.id

        and:
        [JsonApiResourceId.of('sweetie', 'theOne')] == obj.relationships.get('sig_other').get().data
    }


    def 'a resource with a multi-valued relationship reports the entries in the same order'() {
        given:
        def json = """{
            "type": "test_map",
            "id": "multimulti",
            "relationships": {
                "digits": {
                    "data": [
                        { "type": "test_map", "id": "one" },
                        { "type": "test_map", "id": "zero" }
                    ]
                },
                "words": {
                    "data": [
                        { "type": "test_map", "id": "0" },
                        { "type": "test_map", "id": "1" }
                    ]
                }
            }
        }"""

        when:
        MapTestResource obj = deserialize(json)

        then:
        'multimulti' == obj.id

        and:
        ['digits', 'words'] == obj.relationships.keys() as List
        [mtrid('one'), mtrid('zero')] == obj.relationships.get('digits').get().data
        [mtrid('0'), mtrid('1')] == obj.relationships.get('words').get().data
    }
}
