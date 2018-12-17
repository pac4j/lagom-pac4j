package org.pac4j.lagom.scaladsl

import com.lightbend.lagom.scaladsl.api.LagomConfigComponent
import org.pac4j.core.authorization.authorizer.IsAnonymousAuthorizer.isAnonymous
import org.pac4j.core.authorization.authorizer.IsAuthenticatedAuthorizer.isAuthenticated
import org.pac4j.core.config.Config
import org.pac4j.core.context.HttpConstants.{AUTHORIZATION_HEADER, BEARER_HEADER_PREFIX}
import org.pac4j.core.context.WebContext
import org.pac4j.core.credentials.authenticator.Authenticator
import org.pac4j.core.credentials.{Credentials, TokenCredentials}
import org.pac4j.core.profile.CommonProfile
import org.pac4j.http.client.direct.{CookieClient, HeaderClient}
import org.pac4j.lagom.jwt.JwtAuthenticatorHelper
import org.pac4j.lagom.scaladsl.ClientNames.{COOKIE_CLIENT, HEADER_CLIENT, HEADER_JWT_CLIENT}

/**
  * DI module for run tests.
  *
  * @author Vladimir Kornyshev
  * @since 1.0.0
  */
trait TestModule extends LagomConfigComponent {

  lazy val cookieClient: CookieClient = {
    val cookieClient = new CookieClient("auth", new Authenticator[Credentials]() {
      override def validate(credentials: Credentials, webContext: WebContext): Unit = {
        val profile = new CommonProfile()
        profile.setId(credentials.asInstanceOf[TokenCredentials].getToken)
        credentials.setUserProfile(profile)
      }
    })
    cookieClient.setName(COOKIE_CLIENT)
    cookieClient
  }

  lazy val jwtClient: HeaderClient = {
    val headerClient = new HeaderClient
    headerClient.setHeaderName(AUTHORIZATION_HEADER)
    headerClient.setPrefixHeader(BEARER_HEADER_PREFIX)
    headerClient.setAuthenticator(JwtAuthenticatorHelper.parse(config.getConfig("pac4j.lagom.jwt.authenticator")))
    headerClient.setName(HEADER_JWT_CLIENT)
    headerClient
  }

  lazy val client: HeaderClient = {
    val headerClient = new HeaderClient(AUTHORIZATION_HEADER, new Authenticator[Credentials]() {
      override def validate(credentials: Credentials, webContext: WebContext): Unit = {
        val profile = new CommonProfile()
        profile.setId(credentials.asInstanceOf[TokenCredentials].getToken)
        credentials.setUserProfile(profile)
      }
    })
    headerClient.setName(HEADER_CLIENT)
    headerClient
  }

  lazy val serviceConfig: Config = {
    val config = new Config(client, jwtClient, cookieClient)
    config.getClients.setDefaultSecurityClients(client.getName)
    config.addAuthorizer("_anonymous_", isAnonymous[CommonProfile])
    config.addAuthorizer("_authenticated_", isAuthenticated[CommonProfile])
    config
  }

}
