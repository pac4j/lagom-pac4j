package org.pac4j.lagom.scaladsl.test

import com.lightbend.lagom.scaladsl.api.transport.{Forbidden, RequestHeader}
import com.lightbend.lagom.scaladsl.server.{LagomApplication, LagomServer, LocalServiceLocator}
import com.lightbend.lagom.scaladsl.testkit.ServiceTest
import com.lightbend.lagom.scaladsl.testkit.ServiceTest.TestServer
import com.softwaremill.macwire.wire
import org.pac4j.core.context.HttpConstants.AUTHORIZATION_HEADER
import org.pac4j.lagom.scaladsl.transport.Unauthorized
import org.pac4j.lagom.scaladsl.{TestModule, TestService, TestServiceImpl}
import org.scalatest.{AsyncWordSpec, BeforeAndAfterAll, Matchers}
import play.api.libs.ws.ahc.AhcWSComponents

/**
  * Test of security logic for simple {@link org.pac4j.http.client.direct.HeaderClient}.
  *
  * @author Vladimir Kornyshev
  * @since 1.0.0
  */
class HeaderClientTest extends AsyncWordSpec with Matchers with BeforeAndAfterAll {

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
      service.headerAuthenticate.invoke.map { result =>
        result should ===("anonymous")
      }
    }

    "authenticate by Alice" in {
      service.headerAuthenticate.handleRequestHeader((header: RequestHeader) => header.withHeader(AUTHORIZATION_HEADER, "Alice")).invoke.map { result =>
        result should ===("Alice")
      }
    }

    "not authorize by anonymous" in {
      service.headerAuthorize.invoke.map { result =>
        fail("authorize by anonymous should be forbidden")
      } recoverWith {
        case f: Unauthorized =>
          f.getMessage should ===("Unauthorized")
      }
    }

    "authorize by Alice" in {
      service.headerAuthorize.handleRequestHeader((header: RequestHeader) => header.withHeader(AUTHORIZATION_HEADER, "Alice")).invoke.map { result =>
        result should ===("Alice")
      }
    }

    "not authorize by anonymous (authorizer from config)" in {
      service.headerAuthorizeConfig.invoke.map { result =>
        fail("authorize by anonymous should be forbidden")
      } recoverWith {
        case f: Unauthorized =>
          f.getMessage should ===("Unauthorized")
      }
    }

    "authorize by Alice (authorizer from config)" in {
      service.headerAuthorizeConfig.handleRequestHeader((header: RequestHeader) => header.withHeader(AUTHORIZATION_HEADER, "Alice")).invoke.map { result =>
        result should ===("Alice")
      }
    }

  }

  override protected def beforeAll(): Unit = server

  override protected def afterAll(): Unit = server.stop()

}
