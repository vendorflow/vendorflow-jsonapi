package co.vendorflow.oss.jsonapi.spring.web;

import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.web.config.HateoasAwareSpringDataWebConfiguration;

/**
 * Note that this class is not auto-configured to prevent accidents. Auto-configuration might be
 * added in the future.
 *
 * @author Christopher Smith
 */
@Configuration(proxyBeanMethods = false)
public class JsonApiSpringDataWebConfiguration extends HateoasAwareSpringDataWebConfiguration {

    public JsonApiSpringDataWebConfiguration(ApplicationContext context,
            @Qualifier("mvcConversionService") ObjectFactory<ConversionService> conversionService) {
        super(context, conversionService);
    }


    @Bean
    @Override
    public JsonApiSortHandlerMethodArgumentResolver sortResolver() {
        var sortResolver = new JsonApiSortHandlerMethodArgumentResolver();
        customizeSortResolver(sortResolver);
        return sortResolver;
    }
}
