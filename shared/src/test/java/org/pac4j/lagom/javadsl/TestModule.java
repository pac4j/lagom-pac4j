package org.pac4j.lagom.javadsl;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.lightbend.lagom.javadsl.server.ServiceGuiceSupport;
import org.pac4j.core.config.Config;
import org.pac4j.core.credentials.TokenCredentials;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.http.client.direct.HeaderClient;

import javax.inject.Named;

import static org.pac4j.core.authorization.authorizer.IsAnonymousAuthorizer.isAnonymous;
import static org.pac4j.core.authorization.authorizer.IsAuthenticatedAuthorizer.isAuthenticated;
import static org.pac4j.core.context.HttpConstants.AUTHORIZATION_HEADER;
import static org.pac4j.lagom.javadsl.ClientNames.HEADER_CLIENT;

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
    @Named(HEADER_CLIENT)
    protected HeaderClient provideHttpClient() {
        HeaderClient headerClient = new HeaderClient(AUTHORIZATION_HEADER, (credentials, webContext) -> {
            final CommonProfile profile = new CommonProfile();
            profile.setId(((TokenCredentials)credentials).getToken());
            credentials.setUserProfile(profile);
        });
        headerClient.setName(HEADER_CLIENT);
        return headerClient;
    }

    @Provides
    protected Config provideConfig(@Named(HEADER_CLIENT) HeaderClient headerClient) {
        final Config config = new Config(headerClient);
        config.getClients().setDefaultSecurityClients(headerClient.getName());
        config.addAuthorizer("_anonymous_", isAnonymous());
        config.addAuthorizer("_authenticated_", isAuthenticated());
        return config;
    }

}
