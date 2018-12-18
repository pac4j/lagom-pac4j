package org.pac4j.lagom.javadsl;

import akka.NotUsed;
import com.lightbend.lagom.javadsl.api.Descriptor;
import com.lightbend.lagom.javadsl.api.Service;
import com.lightbend.lagom.javadsl.api.ServiceCall;

import static com.lightbend.lagom.javadsl.api.Service.named;
import static com.lightbend.lagom.javadsl.api.Service.pathCall;

/**
 * Descriptor of Lagom service for tests.
 *
 * @author Sergey Morgunov
 * @since 1.0.0
 */
public interface TestService extends Service {

    ServiceCall<NotUsed, String> defaultAuthenticate();
    ServiceCall<NotUsed, String> defaultAuthorize();
    ServiceCall<NotUsed, String> defaultAuthorizeConfig();

    ServiceCall<NotUsed, String> cookieAuthenticate();
    ServiceCall<NotUsed, String> cookieAuthorize();
    ServiceCall<NotUsed, String> cookieAuthorizeConfig();

    ServiceCall<NotUsed, String> headerAuthenticate();
    ServiceCall<NotUsed, String> headerAuthorize();
    ServiceCall<NotUsed, String> headerAuthorizeConfig();

    ServiceCall<NotUsed, String> headerJwtAuthenticate();

    @Override
    default Descriptor descriptor() {
        return named("default").withCalls(
                pathCall("/default/authenticate", this::defaultAuthenticate),
                pathCall("/default/authorize", this::defaultAuthorize),
                pathCall("/default/authorize/config", this::defaultAuthorizeConfig),
                pathCall("/cookie/authenticate", this::cookieAuthenticate),
                pathCall("/cookie/authorize", this::cookieAuthorize),
                pathCall("/cookie/authorize/config", this::cookieAuthorizeConfig),
                pathCall("/header/authenticate", this::headerAuthenticate),
                pathCall("/header/authorize", this::headerAuthorize),
                pathCall("/header/authorize/config", this::headerAuthorizeConfig),
                pathCall("/header/jwt/authenticate", this::headerJwtAuthenticate)
        ).withAutoAcl(true);
    }
}
