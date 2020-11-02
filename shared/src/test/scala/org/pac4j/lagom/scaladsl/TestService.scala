package org.pac4j.lagom.scaladsl

import akka.NotUsed
import com.lightbend.lagom.scaladsl.api.{Descriptor, Service, ServiceCall}
import com.lightbend.lagom.scaladsl.api.Service.{named, pathCall}

/**
  * Descriptor of Lagom service for tests.
  *
  * @author Vladimir Kornyshev
  * @since 1.0.0
  */
trait TestService extends Service {

  def defaultAuthenticate: ServiceCall[NotUsed, String]
  def defaultAuthorize: ServiceCall[NotUsed, String]
  def defaultAuthorizeByRole: ServiceCall[NotUsed, String]
  def defaultAuthorizeConfig: ServiceCall[NotUsed, String]

  def cookieAuthenticate: ServiceCall[NotUsed, String]
  def cookieAuthorize: ServiceCall[NotUsed, String]
  def cookieAuthorizeConfig: ServiceCall[NotUsed, String]

  def headerAuthenticate: ServiceCall[NotUsed, String]
  def headerAuthorize: ServiceCall[NotUsed, String]
  def headerAuthorizeConfig: ServiceCall[NotUsed, String]

  def headerJwtAuthenticate: ServiceCall[NotUsed, String]

  override def descriptor: Descriptor = named("default").withCalls(
    pathCall("/default/authenticate", this.defaultAuthenticate),
    pathCall("/default/authorize", this.defaultAuthorize),
    pathCall("/default/authorize/role", this.defaultAuthorizeByRole),
    pathCall("/default/authorize/config", this.defaultAuthorizeConfig),
    pathCall("/cookie/authenticate", this.cookieAuthenticate),
    pathCall("/cookie/authorize", this.cookieAuthorize),
    pathCall("/cookie/authorize/config", this.cookieAuthorizeConfig),
    pathCall("/header/authenticate", this.headerAuthenticate),
    pathCall("/header/authorize", this.headerAuthorize),
    pathCall("/header/authorize/config", this.headerAuthorizeConfig),
    pathCall("/header/jwt/authenticate", this.headerJwtAuthenticate)
  )
    .withAutoAcl(true)
    .withExceptionSerializer(Pac4jExceptionSerializer())

}
