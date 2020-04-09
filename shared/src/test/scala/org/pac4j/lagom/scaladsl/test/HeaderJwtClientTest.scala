package org.pac4j.lagom.scaladsl.test

import java.util.{Date, UUID}

import com.lightbend.lagom.scaladsl.server.{LagomApplication, LagomServer, LocalServiceLocator}
import com.lightbend.lagom.scaladsl.testkit.ServiceTest
import com.lightbend.lagom.scaladsl.testkit.ServiceTest.TestServer
import com.nimbusds.jwt.JWTClaimsSet
import com.softwaremill.macwire.wire
import com.typesafe.config.ConfigFactory
import org.pac4j.core.profile.CommonProfile
import org.pac4j.jwt.profile.JwtGenerator
import org.pac4j.lagom.jwt.JwtGeneratorHelper
import org.pac4j.lagom.scaladsl.transport.RequestHeaderHelper.authorizationBearer
import org.pac4j.lagom.scaladsl.{TestModule, TestService, TestServiceImpl}
import org.scalatest.{AsyncWordSpec, BeforeAndAfterAll, Matchers}
import play.api.libs.ws.ahc.AhcWSComponents

/**
  * Test of security logic for {@link org.pac4j.http.client.direct.HeaderClient}
  * with {@link org.pac4j.jwt.credentials.authenticator.JwtAuthenticator}.
  *
  * @author Sergey Morgunov
  * @since 1.0.0
  */
class HeaderJwtClientTest extends AsyncWordSpec with Matchers with BeforeAndAfterAll {

  private val ALICE_CLAIMS = new JWTClaimsSet.Builder()
    .issuer("https://pac4j.org")
    .subject("Alice")
    .issueTime(new Date)
    .jwtID(UUID.randomUUID.toString)
    .build
    .getClaims

  lazy val server: TestServer[LagomApplication with LocalServiceLocator with AhcWSComponents] = {
    ServiceTest.startServer(ServiceTest.defaultSetup.withCluster(false)) { ctx =>
      new LagomApplication(ctx) with LocalServiceLocator with AhcWSComponents with TestModule {
        override def lagomServer: LagomServer = serverFor[TestService](wire[TestServiceImpl])
      }
    }
  }

  lazy val service: TestService = server.serviceClient.implement[TestService]

  private var octSignJwtGenerator: JwtGenerator[CommonProfile] = _
  private var rsaSignJwtGenerator: JwtGenerator[CommonProfile] = _
  private var ecSignJwtGenerator: JwtGenerator[CommonProfile] = _

  private var octEncryptJwtGenerator: JwtGenerator[CommonProfile] = _
  private var rsaEncryptJwtGenerator: JwtGenerator[CommonProfile] = _
  private var ecEncryptJwtGenerator: JwtGenerator[CommonProfile] = _


  "TestService" should {

    "authenticate by JWT with RSA signature" in {
      val jwt = rsaSignJwtGenerator.generate(ALICE_CLAIMS)
      service.headerJwtAuthenticate.handleRequestHeader(authorizationBearer(jwt)).invoke.map { result =>
        result should ===("Alice")
      }
    }

    "authenticate by JWT with RSA encryption" in {
      val jwt = rsaEncryptJwtGenerator.generate(ALICE_CLAIMS)
      service.headerJwtAuthenticate.handleRequestHeader(authorizationBearer(jwt)).invoke.map { result =>
        result should ===("Alice")
      }
    }

    "authenticate by JWT with Secret signature" in {
      val jwt = octSignJwtGenerator.generate(ALICE_CLAIMS)
      service.headerJwtAuthenticate.handleRequestHeader(authorizationBearer(jwt)).invoke.map { result =>
        result should ===("Alice")
      }
    }

    "authenticate by JWT with Secret encryption" in {
      val jwt = octEncryptJwtGenerator.generate(ALICE_CLAIMS)
      service.headerJwtAuthenticate.handleRequestHeader(authorizationBearer(jwt)).invoke.map { result =>
        result should ===("Alice")
      }
    }

    "authenticate by JWT with EC signature" in {
      val jwt = ecSignJwtGenerator.generate(ALICE_CLAIMS)
      service.headerJwtAuthenticate.handleRequestHeader(authorizationBearer(jwt)).invoke.map { result =>
        result should ===("Alice")
      }
    }

    "authenticate by JWT with EC encryption and default signature" in {
      val jwt = ecEncryptJwtGenerator.generate(ALICE_CLAIMS)
      service.headerJwtAuthenticate.handleRequestHeader(authorizationBearer(jwt)).invoke.map { result =>
        result should ===("Alice")
      }
    }

  }

  override protected def beforeAll(): Unit = {
    server
    val config = ConfigFactory.load
    octSignJwtGenerator = JwtGeneratorHelper.parse(config.getConfig("pac4j.lagom.jwt.generator.sign.oct"))
    rsaSignJwtGenerator = JwtGeneratorHelper.parse(config.getConfig("pac4j.lagom.jwt.generator.sign.rsa"))
    ecSignJwtGenerator = JwtGeneratorHelper.parse(config.getConfig("pac4j.lagom.jwt.generator.sign.ec"))

    octEncryptJwtGenerator = JwtGeneratorHelper.parse(config.getConfig("pac4j.lagom.jwt.generator.encrypt.oct"))
    rsaEncryptJwtGenerator = JwtGeneratorHelper.parse(config.getConfig("pac4j.lagom.jwt.generator.encrypt.rsa"))
    ecEncryptJwtGenerator = JwtGeneratorHelper.parse(config.getConfig("pac4j.lagom.jwt.generator.encrypt.ec"))
  }

  override protected def afterAll(): Unit = server.stop()

}
