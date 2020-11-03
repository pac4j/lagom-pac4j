package org.pac4j.lagom.scaladsl

import com.lightbend.lagom.scaladsl.api.deser.DefaultExceptionSerializer
import com.lightbend.lagom.scaladsl.api.transport.{ExceptionMessage, TransportErrorCode}
import org.pac4j.lagom.scaladsl.transport.Unauthorized
import play.api.{Environment, Mode}

/**
  * Serializer for unauthorized exception.
  *
  * @author Sergey Morgunov
  * @since 2.2.0
  */
class Pac4jExceptionSerializer(environment: Environment) extends DefaultExceptionSerializer(environment) {
  override protected def fromCodeAndMessage(transportErrorCode: TransportErrorCode, exceptionMessage: ExceptionMessage): Throwable =
    if (Unauthorized.ErrorCode.http == transportErrorCode.http) Unauthorized(exceptionMessage)
    else super.fromCodeAndMessage(transportErrorCode, exceptionMessage)
}

object Pac4jExceptionSerializer {
  def apply(environment: Environment = Environment.simple(mode = Mode.Prod)) = new Pac4jExceptionSerializer(environment)
}
