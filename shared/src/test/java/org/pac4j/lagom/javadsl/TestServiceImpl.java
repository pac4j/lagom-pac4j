package org.pac4j.lagom.javadsl;

import akka.NotUsed;
import com.lightbend.lagom.javadsl.api.ServiceCall;
import org.pac4j.core.config.Config;

import javax.inject.Inject;

import static org.pac4j.core.authorization.authorizer.IsAuthenticatedAuthorizer.isAuthenticated;
import static org.pac4j.core.authorization.authorizer.RequireAnyRoleAuthorizer.requireAnyRole;
import static org.pac4j.lagom.javadsl.ClientNames.COOKIE_CLIENT;
import static org.pac4j.lagom.javadsl.ClientNames.HEADER_CLIENT;
import static org.pac4j.lagom.javadsl.ClientNames.HEADER_JWT_CLIENT;

import static java.util.concurrent.CompletableFuture.completedFuture;

/**
 * Implementation of Lagom service for tests.
 *
 * @author Sergey Morgunov
 * @since 1.0.0
 */
public class TestServiceImpl implements TestService, SecuredService {

    private final Config securityConfig;

    @Inject
    public TestServiceImpl(Config securityConfig) {
        this.securityConfig = securityConfig;
    }

    @Override
    public ServiceCall<NotUsed, String> defaultAuthenticate() {
        return authenticate(profile ->
                request -> completedFuture(profile.getId())
        );
    }

    @Override
    public ServiceCall<NotUsed, String> defaultAuthorize() {
        return authorize(isAuthenticated(), profile ->
                request -> completedFuture(profile.getId())
        );
    }

    @Override
    public ServiceCall<NotUsed, String> defaultAuthorizeByRole() {
        return authorize(requireAnyRole("role"), profile ->
            request -> completedFuture(profile.getId())
        );
    }

    @Override
    public ServiceCall<NotUsed, String> defaultAuthorizeConfig() {
        String authorizerName = "_authenticated_";
        return authorize(authorizerName, profile ->
                request -> completedFuture(profile.getId())
        );
    }

    @Override
    public ServiceCall<NotUsed, String> cookieAuthenticate() {
        return authenticate(COOKIE_CLIENT, profile ->
                request -> completedFuture(profile.getId())
        );
    }

    @Override
    public ServiceCall<NotUsed, String> cookieAuthorize() {
        return authorize(COOKIE_CLIENT, isAuthenticated(), profile ->
                request -> completedFuture(profile.getId())
        );
    }

    @Override
    public ServiceCall<NotUsed, String> cookieAuthorizeConfig() {
        String authorizerName = "_authenticated_";
        return authorize(COOKIE_CLIENT, authorizerName, profile ->
                request -> completedFuture(profile.getId())
        );
    }

    @Override
    public ServiceCall<NotUsed, String> headerAuthenticate() {
        return authenticate(HEADER_CLIENT, profile ->
                request -> completedFuture(profile.getId())
        );
    }

    @Override
    public ServiceCall<NotUsed, String> headerAuthorize() {
        return authorize(HEADER_CLIENT, isAuthenticated(), profile ->
                request -> completedFuture(profile.getId())
        );
    }

    @Override
    public ServiceCall<NotUsed, String> headerAuthorizeConfig() {
        String authorizerName = "_authenticated_";
        return authorize(HEADER_CLIENT, authorizerName, profile ->
                request -> completedFuture(profile.getId())
        );
    }

    @Override
    public ServiceCall<NotUsed, String> headerJwtAuthenticate() {
        return authenticate(HEADER_JWT_CLIENT, profile ->
                request -> completedFuture(profile.getId())
        );
    }

    @Override
    public Config getSecurityConfig() {
        return securityConfig;
    }
}
