package org.pac4j.lagom.scaladsl.test

import com.lightbend.lagom.scaladsl.api.transport.RequestHeader
import com.lightbend.lagom.scaladsl.server.{LagomApplication, LagomServer, LocalServiceLocator}
import com.lightbend.lagom.scaladsl.testkit.ServiceTest
import com.lightbend.lagom.scaladsl.testkit.ServiceTest.TestServer
import com.softwaremill.macwire.wire
import org.pac4j.lagom.scaladsl.transport.Unauthorized
import org.pac4j.lagom.scaladsl.{TestModule, TestService, TestServiceImpl}
import org.scalatest.{AsyncWordSpec, BeforeAndAfterAll, Matchers}
import play.api.http.HeaderNames.COOKIE
import play.api.libs.ws.ahc.AhcWSComponents

/**
  * Test of security logic for simple {@link org.pac4j.http.client.direct.CookieClient}.
  *
  * @author Sergey Morgunov
  * @since 1.0.1
  */
class CookieClientTest extends AsyncWordSpec with Matchers with BeforeAndAfterAll {

  lazy val server: TestServer[LagomApplication with LocalServiceLocator with AhcWSComponents] = {
    ServiceTest.startServer(ServiceTest.defaultSetup.withCluster(false)) { ctx =>
      new LagomApplication(ctx) with LocalServiceLocator with AhcWSComponents with TestModule {
        override def lagomServer: LagomServer = LagomServer.forService(
          bindService[TestService].to(wire[TestServiceImpl])
        )
      }
    }
  }

  lazy val service: TestService = server.serviceClient.implement[TestService]

  "TestService" should {

    "authenticate by anonymous" in {
      service.cookieAuthenticate.invoke.map { result =>
        result should ===("anonymous")
      }
    }

    "authenticate by Alice" in {
      service.cookieAuthenticate.handleRequestHeader((header: RequestHeader) => header.withHeader(COOKIE, "auth=Alice; aaa=bbb")).invoke.map { result =>
        result should ===("Alice")
      }
    }

    "not authorize by anonymous" in {
      service.cookieAuthorize.invoke.map { result =>
        fail("authorize by anonymous should be forbidden")
      } recoverWith {
        case f: Unauthorized =>
          f.getMessage should ===("Unauthorized")
      }
    }

    "authorize by Alice" in {
      service.cookieAuthorize.handleRequestHeader((header: RequestHeader) => header.withHeader(COOKIE, "aaa=bbb; auth=Alice;")).invoke.map { result =>
        result should ===("Alice")
      }
    }

    "not authorize by anonymous (authorizer from config)" in {
      service.cookieAuthorizeConfig.invoke.map { result =>
        fail("authorize by anonymous should be forbidden")
      } recoverWith {
        case f: Unauthorized =>
          f.getMessage should ===("Unauthorized")
      }
    }

    "authorize by Alice (authorizer from config)" in {
      service.cookieAuthorizeConfig.handleRequestHeader((header: RequestHeader) => header.withHeader(COOKIE, "auth=Alice; aaa=bbb")).invoke.map { result =>
        result should ===("Alice")
      }
    }

  }

  override protected def beforeAll(): Unit = server

  override protected def afterAll(): Unit = server.stop()

}
