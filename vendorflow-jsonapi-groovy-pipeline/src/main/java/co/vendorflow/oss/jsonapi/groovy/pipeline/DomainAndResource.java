package co.vendorflow.oss.jsonapi.groovy.pipeline;

import java.util.function.Function;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public final class DomainAndResource<D, R> {
    public final D domain;
    public final R resource;

    public <R2> DomainAndResource<D, R2> map(Function<? super R, ? extends R2> transform) {
        return new DomainAndResource<>(domain, transform.apply(resource));
    }

    public D domain() {
        return domain;
    }

    public R resource() {
        return resource;
    }

    public static <D, R> DomainAndResource<D, R> start(D domain, Function<? super D, ? extends R> toResource) {
        return new DomainAndResource<>(domain, toResource.apply(domain));
    }
}
