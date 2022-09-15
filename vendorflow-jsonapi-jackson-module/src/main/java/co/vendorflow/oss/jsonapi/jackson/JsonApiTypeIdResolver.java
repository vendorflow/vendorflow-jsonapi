package co.vendorflow.oss.jsonapi.jackson;

import static java.util.stream.Collectors.toMap;

import java.util.Map;
import java.util.Optional;
import java.util.ServiceLoader;
import java.util.ServiceLoader.Provider;
import java.util.stream.Stream;

import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import com.fasterxml.jackson.databind.DatabindContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.jsontype.impl.TypeIdResolverBase;

import co.vendorflow.oss.jsonapi.model.resource.JsonApiResource;
import co.vendorflow.oss.jsonapi.model.resource.JsonApiType;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JsonApiTypeIdResolver extends TypeIdResolverBase {
    private static final Map<String, Class<? extends JsonApiResource<?, ?>>> registrations;

    public static Stream<JsonApiTypeRegistration> findRegistrations() {
        return ServiceLoader.load(JsonApiTypeRegistration.class).stream()
            .map(Provider::get);
    }

    static {
        registrations = findRegistrations()
                .peek(tr -> log.debug("registering JSON:API type {} as {}", tr.typeName(), tr.typeClass()))
                .collect(toMap(JsonApiTypeRegistration::typeName, JsonApiTypeRegistration::typeClass));

        log.info("registered JSON:API types {}", registrations.keySet());
    }

    @Override
    public String idFromValue(Object value) {
        return idFromValueAndType(value, value.getClass());
    }


    @Override
    public String idFromValueAndType(Object value, Class<?> suggestedType) {
        return Optional.ofNullable(value)
                .<Class<?>>map(Object::getClass)
                .or(() -> Optional.of(suggestedType))
                .map(clazz -> clazz.getAnnotation(JsonApiType.class))
                .map(JsonApiType::value)
                .orElseThrow(() -> new IllegalArgumentException("only JsonApiType instances are supported"));
    }


    @Override
    public JavaType typeFromId(DatabindContext context, String id) {
        return context.constructType(registrations.get(id));
    }


    @Override
    public Id getMechanism() {
        return Id.NAME;
    }


    @Override
    public String getDescForKnownTypeIds() {
        return registrations.keySet().toString();
    }
}
