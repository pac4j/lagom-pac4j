package org.pac4j.lagom.scaladsl

import org.pac4j.core.config.Config
import org.pac4j.core.context.HttpConstants.AUTHORIZATION_HEADER
import org.pac4j.core.context.WebContext
import org.pac4j.core.credentials.authenticator.Authenticator
import org.pac4j.core.credentials.{Credentials, TokenCredentials}
import org.pac4j.core.profile.CommonProfile
import org.pac4j.http.client.direct.HeaderClient

import org.pac4j.core.authorization.authorizer.IsAnonymousAuthorizer.isAnonymous
import org.pac4j.core.authorization.authorizer.IsAuthenticatedAuthorizer.isAuthenticated

/**
  * DI module for run tests.
  *
  * @author Vladimir Kornyshev
  * @since 1.0.0
  */
trait TestModule {

  lazy val client: HeaderClient = {
    val headerClient = new HeaderClient(AUTHORIZATION_HEADER, new Authenticator[Credentials]() {
      override def validate(credentials: Credentials, webContext: WebContext): Unit = {
        val profile = new CommonProfile()
        profile.setId(credentials.asInstanceOf[TokenCredentials].getToken)
        credentials.setUserProfile(profile)
      }
    })
    headerClient.setName(ClientNames.HEADER_CLIENT)
    headerClient
  }

  lazy val serviceConfig: Config = {
    val config = new Config(client)
    config.getClients.setDefaultSecurityClients(client.getName)
    config.addAuthorizer("_anonymous_", isAnonymous())
    config.addAuthorizer("_authenticated_", isAuthenticated())
    config
  }

}
