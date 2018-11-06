package org.pac4j.lagom.javadsl.test;

import com.lightbend.lagom.javadsl.testkit.ServiceTest;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.pac4j.lagom.javadsl.TestService;

import java.util.concurrent.ExecutionException;

import static com.lightbend.lagom.javadsl.testkit.ServiceTest.defaultSetup;
import static com.lightbend.lagom.javadsl.testkit.ServiceTest.startServer;
import static org.assertj.core.api.Assertions.assertThat;
import static org.pac4j.core.context.HttpConstants.AUTHORIZATION_HEADER;
import static org.pac4j.core.context.HttpConstants.BEARER_HEADER_PREFIX;

/**
 * Test of security logic for {@link org.pac4j.http.client.direct.HeaderClient}
 * with {@link org.pac4j.jwt.credentials.authenticator.JwtAuthenticator}.
 *
 * @author Sergey Morgunov
 * @since 1.0.0
 */
class HeaderJwtClientTest {

    private static ServiceTest.TestServer server;

    private static TestService service;

    @BeforeAll
    static void beforeAll() {
        server = startServer(defaultSetup().withCluster(false));
        service = server.client(TestService.class);
    }

    @AfterAll
    static void afterAll() {
        if (server != null) server.stop();
    }

    @Test
    @DisplayName("authenticate by JWT with RSA signature")
    void testJwtRSASignature() throws ExecutionException, InterruptedException {
        String jwt = "eyJraWQiOiIxMjMiLCJhbGciOiJSUzI1NiJ9.eyJzdWIiOiJBbGljZSIsIm5iZiI6MTU0MTA4OTAxNSwiaXNzIjoiaHR0cHM6XC9cL3BhYzRqLm9yZyIsImlhdCI6MTU0MTA4OTAxNSwianRpIjoiOWI5YTU2MTUtOWEzYi00MDE5LWE5NWUtMjNmNzUxMDQzNzUxIn0.S0JQXt0JkZV6MgTrsbue8piucnKYfrJr8i-jQJCKBHwGwXg10F2c9y0W6nviRzAGfGn5KPV_TXnPyG4-3_Mw4i7IM0f88nO8-lhBiB4DFolzL89tV4ci8LcZwGpuXmzY1AmuszNaSY04glSh6dujVCFiXfjYW9TyiDpgLb_BQA3_9opkORFkNyZJbrvFqVGtFga_qmp5VZ358jXSgvlmq_aS7w9glkgWEg3ubR5AgYFhe-g4Lwu2KJfOu_Hv7nceYoVF3MEm4Zal1KfKO10VyS_oe_WdIDKmRCaciWiPXNaGLTFxgB9FjScBktWWGxL7SKmjdrKn6QyjkhsfPSs4qw";
        String result = service.headerJwtAuthenticate()
                .handleRequestHeader(header -> header.withHeader(AUTHORIZATION_HEADER, BEARER_HEADER_PREFIX.concat(jwt)))
                .invoke().toCompletableFuture().get();
        assertThat(result).isEqualTo("Alice");
    }

    @Test
    @DisplayName("authenticate by JWT with RSA encryption")
    void testJwtRSAEncryption() throws ExecutionException, InterruptedException {
        String jwt = "eyJlbmMiOiJBMTI4R0NNIiwiYWxnIjoiUlNBLU9BRVAtMjU2In0.ZtHvEPrARdBqYUIYHtK8KGrAU9-Fc6G3r5VJOTow2Rdg5e7jqjJD0TJrcH9VMNELunEB4ldWOc8ynx1eflU8kMAOG6nk95YxEFeNh2erZobQ1GHTxJIgAo6Kq4nvTA_B8EzZWZ_xK9s4nD9Cs048-WsmQ0x6D7Sfh15aeDiT0hu962u5Q5lVxk4IjoGIHH6mRJmLioafBf_B6rEniLgYQ604uCh-AZv31VimFfNT8SS64sbSfAumUYv82yujY1CS8fWG4fQtwTWdtxCW8Odey9t8_Po96H59pgNoQasX_vIqPNfQDeEjm-l4eQWh7g-zAOqU27CPTyypOiuPayOUSg.dFG4GHhr2Pwdf3-l.rM8Fj80yrDRvPyawHzrCg4C6tkklcTOfhYreFriJSBFmrlm2xDCdWuguiKL5MYXIhkW2aHQcah0goh1Twmtpydzv69H1G-YvJF29neSCZ75HsdSw0pEWZ1rDWvksZgLs-kFt8pQcJ08HaZmIbLKX6-c8IYLIvIlFYH4.CQHTjX-wk-XMWQhPnB2WVA";
        String result = service.headerJwtAuthenticate()
                .handleRequestHeader(header -> header.withHeader(AUTHORIZATION_HEADER, BEARER_HEADER_PREFIX.concat(jwt)))
                .invoke().toCompletableFuture().get();
        assertThat(result).isEqualTo("Alice");
    }

