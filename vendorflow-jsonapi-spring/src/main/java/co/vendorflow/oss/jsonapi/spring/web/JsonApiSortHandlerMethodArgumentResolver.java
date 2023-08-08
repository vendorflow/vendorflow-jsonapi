package co.vendorflow.oss.jsonapi.spring.web;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import static java.util.stream.StreamSupport.stream;
import static org.springframework.data.domain.Sort.Direction.DESC;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.springframework.core.MethodParameter;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.web.HateoasSortHandlerMethodArgumentResolver;
import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.method.support.UriComponentsContributor;
import org.springframework.web.util.UriComponentsBuilder;

import co.vendorflow.oss.jsonapi.model.error.IsJsonApiError;
import co.vendorflow.oss.jsonapi.model.error.JsonApiError;
import co.vendorflow.oss.jsonapi.model.error.JsonApiErrors;

public class JsonApiSortHandlerMethodArgumentResolver extends HateoasSortHandlerMethodArgumentResolver implements UriComponentsContributor {
    public static final String JSON_API_SORT_PARAMETER = "sort";
    public static final String JSON_API_SORT_DELIMITER = ",";
    public static final String JSON_API_SORT_DESCENDING = "-";

    public JsonApiSortHandlerMethodArgumentResolver() {
        // redundant, but to ensure no future changes in defaults could break this
        super.setSortParameter(JSON_API_SORT_PARAMETER);
        super.setPropertyDelimiter(JSON_API_SORT_DELIMITER);
    }

    @Override
    public final void setSortParameter(String sortParameter) {
        if (!JSON_API_SORT_PARAMETER.equals(sortParameter)) {
            throw new IllegalArgumentException("the JSON:API sort parameter must be named " + JSON_API_SORT_PARAMETER + " but was " + sortParameter);
        }
    }

    @Override
    public final void setPropertyDelimiter(String propertyDelimiter) {
        if (!JSON_API_SORT_DELIMITER.equals(propertyDelimiter)) {
            throw new IllegalArgumentException("the JSON:API sort delimiter must be '" + JSON_API_SORT_DELIMITER + "' but was '" + propertyDelimiter + "'");
        }
    }


    @Override
    public Sort resolveArgument(MethodParameter parameter, @Nullable ModelAndViewContainer mavContainer,
            NativeWebRequest webRequest, @Nullable WebDataBinderFactory binderFactory) {

        String[] directionParameter = webRequest.getParameterValues(getSortParameter(parameter));

        // No parameter
        if (directionParameter == null) {
            return getDefaultFromAnnotationOrFallback(parameter);
        }

        // Single empty parameter, e.g "sort="
        if (directionParameter.length == 1 && !StringUtils.hasText(directionParameter[0])) {
            return getDefaultFromAnnotationOrFallback(parameter);
        }

        if (directionParameter.length > 1) {
            throw new TooManySortParametersException();
        }

        return parseParameterIntoSort(directionParameter[0]);
    }


    static Sort parseParameterIntoSort(String parameter) {
        return Arrays.stream(parameter.split(JSON_API_SORT_DELIMITER))
            .map(JsonApiSortHandlerMethodArgumentResolver::propToOrder)
            .collect(collectingAndThen(toList(), Sort::by));
    }


    static Order propToOrder(String prop) {
        return prop.startsWith(JSON_API_SORT_DESCENDING)
            ? Order.desc(prop.substring(1))
            : Order.asc(prop);
    }


    static String sortToJsonApiParameter(Sort sort) {
        return stream(sort.spliterator(), false)
                .map(order -> (order.getDirection() == DESC ? JSON_API_SORT_DESCENDING : "") + order.getProperty())
                .collect(joining(JSON_API_SORT_DELIMITER));
    }


    @Override
    public void contributeMethodArgument(MethodParameter parameter, Object value, UriComponentsBuilder builder,
            Map<String, Object> uriVariables, ConversionService conversionService) {
        if (!(value instanceof Sort)) {
            return;
        }

        Sort sort = (Sort) value;
        builder.replaceQueryParam(JSON_API_SORT_PARAMETER, sortToJsonApiParameter(sort));
    }


    @Override
    protected List<String> foldIntoExpressions(Sort sort) {
        return List.of(sortToJsonApiParameter(sort));
    }


    @ResponseStatus(HttpStatus.BAD_REQUEST)
    static class TooManySortParametersException extends IllegalArgumentException implements IsJsonApiError {
        static final String TOO_MANY_SORT_PARAMETERS_CODE = JsonApiErrors.BAD_QUERY_CODE_PREFIX + "TOO_MANY_SORT_PARAMETERS";

        @Override
        public JsonApiError asJsonApiError() {
            return JsonApiErrors.badQuery(TOO_MANY_SORT_PARAMETERS_CODE);
        }
    }


}
