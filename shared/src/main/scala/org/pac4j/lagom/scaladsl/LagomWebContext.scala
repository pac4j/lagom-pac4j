package org.pac4j.lagom.scaladsl

import java.net.HttpCookie
import java.util
import java.util.Collections.emptyList

import com.lightbend.lagom.scaladsl.api.transport.RequestHeader
import org.pac4j.core.context.session.SessionStore
import org.pac4j.core.context.{Cookie, WebContext}
import org.pac4j.core.exception.TechnicalException
import play.api.http.HeaderNames.COOKIE
import play.core.netty.utils.ServerCookieDecoder

import scala.collection.JavaConversions._

/**
  * <p>Implementation web context of PAC4J for Lagom framework.</p>
  * <p>Context is immutable and the {@link SessionStore} is not supported.</p>
  *
  * @author Vladimir Kornyshev
  * @since 1.0.0
  */
class LagomWebContext(requestHeader: RequestHeader) extends WebContext {

  override def getSessionStore: SessionStore[_ <: WebContext] = throw new TechnicalException("Operation not supported")

  override def getRequestParameter(s: String): String = throw new TechnicalException("Operation not supported")

  override def getRequestParameters: util.Map[String, Array[String]] = throw new TechnicalException("Operation not supported")

  override def getRequestAttribute(s: String): AnyRef = throw new TechnicalException("Operation not supported")

  override def setRequestAttribute(s: String, o: Any): Unit = throw new TechnicalException("Operation not supported")

  override def getRequestHeader(name: String): String = requestHeader.getHeader(name) match {
    case Some(value) => value
    case None => throw new TechnicalException(s"Header $name not found")
  }

  override def getRequestMethod: String = requestHeader.method.name

  override def getRemoteAddr: String = throw new TechnicalException("Operation not supported")

  override def writeResponseContent(s: String): Unit = throw new TechnicalException("Operation not supported")

  override def setResponseStatus(i: Int): Unit = throw new TechnicalException("Operation not supported")

  override def setResponseHeader(s: String, s1: String): Unit = {}

  override def setResponseContentType(s: String): Unit = throw new TechnicalException("Operation not supported")

  override def getServerName: String = throw new TechnicalException("Operation not supported")

  override def getServerPort: Int = throw new TechnicalException("Operation not supported")

  override def getScheme: String = throw new TechnicalException("Operation not supported")

  override def isSecure: Boolean = false

  override def getFullRequestURL: String = throw new TechnicalException("Operation not supported")

  override def getRequestCookies: util.Collection[Cookie] = requestHeader.getHeader(COOKIE) match {
    case Some(cookies) => for (cookie <- ServerCookieDecoder.STRICT.decode(cookies)) yield new Cookie(cookie.name(), cookie.value())
    case None => emptyList()
  }

  override def addResponseCookie(cookie: Cookie): Unit = throw new TechnicalException("Operation not supported")

  override def getPath: String = requestHeader.uri.getPath

}