    @Test
    @DisplayName("authenticate by JWT with Secret signature")
    void testJwtSecretSignature() throws ExecutionException, InterruptedException {
        String jwt = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJBbGljZSIsIm5iZiI6MTU0MTA5MTI5OCwiaXNzIjoiaHR0cHM6XC9cL3BhYzRqLm9yZyIsImlhdCI6MTU0MTA5MTI5OCwianRpIjoiNTk4MzFlNmMtY2I1YS00MmMzLTk3MzYtZmNmNTliZTBhNTIyIn0.EuBjHIa0ysslHnieYAAG_EHwHsrUNxydggL8vWAzQ10";
        String result = service.headerJwtAuthenticate()
                .handleRequestHeader(header -> header.withHeader(AUTHORIZATION_HEADER, BEARER_HEADER_PREFIX.concat(jwt)))
                .invoke().toCompletableFuture().get();
        assertThat(result).isEqualTo("Alice");
    }

    @Test
    @DisplayName("authenticate by JWT with Secret encryption")
    void testJwtSecretEncryption() throws ExecutionException, InterruptedException {
        String jwt = "eyJlbmMiOiJBMjU2R0NNIiwiYWxnIjoiZGlyIn0..tHJg5VSzlWbcnl7H.vXBjEB9Cx_PgNK1IGDJDIaPsuGzNlAZN1KltGP65sHjgWXuKw6JJ0HFJldgZAWH53fdO_6PbG2jUkfF6Ncw6aoN2LKvLEGTv-TWImL7LXzlbiu12SerUHdFCU76HZu2yHD6_C6pWrBIo5e5YQKYS6cHYATxiiAjHVPw.JCfMQsrDP9vqRyAY5chYtw";
        String result = service.headerJwtAuthenticate()
                .handleRequestHeader(header -> header.withHeader(AUTHORIZATION_HEADER, BEARER_HEADER_PREFIX.concat(jwt)))
                .invoke().toCompletableFuture().get();
        assertThat(result).isEqualTo("Alice");
    }

    @Test
    @DisplayName("authenticate by JWT with EC signature")
    void testJwtECSignature() throws ExecutionException, InterruptedException {
        String jwt = "eyJhbGciOiJFUzI1NiJ9.eyJzdWIiOiJBbGljZSIsIm5iZiI6MTU0MTA5MjYzMiwiaXNzIjoiaHR0cHM6XC9cL3BhYzRqLm9yZyIsImlhdCI6MTU0MTA5MjYzMiwianRpIjoiZjZhNDRmMTgtNWE3NS00MDFkLWE3MDktYzM0OTMzZjE5NTQwIn0.GCsjK1wJoxGKAYV_tD7bNKYfOhywBtlQMDu57AcT80eOjeQTvaaKuYLp0YIizFKheghcm-jcKAFCKMNg7HUEfw";
        String result = service.headerJwtAuthenticate()
                .handleRequestHeader(header -> header.withHeader(AUTHORIZATION_HEADER, BEARER_HEADER_PREFIX.concat(jwt)))
                .invoke().toCompletableFuture().get();
        assertThat(result).isEqualTo("Alice");
    }

    @Test
    @DisplayName("authenticate by JWT with EC encryption")
    void testJwtECEncryption() throws ExecutionException, InterruptedException {
        String jwt = "eyJlcGsiOnsia3R5IjoiRUMiLCJjcnYiOiJQLTI1NiIsIngiOiJNTFRrSnRGZWxtd2JwcWxWRlUySmhpUHd0WDJWWGJPS2pEdHYzWkNuaGs4IiwieSI6Imd2ajVhakNhNGpVMDhXb2lvOW9wc3JHNHV2dUJuSnd1cnZZUTQ2SFUxajAifSwiZW5jIjoiQTI1NkdDTSIsImFsZyI6IkVDREgtRVMrQTI1NktXIn0.nuRCyYnJ6gSFn2R1RKZlrUjcO8DClzJ1_7W2DyTmpLV1XSsf_OjKyg.MJmorKCjW7s5YkJ7.K_vab1A2adSVZxTu5fA-X0YV7pGUxaYHTB5vcE7fZf9MjnNYP6QZIJvIKPjvnjW-0QX-LDn-BLYhMKn7Bn5q7yFiWXUMIENG1_1k2JADCsvy3Rirc5dOrSoOVNLbAvZEEcxH_D4NUQ8RLmtHRtHzsisULFYK6fgT43A.wrNSVD2Hj3CAaoSY0RvxSg";
        String result = service.headerJwtAuthenticate()
                .handleRequestHeader(header -> header.withHeader(AUTHORIZATION_HEADER, BEARER_HEADER_PREFIX.concat(jwt)))
                .invoke().toCompletableFuture().get();
        assertThat(result).isEqualTo("Alice");
    }

}
