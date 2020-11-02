package org.pac4j.lagom.javadsl;

import com.lightbend.lagom.javadsl.api.transport.Forbidden;
import com.lightbend.lagom.javadsl.server.ServerServiceCall;
import org.pac4j.core.authorization.authorizer.Authorizer;
import org.pac4j.core.client.Client;
import org.pac4j.core.client.Clients;
import org.pac4j.core.config.Config;
import org.pac4j.core.profile.AnonymousProfile;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.lagom.javadsl.transport.Unauthorized;

import java.util.function.Function;

import static com.lightbend.lagom.javadsl.server.HeaderServiceCall.compose;
import static java.util.Collections.singletonList;
import static java.util.Optional.ofNullable;

/**
 * <p>
 *     Interface, that implement cross-cutting security concerns for Lagom services.
 * </p>
 * <p>
 *     More information about service call composition in Lagom <a href="https://www.lagomframework.com/documentation/current/java/ServiceImplementation.html#Service-call-composition">documentation</a>.
 * </p>
 *
 * @author Sergey Morgunov
 * @since 1.0.0
 */
public interface SecuredService {

    /**
     * Get configuration of pac4j for this service.
     *
     * @return pac4j configuration
     */
    Config getSecurityConfig();

    /**
     * Service call composition for authentication.
     *
     * @param serviceCall Service call
     * @param <Request> Type of request
     * @param <Response> Type of response
     * @return Service call with authentication logic
     */
    default <Request, Response> ServerServiceCall<Request, Response> authenticate(
            Function<CommonProfile, ServerServiceCall<Request, Response>> serviceCall) {
        return authenticate(getSecurityConfig().getClients().getDefaultSecurityClients(), serviceCall);
    }

    /**
     * Service call composition for authentication.
     *
     * @param clientName Name of authentication client
     * @param serviceCall Service call
     * @param <Request> Type of request
     * @param <Response> Type of response
     * @return Service call with authentication logic
     */
    @SuppressWarnings("unchecked")
    default <Request, Response> ServerServiceCall<Request, Response> authenticate(
            String clientName,
            Function<CommonProfile, ServerServiceCall<Request, Response>> serviceCall) {
        return compose(requestHeader -> {
            CommonProfile profile;
            try {
                Clients clients = getSecurityConfig().getClients();
                Client defaultClient = clients.findClient(clientName);
                LagomWebContext context = new LagomWebContext(requestHeader);
                profile = defaultClient.getUserProfile(defaultClient.getCredentials(context), context);
            } catch (Exception ex) {
                // We can throw only TransportException.
                // Otherwise exception will be sent to the client with stack trace.
                profile = new AnonymousProfile();
            }
            return serviceCall.apply(ofNullable(profile).orElse(new AnonymousProfile()));
        });
    }

    /**
     * Service call composition for authorization.
     *
     * @param authorizer Authorizer (may be composite)
     * @param serviceCall Service call
     * @param <Request> Type of request
     * @param <Response> Type of response
     * @return Service call with authorization logic
     */
    default <Request, Response> ServerServiceCall<Request, Response> authorize(
            Authorizer<CommonProfile> authorizer,
            Function<CommonProfile, ServerServiceCall<Request, Response>> serviceCall) {
        return authorize(getSecurityConfig().getClients().getDefaultSecurityClients(), authorizer, serviceCall);
    }

    /**
     * Service call composition for authorization.
     *
     * @param clientName Name of authentication client
     * @param authorizer Authorizer (may be composite)
     * @param serviceCall Service call
     * @param <Request> Type of request
     * @param <Response> Type of response
     * @return Service call with authorization logic
     */
    default <Request, Response> ServerServiceCall<Request, Response> authorize(
            String clientName,
            Authorizer<CommonProfile> authorizer,
            Function<CommonProfile, ServerServiceCall<Request, Response>> serviceCall) {
        return authenticate(clientName, profile -> compose(requestHeader -> {
            boolean authorized;
            try {
                authorized = authorizer != null && authorizer.isAuthorized(new LagomWebContext(requestHeader), singletonList(profile));
            } catch (Exception ex) {
                // We can throw only TransportException.
                // Otherwise exception will be sent to the client with stack trace.
                authorized = false;
            }
            if (!authorized) {
                if (profile == null || profile instanceof AnonymousProfile) throw new Unauthorized("Unauthorized");
                else throw new Forbidden("Authorization failed");
            }
            return serviceCall.apply(profile);
        }));
    }

    /**
     * Service call composition for authorization.
     *
     * @param authorizerName Name of authorizer, registered in security config
     * @param serviceCall Service call
     * @param <Request> Type of request
     * @param <Response> Type of response
     * @return Service call with authorization logic
     */
    @SuppressWarnings("unchecked")
    default <Request, Response> ServerServiceCall<Request, Response> authorize(
            String authorizerName,
            Function<CommonProfile, ServerServiceCall<Request, Response>> serviceCall) {
        return authorize(getSecurityConfig().getAuthorizers().get(authorizerName), serviceCall);
    }

    /**
     * Service call composition for authorization.
     *
     * @param clientName Name of authentication client
     * @param authorizerName Name of authorizer, registered in security config
     * @param serviceCall Service call
     * @param <Request> Type of request
     * @param <Response> Type of response
     * @return Service call with authorization logic
     */
    @SuppressWarnings("unchecked")
    default <Request, Response> ServerServiceCall<Request, Response> authorize(
            String clientName,
            String authorizerName,
            Function<CommonProfile, ServerServiceCall<Request, Response>> serviceCall) {
        return authorize(clientName, getSecurityConfig().getAuthorizers().get(authorizerName), serviceCall);
    }

}
