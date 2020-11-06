package org.pac4j.lagom.scaladsl.transport

import com.lightbend.lagom.scaladsl.api.transport.RequestHeader
import com.nimbusds.jwt.JWT
import org.pac4j.core.context.HttpConstants.{AUTHORIZATION_HEADER, BEARER_HEADER_PREFIX}

/**
  * Helper functions for [[RequestHeader]].
  *
  * @author Sergey Morgunov
  * @since 2.2.1
  */
object RequestHeaderHelper {

  /**
    * Puts `Authorization` header to [[RequestHeader]].
    *
    * @param jwt JWT
    */
  def authorizationBearer(jwt: String): RequestHeader => RequestHeader = header => header.authorizationBearer(jwt)

  /**
    * Puts `Authorization` header to [[RequestHeader]].
    *
    * @param jwt JWT
    */
  def authorizationBearer(jwt: JWT): RequestHeader => RequestHeader = header => header.authorizationBearer(jwt)

  /**
    * Copies specified header from sourced [[RequestHeader]] to target.
    *
    * @param source sourced [[RequestHeader]]
    * @param name   name of copying header
    */
  def forwardHeader(source: RequestHeader, name: String): RequestHeader => RequestHeader = header => header.forwardHeader(source, name)

  /**
    * Copies `Authorization` header from sourced [[RequestHeader]] to target.
    *
    * @param source sourced [[RequestHeader]]
    */
  def forwardAuthorization(source: RequestHeader): RequestHeader => RequestHeader = header => header.forwardAuthorization(source)

  implicit class Pac4jRequestHeader(header: RequestHeader) {

    /**
      * Puts `Authorization` header to [[RequestHeader]].
      *
      * @param jwt JWT
      * @return [[RequestHeader]] with specified `Authorization` header
      */
    def authorizationBearer(jwt: String): RequestHeader = header.withHeader(AUTHORIZATION_HEADER, BEARER_HEADER_PREFIX + jwt)

    /**
      * Puts `Authorization` header to [[RequestHeader]].
      *
      * @param jwt JWT
      * @return [[RequestHeader]] with specified `Authorization` header
      */
    def authorizationBearer(jwt: JWT): RequestHeader = authorizationBearer(jwt.serialize())

    /**
      * Copies specified header from sourced [[RequestHeader]] to target.
      *
      * @param source sourced [[RequestHeader]]
      * @param name   name of copying header
      */
    def forwardHeader(source: RequestHeader, name: String): RequestHeader =
      source.getHeader(name).map(value => header.withHeader(name, value)).getOrElse(header)

    /**
      * Copies `Authorization` header from sourced [[RequestHeader]] to target.
      *
      * @param source sourced [[RequestHeader]]
      */
    def forwardAuthorization(source: RequestHeader): RequestHeader = header.forwardHeader(source, AUTHORIZATION_HEADER)
  }
}
