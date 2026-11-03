module co.vendorflow.oss.jsonapi.spring {
    requires static spring.beans;
    requires static spring.context;
    requires static spring.core;
    requires static spring.data.commons;
    requires static spring.hateoas;
    requires static spring.web;
    requires static spring.webmvc;

    requires co.vendorflow.oss.jsonapi.model;

    exports co.vendorflow.oss.jsonapi.spring.web;

    opens co.vendorflow.oss.jsonapi.spring.web to spring.beans, spring.context, spring.core;
}
