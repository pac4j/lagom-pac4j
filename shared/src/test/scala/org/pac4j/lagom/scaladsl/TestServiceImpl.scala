package org.pac4j.lagom.scaladsl
import akka.NotUsed
import com.lightbend.lagom.scaladsl.api.ServiceCall
import com.lightbend.lagom.scaladsl.server.ServerServiceCall
import org.pac4j.core.authorization.authorizer.Authorizer
import org.pac4j.core.config.Config
import org.pac4j.core.profile.CommonProfile
import org.pac4j.lagom.scaladsl.ClientNames.HEADER_CLIENT

import scala.concurrent.Future

import org.pac4j.core.authorization.authorizer.IsAuthenticatedAuthorizer.isAuthenticated

/**
  * Implementation of Lagom service for tests.
  *
  * @author Vladimir Kornyshev
  * @since 1.0.0
  */
class TestServiceImpl(override val securityConfig: Config) extends TestService with SecuredService {

  override def defaultAuthenticate: ServiceCall[NotUsed, String] = {
    authenticate((profile: CommonProfile) => ServerServiceCall { request: NotUsed => Future.successful(profile.getId) })
  }

  override def defaultAuthorize: ServiceCall[NotUsed, String] = {
    val authorizer: Authorizer[CommonProfile] = isAuthenticated()
    authorize(authorizer, (profile: CommonProfile) => ServerServiceCall { request: NotUsed => Future.successful(profile.getId) })
  }

  override def defaultAuthorizeConfig: ServiceCall[NotUsed, String] = {
    val authorizerName = "_authenticated_"
    authorize(authorizerName, (profile: CommonProfile) => ServerServiceCall { request: NotUsed => Future.successful(profile.getId) })
  }

  override def headerAuthenticate: ServiceCall[NotUsed, String] = {
    authenticate(HEADER_CLIENT, (profile: CommonProfile) => ServerServiceCall { request: NotUsed => Future.successful(profile.getId) })
  }

  override def headerAuthorize: ServiceCall[NotUsed, String] = {
    val authorizer: Authorizer[CommonProfile] = isAuthenticated()
    authorize(HEADER_CLIENT, authorizer, (profile: CommonProfile) => ServerServiceCall { request: NotUsed => Future.successful(profile.getId) })
  }

  override def headerAuthorizeConfig: ServiceCall[NotUsed, String] = {
    val authorizerName = "_authenticated_"
    authorize(HEADER_CLIENT, authorizerName, (profile: CommonProfile) => ServerServiceCall { request: NotUsed => Future.successful(profile.getId) })
  }

}
