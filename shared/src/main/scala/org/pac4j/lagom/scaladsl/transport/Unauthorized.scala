package org.pac4j.lagom.scaladsl.transport

import com.lightbend.lagom.scaladsl.api.transport.{ExceptionMessage, TransportErrorCode, TransportException}

/**
  * Exception thrown when a service call is unauthorized.
  *
  * @author Sergey Morgunov
  * @since 2.2.0
  */
final class Unauthorized(errorCode: TransportErrorCode, exceptionMessage: ExceptionMessage, cause: Throwable)
    extends TransportException(errorCode, exceptionMessage, cause) {
  def this(errorCode: TransportErrorCode, exceptionMessage: ExceptionMessage) = this(errorCode, exceptionMessage, null)
}

object Unauthorized {
  val ErrorCode: TransportErrorCode = TransportErrorCode(401, 4401, "Unauthorized")

  def apply(message: String) = new Unauthorized(
    ErrorCode,
    new ExceptionMessage(classOf[Unauthorized].getSimpleName, message),
    null
  )

  def apply(exceptionMessage: ExceptionMessage) = new Unauthorized(
    ErrorCode,
    exceptionMessage,
    null
  )

  def apply(cause: Throwable) = new Unauthorized(
    ErrorCode,
    new ExceptionMessage(classOf[Unauthorized].getSimpleName, cause.getMessage),
    cause
  )
}

