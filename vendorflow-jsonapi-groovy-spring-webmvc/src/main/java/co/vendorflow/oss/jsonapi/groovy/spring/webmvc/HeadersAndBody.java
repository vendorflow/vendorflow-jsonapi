package co.vendorflow.oss.jsonapi.groovy.spring.webmvc;

import java.net.URI;
import java.util.function.Consumer;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class HeadersAndBody<B> {
    final HttpHeaders headers;
    final B body;

    public static <B> HeadersAndBody<B> of(B body) {
        return new HeadersAndBody<>(new HttpHeaders(), body);
    }


    public ResponseEntity<B> toResponseEntity(HttpStatus status) {
        return ResponseEntity.status(status)
                .headers(headers)
                .body(body);
    }


    public ResponseEntity<B> toResponseEntity() {
        return toResponseEntity(HttpStatus.OK);
    }


    public HeadersAndBody<B> headers(Consumer<? super HttpHeaders> consumer) {
        consumer.accept(headers);
        return this;
    }


    public HeadersAndBody<B> location(URI location) {
        headers.setLocation(location);
        return this;
    }
}
