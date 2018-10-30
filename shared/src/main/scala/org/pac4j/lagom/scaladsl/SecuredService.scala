package org.pac4j.lagom.scaladsl

import java.util.Collections.singletonList

import com.lightbend.lagom.scaladsl.api.transport.Forbidden
import com.lightbend.lagom.scaladsl.server.ServerServiceCall
import org.pac4j.core.authorization.authorizer.Authorizer
import org.pac4j.core.client.Client
import org.pac4j.core.config.Config
import org.pac4j.core.credentials.Credentials
import org.pac4j.core.profile.{AnonymousProfile, CommonProfile}

/**
  * <p>
  *   Interface, that implement cross-cutting security concerns for Lagom services.
  * </p>
  * <p>
  *   More information about service call composition in Lagom <a href="https://www.lagomframework.com/documentation/current/scala/ServiceImplementation.html#Service-call-composition">documentation</a>.
  * </p>
  *
  * @author Vladimir Kornyshev
  * @since 1.0.0
  */
trait SecuredService {

  /**
    * Get configuration of pac4j for this service.
    *
    * @return pac4j configuration
    */
  def securityConfig: Config

  /**
    * Service call composition for authentication.
    *
    * @param serviceCall Service call
    * @tparam Request Type of request
    * @tparam Response Type of response
    * @return Service call with authentication logic
    */
  def authenticate[Request, Response](
        serviceCall: CommonProfile => ServerServiceCall[Request, Response]): ServerServiceCall[Request, Response] =
    authenticate(securityConfig.getClients.getDefaultSecurityClients, serviceCall)

  /**
    * Service call composition for authentication.
    *
    * @param clientName Name of authentication client
    * @param serviceCall Service call
    * @tparam Request Type of request
    * @tparam Response Type of response
    * @return Service call with authentication logic
    */
  def authenticate[Request, Response](
        clientName: String, serviceCall: CommonProfile => ServerServiceCall[Request, Response]): ServerServiceCall[Request, Response] =
    ServerServiceCall.compose { requestHeader =>
      val profile = try {
        val clients = securityConfig.getClients
        val defaultClient = clients.findClient(clientName).asInstanceOf[Client[Credentials, CommonProfile]]
        val context = new LagomWebContext(requestHeader)
        val credentials = defaultClient.getCredentials(context)
        defaultClient.getUserProfile(credentials, context)
      } catch {
        case ex: Exception =>
          // We can throw only TransportException.
          // Otherwise exception will be sent to the client with stack trace.
          new AnonymousProfile
      }

      serviceCall.apply(Option(profile).getOrElse(new AnonymousProfile))
    }

  /**
    * Service call composition for authorization.
    *
    * @param authorizer Authorizer (may be composite)
    * @param serviceCall Service call
    * @tparam Request Type of request
    * @tparam Response Type of response
    * @return Service call with authorization logic
    */
  def authorize[Request, Response](
        authorizer: Authorizer[CommonProfile], serviceCall: CommonProfile => ServerServiceCall[Request, Response]): ServerServiceCall[Request, Response] =
    authorize(securityConfig.getClients.getDefaultSecurityClients, authorizer, serviceCall)

  /**
    * Service call composition for authorization.
    *
    * @param clientName Name of authentication client
    * @param authorizer Authorizer (may be composite)
    * @param serviceCall Service call
    * @tparam Request Type of request
    * @tparam Response Type of response
    * @return Service call with authorization logic
    */
  def authorize[Request, Response](
        clientName: String, authorizer: Authorizer[CommonProfile], serviceCall: CommonProfile => ServerServiceCall[Request, Response]): ServerServiceCall[Request, Response] =
    authenticate(clientName, (profile: CommonProfile) => ServerServiceCall.compose { requestHeader =>
      val authorized = try {
        authorizer != null && authorizer.isAuthorized(new LagomWebContext(requestHeader), singletonList(profile))
      } catch {
        case ex: Exception =>
          // We can throw only TransportException.
          // Otherwise exception will be sent to the client with stack trace.
          false
      }
      if (!authorized) throw Forbidden("Authorization failed")
      serviceCall.apply(profile)
    })

  /**
    * Service call composition for authorization.
    *
    * @param authorizerName Name of authorizer, registered in security config
    * @param serviceCall    Service call
    * @tparam Request Type of request
    * @tparam Response Type of response
    * @return Service call with authorization logic
    */
  def authorize[Request, Response](
        authorizerName: String, serviceCall: CommonProfile => ServerServiceCall[Request, Response]): ServerServiceCall[Request, Response] =
    authorize(securityConfig.getAuthorizers.get(authorizerName).asInstanceOf[Authorizer[CommonProfile]], serviceCall)

  /**
    * Service call composition for authorization.
    *
    * @param clientName     Name of authentication client
    * @param authorizerName Name of authorizer, registered in security config
    * @param serviceCall    Service call
    * @tparam Request Type of request
    * @tparam Response Type of response
    * @return Service call with authorization logic
    */
  def authorize[Request, Response](
        clientName: String, authorizerName: String, serviceCall: CommonProfile => ServerServiceCall[Request, Response]): ServerServiceCall[Request, Response] =
    authorize(clientName, securityConfig.getAuthorizers.get(authorizerName).asInstanceOf[Authorizer[CommonProfile]], serviceCall)
}
