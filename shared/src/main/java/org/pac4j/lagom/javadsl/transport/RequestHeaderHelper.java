package org.pac4j.lagom.javadsl.transport;

import com.lightbend.lagom.javadsl.api.transport.RequestHeader;
import com.nimbusds.jwt.JWT;

import java.util.function.Function;

import static org.pac4j.core.context.HttpConstants.AUTHORIZATION_HEADER;
import static org.pac4j.core.context.HttpConstants.BEARER_HEADER_PREFIX;

/**
 * Helper functions for {@link RequestHeader}.
 *
 * @author Sergey Morgunov
 * @since 2.2.1
 */
public final class RequestHeaderHelper {

    /**
     * Puts {@code Authorization} header to {@link RequestHeader}.
     * @param jwt JWT
     */
    public static Function<RequestHeader, RequestHeader> authorizationBearer(String jwt) {
        return header -> header.withHeader(AUTHORIZATION_HEADER, BEARER_HEADER_PREFIX.concat(jwt));
    }

    /**
     * Puts {@code Authorization} header to {@link RequestHeader}.
     * @param jwt JWT
     */
    public static Function<RequestHeader, RequestHeader> authorizationBearer(JWT jwt) {
        return authorizationBearer(jwt.serialize());
    }

    /**
     * Copies specified header from sourced {@link RequestHeader} to target.
     * @param source sourced {@link RequestHeader}
     * @param name name of copying header
     */
    public static Function<RequestHeader, RequestHeader> forwardHeader(RequestHeader source, String name) {
        return header -> source.getHeader(name).map(value -> header.withHeader(name, value)).orElse(header);
    }

    /**
     * Copies {@code Authorization} header from sourced {@link RequestHeader} to target.
     * @param source sourced {@link RequestHeader}
     */
    public static Function<RequestHeader, RequestHeader> forwardAuthorization(RequestHeader source) {
        return forwardHeader(source, AUTHORIZATION_HEADER);
    }

    private RequestHeaderHelper() {
    }
}
