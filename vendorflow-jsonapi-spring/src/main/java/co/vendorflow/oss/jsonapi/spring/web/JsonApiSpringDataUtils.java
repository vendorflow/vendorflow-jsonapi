package co.vendorflow.oss.jsonapi.spring.web;

import static java.util.function.Predicate.not;
import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import static java.util.stream.StreamSupport.stream;
import static org.springframework.data.domain.Sort.Direction.DESC;

import java.util.Arrays;

import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;

public class JsonApiSpringDataUtils {
    public static final String JSON_API_SORT_PARAMETER = "sort";
    public static final String JSON_API_SORT_DELIMITER = ",";
    public static final String JSON_API_SORT_DESCENDING = "-";

    public static String parameterValue(Sort sort) {
        return stream(sort.spliterator(), false)
                .map(order -> (order.getDirection() == DESC ? JSON_API_SORT_DESCENDING : "") + order.getProperty())
                .collect(joining(JSON_API_SORT_DELIMITER));
    }


    public static Order parseParameterPart(String part) {
        return part.startsWith(JSON_API_SORT_DESCENDING)
                ? Order.desc(part.substring(1))
                : Order.asc(part);
    }


    public static Sort parseSortParameter(String parameter) {
        if (parameter == null || parameter.isEmpty()) {
            return Sort.unsorted();
        }

        return Arrays.stream(parameter.split(JSON_API_SORT_DELIMITER))
                .filter(not(String::isEmpty))
                .map(JsonApiSpringDataUtils::parseParameterPart)
                .collect(collectingAndThen(toList(), Sort::by));
    }
}
