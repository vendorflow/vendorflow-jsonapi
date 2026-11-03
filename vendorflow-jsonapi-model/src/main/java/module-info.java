module co.vendorflow.oss.jsonapi.model {
    requires static jakarta.validation;
    requires static lombok;
    requires static org.apache.groovy;

    exports co.vendorflow.oss.jsonapi.model;
    exports co.vendorflow.oss.jsonapi.model.error;
    exports co.vendorflow.oss.jsonapi.model.links;
    exports co.vendorflow.oss.jsonapi.model.request;
    exports co.vendorflow.oss.jsonapi.model.resource;
    exports co.vendorflow.oss.jsonapi.model.resource.id;

    opens co.vendorflow.oss.jsonapi.model.error to com.fasterxml.jackson.databind, tools.jackson.databind, org.hibernate.validator;
    opens co.vendorflow.oss.jsonapi.model.links to com.fasterxml.jackson.databind, tools.jackson.databind, org.hibernate.validator;
    opens co.vendorflow.oss.jsonapi.model.request to com.fasterxml.jackson.databind, tools.jackson.databind, org.hibernate.validator;
    opens co.vendorflow.oss.jsonapi.model.resource to com.fasterxml.jackson.databind, tools.jackson.databind, org.hibernate.validator;
    opens co.vendorflow.oss.jsonapi.model.resource.id to com.fasterxml.jackson.databind, tools.jackson.databind, org.hibernate.validator;
}
