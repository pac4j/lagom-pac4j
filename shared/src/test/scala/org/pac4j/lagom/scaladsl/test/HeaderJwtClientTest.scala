package org.pac4j.lagom.scaladsl.test

import com.lightbend.lagom.scaladsl.api.transport.RequestHeader
import com.lightbend.lagom.scaladsl.server.{LagomApplication, LagomServer, LocalServiceLocator}
import com.lightbend.lagom.scaladsl.testkit.ServiceTest
import com.lightbend.lagom.scaladsl.testkit.ServiceTest.TestServer
import com.softwaremill.macwire.wire
import org.pac4j.core.context.HttpConstants.{AUTHORIZATION_HEADER, BEARER_HEADER_PREFIX}
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

    "authenticate by JWT with RSA signature" in {
      val jwt = "eyJraWQiOiIxMjMiLCJhbGciOiJSUzI1NiJ9.eyJzdWIiOiJBbGljZSIsIm5iZiI6MTU0MTA4OTAxNSwiaXNzIjoiaHR0cHM6XC9cL3BhYzRqLm9yZyIsImlhdCI6MTU0MTA4OTAxNSwianRpIjoiOWI5YTU2MTUtOWEzYi00MDE5LWE5NWUtMjNmNzUxMDQzNzUxIn0.S0JQXt0JkZV6MgTrsbue8piucnKYfrJr8i-jQJCKBHwGwXg10F2c9y0W6nviRzAGfGn5KPV_TXnPyG4-3_Mw4i7IM0f88nO8-lhBiB4DFolzL89tV4ci8LcZwGpuXmzY1AmuszNaSY04glSh6dujVCFiXfjYW9TyiDpgLb_BQA3_9opkORFkNyZJbrvFqVGtFga_qmp5VZ358jXSgvlmq_aS7w9glkgWEg3ubR5AgYFhe-g4Lwu2KJfOu_Hv7nceYoVF3MEm4Zal1KfKO10VyS_oe_WdIDKmRCaciWiPXNaGLTFxgB9FjScBktWWGxL7SKmjdrKn6QyjkhsfPSs4qw"
      service.headerJwtAuthenticate.handleRequestHeader((header: RequestHeader) => header.withHeader(AUTHORIZATION_HEADER, BEARER_HEADER_PREFIX + jwt)).invoke.map { result =>
        result should ===("Alice")
      }
    }

    "authenticate by JWT with RSA encryption" in {
      val jwt = "eyJlbmMiOiJBMTI4R0NNIiwiYWxnIjoiUlNBLU9BRVAtMjU2In0.ZtHvEPrARdBqYUIYHtK8KGrAU9-Fc6G3r5VJOTow2Rdg5e7jqjJD0TJrcH9VMNELunEB4ldWOc8ynx1eflU8kMAOG6nk95YxEFeNh2erZobQ1GHTxJIgAo6Kq4nvTA_B8EzZWZ_xK9s4nD9Cs048-WsmQ0x6D7Sfh15aeDiT0hu962u5Q5lVxk4IjoGIHH6mRJmLioafBf_B6rEniLgYQ604uCh-AZv31VimFfNT8SS64sbSfAumUYv82yujY1CS8fWG4fQtwTWdtxCW8Odey9t8_Po96H59pgNoQasX_vIqPNfQDeEjm-l4eQWh7g-zAOqU27CPTyypOiuPayOUSg.dFG4GHhr2Pwdf3-l.rM8Fj80yrDRvPyawHzrCg4C6tkklcTOfhYreFriJSBFmrlm2xDCdWuguiKL5MYXIhkW2aHQcah0goh1Twmtpydzv69H1G-YvJF29neSCZ75HsdSw0pEWZ1rDWvksZgLs-kFt8pQcJ08HaZmIbLKX6-c8IYLIvIlFYH4.CQHTjX-wk-XMWQhPnB2WVA"
      service.headerJwtAuthenticate.handleRequestHeader((header: RequestHeader) => header.withHeader(AUTHORIZATION_HEADER, BEARER_HEADER_PREFIX + jwt)).invoke.map { result =>
        result should ===("Alice")
      }
    }

    "authenticate by JWT with Secret signature" in {
      val jwt = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJBbGljZSIsIm5iZiI6MTU0MTA5MTI5OCwiaXNzIjoiaHR0cHM6XC9cL3BhYzRqLm9yZyIsImlhdCI6MTU0MTA5MTI5OCwianRpIjoiNTk4MzFlNmMtY2I1YS00MmMzLTk3MzYtZmNmNTliZTBhNTIyIn0.EuBjHIa0ysslHnieYAAG_EHwHsrUNxydggL8vWAzQ10"
      service.headerJwtAuthenticate.handleRequestHeader((header: RequestHeader) => header.withHeader(AUTHORIZATION_HEADER, BEARER_HEADER_PREFIX + jwt)).invoke.map { result =>
        result should ===("Alice")
      }
    }

    "authenticate by JWT with Secret encryption" in {
      val jwt = "eyJlbmMiOiJBMjU2R0NNIiwiYWxnIjoiZGlyIn0..tHJg5VSzlWbcnl7H.vXBjEB9Cx_PgNK1IGDJDIaPsuGzNlAZN1KltGP65sHjgWXuKw6JJ0HFJldgZAWH53fdO_6PbG2jUkfF6Ncw6aoN2LKvLEGTv-TWImL7LXzlbiu12SerUHdFCU76HZu2yHD6_C6pWrBIo5e5YQKYS6cHYATxiiAjHVPw.JCfMQsrDP9vqRyAY5chYtw"
      service.headerJwtAuthenticate.handleRequestHeader((header: RequestHeader) => header.withHeader(AUTHORIZATION_HEADER, BEARER_HEADER_PREFIX + jwt)).invoke.map { result =>
        result should ===("Alice")
      }
    }

    "authenticate by JWT with EC signature" in {
      val jwt = "eyJhbGciOiJFUzI1NiJ9.eyJzdWIiOiJBbGljZSIsIm5iZiI6MTU0MTA5MjYzMiwiaXNzIjoiaHR0cHM6XC9cL3BhYzRqLm9yZyIsImlhdCI6MTU0MTA5MjYzMiwianRpIjoiZjZhNDRmMTgtNWE3NS00MDFkLWE3MDktYzM0OTMzZjE5NTQwIn0.GCsjK1wJoxGKAYV_tD7bNKYfOhywBtlQMDu57AcT80eOjeQTvaaKuYLp0YIizFKheghcm-jcKAFCKMNg7HUEfw"
      service.headerJwtAuthenticate.handleRequestHeader((header: RequestHeader) => header.withHeader(AUTHORIZATION_HEADER, BEARER_HEADER_PREFIX + jwt)).invoke.map { result =>
        result should ===("Alice")
      }
    }

    "authenticate by JWT with EC encryption" in {
      val jwt = "eyJlcGsiOnsia3R5IjoiRUMiLCJjcnYiOiJQLTI1NiIsIngiOiJNTFRrSnRGZWxtd2JwcWxWRlUySmhpUHd0WDJWWGJPS2pEdHYzWkNuaGs4IiwieSI6Imd2ajVhakNhNGpVMDhXb2lvOW9wc3JHNHV2dUJuSnd1cnZZUTQ2SFUxajAifSwiZW5jIjoiQTI1NkdDTSIsImFsZyI6IkVDREgtRVMrQTI1NktXIn0.nuRCyYnJ6gSFn2R1RKZlrUjcO8DClzJ1_7W2DyTmpLV1XSsf_OjKyg.MJmorKCjW7s5YkJ7.K_vab1A2adSVZxTu5fA-X0YV7pGUxaYHTB5vcE7fZf9MjnNYP6QZIJvIKPjvnjW-0QX-LDn-BLYhMKn7Bn5q7yFiWXUMIENG1_1k2JADCsvy3Rirc5dOrSoOVNLbAvZEEcxH_D4NUQ8RLmtHRtHzsisULFYK6fgT43A.wrNSVD2Hj3CAaoSY0RvxSg"
      service.headerJwtAuthenticate.handleRequestHeader((header: RequestHeader) => header.withHeader(AUTHORIZATION_HEADER, BEARER_HEADER_PREFIX + jwt)).invoke.map { result =>
        result should ===("Alice")
      }
    }

  }

  override protected def beforeAll(): Unit = server

  override protected def afterAll(): Unit = server.stop()

}
