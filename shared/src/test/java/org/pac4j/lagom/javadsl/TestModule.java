package org.pac4j.lagom.javadsl;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.lightbend.lagom.javadsl.server.ServiceGuiceSupport;
import com.nimbusds.jose.JOSEException;
import org.pac4j.core.config.Config;
import org.pac4j.core.credentials.TokenCredentials;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.http.client.direct.CookieClient;
import org.pac4j.http.client.direct.HeaderClient;
import org.pac4j.lagom.jwt.JwtAuthenticatorHelper;

import java.net.MalformedURLException;
import java.text.ParseException;
import javax.inject.Named;

import static org.pac4j.core.authorization.authorizer.IsAnonymousAuthorizer.isAnonymous;
import static org.pac4j.core.authorization.authorizer.IsAuthenticatedAuthorizer.isAuthenticated;
import static org.pac4j.core.context.HttpConstants.AUTHORIZATION_HEADER;
import static org.pac4j.core.context.HttpConstants.BEARER_HEADER_PREFIX;
import static org.pac4j.lagom.javadsl.ClientNames.COOKIE_CLIENT;
import static org.pac4j.lagom.javadsl.ClientNames.HEADER_CLIENT;
import static org.pac4j.lagom.javadsl.ClientNames.HEADER_JWT_CLIENT;

/**
 * DI module for run tests.
 *
 * @author Sergey Morgunov
 * @since 1.0.0
 */
public class TestModule extends AbstractModule implements ServiceGuiceSupport {

    @Override
    protected void configure() {
        bindService(TestService.class, TestServiceImpl.class);
    }

    @Provides
    @Named(COOKIE_CLIENT)
    protected CookieClient provideCookieClient() {
        CookieClient cookieClient = new CookieClient("auth", (credentials, webContext) -> {
            final CommonProfile profile = new CommonProfile();
            profile.setId(((TokenCredentials) credentials).getToken());
            credentials.setUserProfile(profile);
        });
        cookieClient.setName(COOKIE_CLIENT);
        return cookieClient;
    }

    @Provides
    @Named(HEADER_JWT_CLIENT)
    protected HeaderClient provideHeaderJwtClient(com.typesafe.config.Config configuration) throws ParseException, JOSEException, MalformedURLException {
        HeaderClient headerClient = new HeaderClient();
        headerClient.setHeaderName(AUTHORIZATION_HEADER);
        headerClient.setPrefixHeader(BEARER_HEADER_PREFIX);
        headerClient.setAuthenticator(JwtAuthenticatorHelper.parse(configuration.getConfig("pac4j.lagom.jwt.authenticator")));
        headerClient.setName(HEADER_JWT_CLIENT);
        return headerClient;
    }

    @Provides
    @Named(HEADER_CLIENT)
    protected HeaderClient provideHeaderClient() {
        HeaderClient headerClient = new HeaderClient(AUTHORIZATION_HEADER, (credentials, webContext) -> {
            final CommonProfile profile = new CommonProfile();
            profile.setId(((TokenCredentials)credentials).getToken());
            credentials.setUserProfile(profile);
        });
        headerClient.setName(HEADER_CLIENT);
        return headerClient;
    }

    @Provides
    protected Config provideConfig(@Named(HEADER_CLIENT) HeaderClient headerClient,
                                   @Named(HEADER_JWT_CLIENT) HeaderClient headerJwtClient,
                                   @Named(COOKIE_CLIENT) CookieClient cookieClient) {
        final Config config = new Config(headerClient, headerJwtClient, cookieClient);
        config.getClients().setDefaultSecurityClients(headerClient.getName());
        config.addAuthorizer("_anonymous_", isAnonymous());
        config.addAuthorizer("_authenticated_", isAuthenticated());
        return config;
    }

}
