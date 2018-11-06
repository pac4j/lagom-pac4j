package org.pac4j.lagom.scaladsl
import akka.NotUsed
import com.lightbend.lagom.scaladsl.api.ServiceCall
import com.lightbend.lagom.scaladsl.server.ServerServiceCall
import org.pac4j.core.authorization.authorizer.IsAuthenticatedAuthorizer.isAuthenticated
import org.pac4j.core.config.Config
import org.pac4j.core.profile.CommonProfile
import org.pac4j.lagom.scaladsl.ClientNames.{HEADER_CLIENT, HEADER_JWT_CLIENT}

import scala.concurrent.Future

/**
  * Implementation of Lagom service for tests.
  *
  * @author Vladimir Kornyshev
  * @since 1.0.0
  */
class TestServiceImpl(override val securityConfig: Config) extends TestService with SecuredService {

  override def defaultAuthenticate: ServiceCall[NotUsed, String] = {
    authenticate((profile: CommonProfile) => ServerServiceCall { _: NotUsed => Future.successful(profile.getId) })
  }

  override def defaultAuthorize: ServiceCall[NotUsed, String] = {
    authorize(isAuthenticated[CommonProfile](), (profile: CommonProfile) => ServerServiceCall { _: NotUsed => Future.successful(profile.getId) })
  }

  override def defaultAuthorizeConfig: ServiceCall[NotUsed, String] = {
    val authorizerName = "_authenticated_"
    authorize(authorizerName, (profile: CommonProfile) => ServerServiceCall { _: NotUsed => Future.successful(profile.getId) })
  }

  override def headerAuthenticate: ServiceCall[NotUsed, String] = {
    authenticate(HEADER_CLIENT, (profile: CommonProfile) => ServerServiceCall { _: NotUsed => Future.successful(profile.getId) })
  }

  override def headerAuthorize: ServiceCall[NotUsed, String] = {
    authorize(HEADER_CLIENT, isAuthenticated[CommonProfile](), (profile: CommonProfile) => ServerServiceCall { _: NotUsed => Future.successful(profile.getId) })
  }

  override def headerAuthorizeConfig: ServiceCall[NotUsed, String] = {
    val authorizerName = "_authenticated_"
    authorize(HEADER_CLIENT, authorizerName, (profile: CommonProfile) => ServerServiceCall { _: NotUsed => Future.successful(profile.getId) })
  }

  override def headerJwtAuthenticate: ServiceCall[NotUsed, String] = {
    authenticate(HEADER_JWT_CLIENT, (profile: CommonProfile) => ServerServiceCall { _: NotUsed => Future.successful(profile.getId) })
  }

}
