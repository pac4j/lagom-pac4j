package org.pac4j.lagom.javadsl.test;

import com.lightbend.lagom.javadsl.api.transport.Forbidden;
import com.lightbend.lagom.javadsl.testkit.ServiceTest.TestServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.pac4j.lagom.javadsl.TestService;

import java.util.concurrent.ExecutionException;

import static com.lightbend.lagom.javadsl.testkit.ServiceTest.defaultSetup;
import static com.lightbend.lagom.javadsl.testkit.ServiceTest.startServer;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.pac4j.core.context.HttpConstants.AUTHORIZATION_HEADER;

/**
 * Test of security logic for default client ({@link org.pac4j.http.client.direct.HeaderClient}).
 *
 * @author Sergey Morgunov
 * @since 1.0.0
 */
class DefaultClientTest {

    private static TestServer server;

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
    @DisplayName("authenticate by anonymous")
    void testAuthenticateAnonymous() throws ExecutionException, InterruptedException {
        String result = service.defaultAuthenticate().invoke().toCompletableFuture().get();
        assertThat(result).isEqualTo("anonymous");
    }

    @Test
    @DisplayName("authenticate by Alice")
    void testAuthenticateProfile() throws ExecutionException, InterruptedException {
        String result = service.defaultAuthenticate()
                .handleRequestHeader(header -> header.withHeader(AUTHORIZATION_HEADER, "Alice"))
                .invoke().toCompletableFuture().get();
        assertThat(result).isEqualTo("Alice");
    }

    @Test
    @DisplayName("authorize by anonymous")
    void testAuthorizeAnonymous() {
        Throwable thrown = catchThrowable(() -> service.defaultAuthorize().invoke().toCompletableFuture().get());
        assertThat(thrown).hasCauseExactlyInstanceOf(Forbidden.class);
        assertThat(thrown.getCause()).hasMessage("Authorization failed");
    }

    @Test
    @DisplayName("authorize by Alice")
    void testAuthorizeProfile() throws ExecutionException, InterruptedException {
        String result = service.defaultAuthorize()
                .handleRequestHeader(header -> header.withHeader(AUTHORIZATION_HEADER, "Alice"))
                .invoke().toCompletableFuture().get();
        assertThat(result).isEqualTo("Alice");
    }

    @Test
    @DisplayName("authorize by anonymous (authorizer from config)")
    void testAuthorizeConfigAnonymous() {
        Throwable thrown = catchThrowable(() -> service.defaultAuthorizeConfig().invoke().toCompletableFuture().get());
        assertThat(thrown).hasCauseExactlyInstanceOf(Forbidden.class);
        assertThat(thrown.getCause()).hasMessage("Authorization failed");
    }

    @Test
    @DisplayName("authorize by Alice (authorizer from config)")
    void testAuthorizeConfigProfile() throws ExecutionException, InterruptedException {
        String result = service.defaultAuthorizeConfig()
                .handleRequestHeader(header -> header.withHeader(AUTHORIZATION_HEADER, "Alice"))
                .invoke().toCompletableFuture().get();
        assertThat(result).isEqualTo("Alice");
    }

}
